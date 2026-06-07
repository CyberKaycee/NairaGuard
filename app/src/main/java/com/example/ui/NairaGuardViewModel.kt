package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiParser
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class NairaGuardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = NairaGuardRepository(database.nairaGuardDao())

    // --- State Expositions ---
    val commodities: StateFlow<List<Commodity>> = repository.allCommodities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prices: StateFlow<List<MarketPrice>> = repository.allPrices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<PriceHistory>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<PriceAlert>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subscription: StateFlow<SubscriptionState> = repository.subscriptionState
        .map { it ?: SubscriptionState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SubscriptionState())

    val feedbackList: StateFlow<List<UserFeedback>> = repository.allFeedback
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- UI State Management ---
    var currentScreen = MutableStateFlow(Screen.DASHBOARD)
    var selectedCategory = MutableStateFlow("All")
    var searchQuery = MutableStateFlow("")

    // Ingest Agent Simulator State
    var agentInputText = MutableStateFlow(
        "Mile 12: Premium Rice bag wholesale is now 92,000 naira. Also white garri bag is 43000 naira"
    )
    var isAgentParsing = MutableStateFlow(false)
    var agentParseResult = MutableStateFlow<String?>(null)
    var agentLastParsedUpdate = MutableStateFlow<List<com.example.api.ParsedUpdate>>(emptyList())

    // Margin calculator premium state
    var calcSelectedCommodity = MutableStateFlow<Commodity?>(null)
    var calcPurchasePrice = MutableStateFlow(85000.0)
    var calcSellingPricePerUnit = MutableStateFlow(800.0)
    var calcTransportCost = MutableStateFlow(3000.0)
    var calcOtherCosts = MutableStateFlow(1500.0)

    // SMS Notifications Simulation Logs
    var smsAlertLogs = MutableStateFlow<List<String>>(emptyList())

    // Mock Users / Registrations Analytics for Admin center
    val mockUsers = listOf(
        MockUser("Yusuf Ibrahim", "Mile 12", "PLUS (Active)", "2026-05-28", "active"),
        MockUser("Chioma Okafor", "Isale Eko", "PLUS (Active)", "2026-05-27", "active"),
        MockUser("Bimbo Alao", "Ikorodu", "Free Tier", "2026-05-29", "inactive"),
        MockUser("Emeka Paul", "Lekki", "PLUS (Trial)", "2026-05-30", "trial"),
        MockUser("Fatima Umar", "Ketu", "PLUS (Expired)", "2026-05-20", "expired"),
        MockUser("Sesan Balogun", "Agege", "Free Tier", "2026-05-25", "inactive"),
        MockUser("Tunde Bakare", "Badagry", "PLUS (Active)", "2026-05-12", "active")
    )

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // --- Actions ---

    fun changeScreen(screen: Screen) {
        currentScreen.value = screen
    }

    fun activatePlusTrial() {
        viewModelScope.launch {
            val trialDuration = 7 * 24 * 60 * 60 * 1000L
            val state = SubscriptionState(
                id = 1,
                tier = "PLUS",
                status = "trial",
                trialStartDate = System.currentTimeMillis(),
                expiryDate = System.currentTimeMillis() + trialDuration,
                dailySmsAlertEnabled = true
            )
            repository.updateSubscription(state)
            addSmsLog("Welcome to NairaGuard Plus! Your 7-day Free Trial is active. Custom alerts, margin calculators and arbitrage tools unlocked.")
        }
    }

    fun subscribePlusMonthly() {
        viewModelScope.launch {
            val oneMonth = 30 * 24 * 60 * 60 * 1000L
            val state = SubscriptionState(
                id = 1,
                tier = "PLUS",
                status = "active",
                trialStartDate = null,
                expiryDate = System.currentTimeMillis() + oneMonth,
                dailySmsAlertEnabled = true
            )
            repository.updateSubscription(state)
            addSmsLog("NairaGuard PLUS Premium monthly subscription activated! 3,000 NGN processed. Full access and custom SMS alerts enabled.")
        }
    }

    fun cancelSubscription() {
        viewModelScope.launch {
            val state = SubscriptionState(
                id = 1,
                tier = "FREE",
                status = "inactive",
                trialStartDate = null,
                expiryDate = null,
                dailySmsAlertEnabled = false
            )
            repository.updateSubscription(state)
            addSmsLog("NairaGuard subscription canceled. Downgraded to FREE standard Lagos pricing plan.")
        }
    }

    fun toggleSmsAlerts(enabled: Boolean) {
        viewModelScope.launch {
            val current = subscription.value
            repository.updateSubscription(current.copy(dailySmsAlertEnabled = enabled))
            if (enabled) {
                addSmsLog("Daily market price SMS alert logs initiated for Lagos food outlets.")
            }
        }
    }

    fun updateSmsPhone(num: String) {
        viewModelScope.launch {
            val current = subscription.value
            repository.updateSubscription(current.copy(phoneNumber = num))
        }
    }

    // --- Ingestion SMS Parsing via Gemini API ---
    fun runIntelligentAgent() {
        val input = agentInputText.value
        if (input.trim().isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            isAgentParsing.value = true
            agentParseResult.value = "Ingest superagent reading message..."

            // Parse text updates
            val parsedResult = GeminiParser.parseUpdate(input)
            
            if (parsedResult != null) {
                // Find matching commodity in db to get true ID
                val resolution = repository.getCommodityByNameAndLocation(
                    parsedResult.commodity, 
                    parsedResult.market
                )

                if (resolution != null) {
                    val (commodity, marketName) = resolution
                    
                    // Let's retrieve existing record or create new
                    val matchedPriceObj = repository.getPriceForCommodityAndMarket(commodity.id, marketName)
                    val updatedPriceObj = if (matchedPriceObj != null) {
                        matchedPriceObj.copy(
                            wholesalePrice = parsedResult.price,
                            retailPrice = parsedResult.price / commodity.conversionFactor,
                            lastUpdated = System.currentTimeMillis(),
                            updatedBy = "WhatsApp Superagent"
                        )
                    } else {
                        MarketPrice(
                            commodityId = commodity.id,
                            marketLocation = marketName,
                            wholesalePrice = parsedResult.price,
                            retailPrice = parsedResult.price / commodity.conversionFactor,
                            lastUpdated = System.currentTimeMillis(),
                            updatedBy = "WhatsApp Superagent"
                        )
                    }

                    // Write to database & price history
                    repository.updateMarketPrice(updatedPriceObj)

                    agentParseResult.value = """
                        Successfully Parsed & Ingested!
                        ------------------------------------
                        Commodity: ${commodity.name} (${commodity.wholesaleUnit})
                        Market: $marketName
                        Wholesale Price: ₦ ${formatNaira(parsedResult.price)}
                        Mapped Sub-unit Price: ₦ ${formatNaira(updatedPriceObj.retailPrice)} per ${commodity.microUnit}
                        Last updated: Just now
                    """.trimIndent()

                    // Add to custom list
                    val currentList = agentLastParsedUpdate.value.toMutableList()
                    currentList.add(0, parsedResult)
                    agentLastParsedUpdate.value = currentList

                    // Check live custom alerts for triggers!
                    checkPriceAlertTriggers(commodity, marketName, updatedPriceObj.retailPrice)

                } else {
                    agentParseResult.value = "Error: Found commodity '${parsedResult.commodity}' but it does not map cleanly to the registered listing schema."
                }
            } else {
                agentParseResult.value = "Error: Superagent was unable to parse commodity types or price targets from this source text. Try specifying a clearer format."
            }
            isAgentParsing.value = false
        }
    }

    // Check alerts and add to SMS logs
    private fun checkPriceAlertTriggers(commodity: Commodity, marketName: String, currentRetailPrice: Double) {
        viewModelScope.launch {
            val alertsList = alerts.value
            alertsList.forEach { alert ->
                if (!alert.isTriggered && alert.commodityId == commodity.id && alert.marketLocation == marketName) {
                    val trigger = if (alert.isAbove) {
                        currentRetailPrice >= alert.targetPrice
                    } else {
                        currentRetailPrice <= alert.targetPrice
                    }

                    if (trigger) {
                        repository.markAlertTriggered(alert.id)
                        val conditionStr = if (alert.isAbove) "higher than" else "lower than"
                        val formattedPrice = formatNaira(alert.targetPrice)
                        val currentPriceFormatted = formatNaira(currentRetailPrice)
                        addSmsLog(
                            "ALERT TRIGGERED 🚨: [${commodity.name} - $marketName] retail price " +
                            "is now ₦$currentPriceFormatted per ${commodity.microUnit}, which is $conditionStr your limit of ₦$formattedPrice!"
                        )
                    }
                }
            }
        }
    }

    // --- Custom Alert Actions ---
    fun addPriceAlert(commodityId: Int, market: String, targetPrice: Double, isAbove: Boolean) {
        viewModelScope.launch {
            val alert = PriceAlert(
                commodityId = commodityId,
                marketLocation = market,
                targetPrice = targetPrice,
                isAbove = isAbove
            )
            repository.insertAlert(alert)
        }
    }

    fun removePriceAlert(alertId: Int) {
        viewModelScope.launch {
            repository.deleteAlert(alertId)
        }
    }

    // --- Admin Manual Ingestion ---
    fun adminUpdatePrice(commodityId: Int, market: String, wholesalePrice: Double) {
        viewModelScope.launch {
            val commoditiesList = commodities.value
            val comm = commoditiesList.find { it.id == commodityId } ?: return@launch

            val matchedPrice = repository.getPriceForCommodityAndMarket(commodityId, market)
            val updatedPrice = if (matchedPrice != null) {
                matchedPrice.copy(
                    wholesalePrice = wholesalePrice,
                    retailPrice = wholesalePrice / comm.conversionFactor,
                    lastUpdated = System.currentTimeMillis(),
                    updatedBy = "Admin Panel (Manual)"
                )
            } else {
                MarketPrice(
                    commodityId = commodityId,
                    marketLocation = market,
                    wholesalePrice = wholesalePrice,
                    retailPrice = wholesalePrice / comm.conversionFactor,
                    lastUpdated = System.currentTimeMillis(),
                    updatedBy = "Admin Panel (Manual)"
                )
            }

            repository.updateMarketPrice(updatedPrice)
            checkPriceAlertTriggers(comm, market, updatedPrice.retailPrice)
        }
    }

    fun adminUpdateConversion(commodityId: Int, factor: Double) {
        viewModelScope.launch {
            repository.updateConversionFactor(commodityId, factor)
        }
    }

    // --- User Feedback Handling ---
    fun submitUserFeedback(
        feedbackType: String,
        commodityId: Int?,
        commodityName: String,
        marketLocation: String,
        reportedWholesalePrice: Double,
        reportedRetailPrice: Double,
        message: String
    ) {
        viewModelScope.launch {
            val feedback = UserFeedback(
                feedbackType = feedbackType,
                commodityId = commodityId,
                commodityName = commodityName,
                marketLocation = marketLocation,
                reportedWholesalePrice = reportedWholesalePrice,
                reportedRetailPrice = reportedRetailPrice,
                message = message,
                status = "Pending"
            )
            repository.insertFeedback(feedback)
            addSmsLog("SYSTEM: Received user feedback report: $feedbackType for $commodityName. Placed in Admin queue.")
        }
    }

    fun adminProcessFeedback(feedback: UserFeedback, approve: Boolean) {
        viewModelScope.launch {
            if (approve) {
                var resolvedId = feedback.commodityId
                val targetCommodityName = feedback.commodityName

                if (resolvedId == null) {
                    val newComm = Commodity(
                        name = targetCommodityName,
                        category = "Grains",
                        wholesaleUnit = "Unit",
                        microUnit = "Pc",
                        conversionFactor = 1.0,
                        description = "Community suggested brand new commodity"
                    )
                    database.nairaGuardDao().insertCommodities(listOf(newComm))
                    val refreshedList = database.nairaGuardDao().getAllCommoditiesList()
                    val saved = refreshedList.find { it.name.equals(targetCommodityName, ignoreCase = true) }
                    resolvedId = saved?.id
                }

                if (resolvedId != null) {
                    val currentPriceObj = repository.getPriceForCommodityAndMarket(resolvedId, feedback.marketLocation)
                    val newPriceObj = if (currentPriceObj != null) {
                        currentPriceObj.copy(
                            wholesalePrice = feedback.reportedWholesalePrice,
                            retailPrice = feedback.reportedRetailPrice,
                            lastUpdated = System.currentTimeMillis(),
                            updatedBy = "User Feedback (Admin Approved)"
                        )
                    } else {
                        MarketPrice(
                            commodityId = resolvedId,
                            marketLocation = feedback.marketLocation,
                            wholesalePrice = feedback.reportedWholesalePrice,
                            retailPrice = feedback.reportedRetailPrice,
                            lastUpdated = System.currentTimeMillis(),
                            updatedBy = "User Feedback"
                        )
                    }
                    repository.updateMarketPrice(newPriceObj)
                    
                    val updatedCommList = database.nairaGuardDao().getAllCommoditiesList()
                    val associatedComm = updatedCommList.find { it.id == resolvedId }
                    if (associatedComm != null) {
                        checkPriceAlertTriggers(associatedComm, feedback.marketLocation, feedback.reportedRetailPrice)
                    }
                }

                repository.updateFeedbackStatus(feedback.id, "Integrated")
                addSmsLog("ADMIN: Approved and Integrated feedback #${feedback.id} for $targetCommodityName at ${feedback.marketLocation}")
            } else {
                repository.updateFeedbackStatus(feedback.id, "Dismissed")
                addSmsLog("ADMIN: Dismissed feedback #${feedback.id} for ${feedback.commodityName}")
            }
        }
    }

    fun adminDeleteFeedback(feedbackId: Int) {
        viewModelScope.launch {
            repository.deleteFeedback(feedbackId)
        }
    }

    // --- UI Utilities ---
    fun formatNaira(amount: Double): String {
        return try {
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            formatter.maximumFractionDigits = 2
            formatter.minimumFractionDigits = 2
            formatter.format(amount)
        } catch (e: Exception) {
            String.format("%.2f", amount)
        }
    }

    fun addSmsLog(log: String) {
        val list = smsAlertLogs.value.toMutableList()
        list.add(0, "[${System.currentTimeMillis().toLocalShortTime()}] $log")
        smsAlertLogs.value = list
    }

    private fun Long.toLocalShortTime(): String {
        val date = java.util.Date(this)
        val format = java.text.SimpleDateFormat("HH:mm:ss", Locale.US)
        return format.format(date)
    }
}

enum class Screen {
    DASHBOARD,
    ARBITRAGE,
    MARGIN_CALC,
    ALERTS,
    SMS_AGENT,
    ADMIN,
    SUBSCRIPTION
}

data class MockUser(
    val name: String,
    val market: String,
    val subscription: String,
    val registerDate: String,
    val status: String
)
