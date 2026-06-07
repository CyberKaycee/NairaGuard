package com.example.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException

class NairaGuardRepository(private val dao: NairaGuardDao) {

    val allCommodities: Flow<List<Commodity>> = dao.getAllCommodities()
    val allPrices: Flow<List<MarketPrice>> = dao.getAllMarketPrices()
    val allHistory: Flow<List<PriceHistory>> = dao.getAllPriceHistory()
    val allAlerts: Flow<List<PriceAlert>> = dao.getAllAlerts()
    val subscriptionState: Flow<SubscriptionState?> = dao.getSubscriptionState()
    val allFeedback: Flow<List<UserFeedback>> = dao.getAllFeedback()

    suspend fun insertFeedback(feedback: UserFeedback) = dao.insertFeedback(feedback)
    suspend fun updateFeedbackStatus(id: Int, status: String) = dao.updateFeedbackStatus(id, status)
    suspend fun deleteFeedback(id: Int) = dao.deleteFeedback(id)


    fun getPricesForCommodity(commodityId: Int): Flow<List<MarketPrice>> =
        dao.getPricesForCommodity(commodityId)

    fun getHistoryForCommodity(commodityId: Int): Flow<List<PriceHistory>> =
        dao.getHistoryForCommodity(commodityId)

    suspend fun getPricesForCommodityList(commodityId: Int): List<MarketPrice> =
        dao.getPricesForCommodityList(commodityId)

    suspend fun getPriceForCommodityAndMarket(commodityId: Int, market: String): MarketPrice? =
        dao.getPriceForCommodityAndMarket(commodityId, market)

    suspend fun updateMarketPrice(marketPrice: MarketPrice) {
        dao.insertMarketPrice(marketPrice)
        // Add to history
        dao.insertHistory(
            PriceHistory(
                commodityId = marketPrice.commodityId,
                marketLocation = marketPrice.marketLocation,
                wholesalePrice = marketPrice.wholesalePrice,
                retailPrice = marketPrice.retailPrice,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateConversionFactor(commodityId: Int, factor: Double) {
        val commodities = dao.getAllCommoditiesList()
        val c = commodities.find { it.id == commodityId }
        if (c != null) {
            val updated = c.copy(conversionFactor = factor)
            dao.updateCommodity(updated)
            
            // Recalculate retail prices for all matching market prices
            val prices = dao.getPricesForCommodityList(commodityId)
            prices.forEach { price ->
                val newRetail = price.wholesalePrice / factor
                dao.insertMarketPrice(price.copy(retailPrice = newRetail, lastUpdated = System.currentTimeMillis(), updatedBy = "System (Conversion Update)"))
            }
        }
    }

    suspend fun insertAlert(alert: PriceAlert) = dao.insertAlert(alert)
    suspend fun deleteAlert(id: Int) = dao.deleteAlert(id)
    suspend fun markAlertTriggered(id: Int) = dao.markAlertTriggered(id)

    suspend fun updateSubscription(state: SubscriptionState) =
        dao.insertOrUpdateSubscription(state)

    suspend fun getSubscriptionStateOnce(): SubscriptionState? = dao.getSubscriptionStateOnce()

    suspend fun getCommodityByNameAndLocation(commodityNameQuery: String, locationQuery: String): Pair<Commodity, String>? {
        val commodities = dao.getAllCommoditiesList()
        
        // Find best match for commodity name
        val matchedComm = commodities.find { 
            it.name.contains(commodityNameQuery, ignoreCase = true) || 
            commodityNameQuery.contains(it.name, ignoreCase = true) 
        } ?: commodities.find {
            it.description.contains(commodityNameQuery, ignoreCase = true)
        }

        if (matchedComm == null) return null

        // Find best match for market location
        val resolvedMarket = when {
            locationQuery.contains("mile", ignoreCase = true) || locationQuery.contains("12", ignoreCase = true) -> "Mile 12 (Mainland)"
            locationQuery.contains("ikorodu", ignoreCase = true) -> "Ikorodu (Mainland)"
            locationQuery.contains("eko", ignoreCase = true) || locationQuery.contains("isale", ignoreCase = true) -> "Isale Eko (Island)"
            locationQuery.contains("lekki", ignoreCase = true) -> "Lekki (Island)"
            locationQuery.contains("island", ignoreCase = true) -> "Isale Eko (Island)"
            else -> "Mile 12 (Mainland)" // Default mainland hub
        }

        return Pair(matchedComm, resolvedMarket)
    }

    suspend fun seedDatabaseIfEmpty() {
        // Seeding checks
        val commoditiesList = dao.getAllCommoditiesList()
        if (commoditiesList.isNotEmpty()) {
            Log.d("NairaGuardRepo", "Database already seeded with ${commoditiesList.size} commodities.")
            return
        }

        Log.d("NairaGuardRepo", "Seeding database with 20 Nigerian commodities, geographical prices, and history...")

        val baseCommodities = listOf(
            Commodity(name = "Premium Rice", category = "Grains", wholesaleUnit = "50kg Bag", microUnit = "Cup", conversionFactor = 128.0, description = "Imported double polished parboiled long grain rice"),
            Commodity(name = "Oloyin Beans", category = "Beans", wholesaleUnit = "50kg Bag", microUnit = "Derica", conversionFactor = 60.0, description = "Sweet brown honey beans from northern Nigeria"),
            Commodity(name = "White Garri", category = "Processed Tubers", wholesaleUnit = "50kg Bag", microUnit = "Paint Bucket", conversionFactor = 12.0, description = "Finely processed cassava flakes"),
            Commodity(name = "Yellow Garri", category = "Processed Tubers", wholesaleUnit = "50kg Bag", microUnit = "Paint Bucket", conversionFactor = 12.0, description = "Cassava flakes fried with red palm oil"),
            Commodity(name = "Groundnut Oil", category = "Oils", wholesaleUnit = "25L Jerrican", microUnit = "1L Bottle", conversionFactor = 25.0, description = "Refined vegetable peanut cooking oil"),
            Commodity(name = "Palm Oil", category = "Oils", wholesaleUnit = "25L Jerrican", microUnit = "1L Bottle", conversionFactor = 25.0, description = "Locally processed organic red palm oil"),
            Commodity(name = "Granulated Sugar", category = "Packaged Goods", wholesaleUnit = "50kg Bag", microUnit = "Cup", conversionFactor = 150.0, description = "Refined white granulated cane sugar"),
            Commodity(name = "Dangote Cement", category = "Building Materials", wholesaleUnit = "50kg Bag", microUnit = "1kg Scoop", conversionFactor = 50.0, description = "Dangote 3X premium 42.5 grade cement"),
            Commodity(name = "Local Beef", category = "Meats", wholesaleUnit = "Quarter Side (50kg)", microUnit = "1kg Piece", conversionFactor = 50.0, description = "Freshly butchered grass-fed local beef"),
            Commodity(name = "Tuber Yam", category = "Tubers", wholesaleUnit = "100 Tubers (Heap)", microUnit = "1 Tuber", conversionFactor = 100.0, description = "Premium medium sized Abuja yams"),
            Commodity(name = "Red Onions", category = "Vegetables", wholesaleUnit = "100kg Bag", microUnit = "Plate", conversionFactor = 25.0, description = "Sharp red onions imported from Aliero, Kebbi"),
            Commodity(name = "Round Tomatoes", category = "Vegetables", wholesaleUnit = "40kg Raffia Basket", microUnit = "Derica", conversionFactor = 40.0, description = "Red plum tomatoes from northern irrigation farms"),
            Commodity(name = "Rodo Pepper", category = "Vegetables", wholesaleUnit = "30kg Raffia Basket", microUnit = "Paint Bucket", conversionFactor = 10.0, description = "Very hot scotch bonnet habanero pepper"),
            Commodity(name = "Titus Mackerel", category = "Seafood", wholesaleUnit = "20kg Carton", microUnit = "1kg Piece", conversionFactor = 20.0, description = "Frozen imported premium mackerel (Titus) fish"),
            Commodity(name = "Agege Bread", category = "Bakery", wholesaleUnit = "Crate of 30 Loaves", microUnit = "1 Loaf", conversionFactor = 30.0, description = "Freshly baked local unsliced Lagos sweet bread"),
            Commodity(name = "Plantain", category = "Fruits", wholesaleUnit = "Large Bunch (20 Fingers)", microUnit = "1 Finger", conversionFactor = 20.0, description = "Freshly cut local Nigerian plantain hands"),
            Commodity(name = "Refined Salt", category = "Packaged Goods", wholesaleUnit = "Carton of 45 Packs", microUnit = "1 Sachet (250g)", conversionFactor = 45.0, description = "Iodized Mr. Chef table salt"),
            Commodity(name = "Chicken Eggs", category = "Livestock", wholesaleUnit = "Crate of 30 Eggs", microUnit = "1 Egg", conversionFactor = 30.0, description = "Fresh farm large brown chicken eggs"),
            Commodity(name = "Peak Powdered Milk", category = "Dairy", wholesaleUnit = "Carton of 72 Sachets", microUnit = "1 Sachet (14g)", conversionFactor = 72.0, description = "Highly nutritious full cream instant powdered milk"),
            Commodity(name = "Yellow Maize", category = "Grains", wholesaleUnit = "50kg Bag", microUnit = "Paint Bucket", conversionFactor = 12.0, description = "Dried yellow maize corn grains"),
            Commodity(name = "Puna Yam (Large)", category = "Tubers", wholesaleUnit = "100 Tubers (Large)", microUnit = "1 Tuber", conversionFactor = 100.0, description = "Premium selected large Abuja eating yams"),
            Commodity(name = "Green Plantain", category = "Fruits", wholesaleUnit = "Large Bunch", microUnit = "1 Finger", conversionFactor = 24.0, description = "Unripe green cooking plantain bunch from Edo State"),
            Commodity(name = "Palm Kernel Oil", category = "Oils", wholesaleUnit = "25L Jerrican", microUnit = "1L Bottle", conversionFactor = 25.0, description = "Quality organic oil extracted from palm kernels"),
            Commodity(name = "Washing Detergent", category = "Household", wholesaleUnit = "Carton of 48 Packs", microUnit = "1 Pack", conversionFactor = 48.0, description = "Unilever active washing detergent powder packs"),
            Commodity(name = "Antiseptic Soap", category = "Household", wholesaleUnit = "Carton of 36 Bars", microUnit = "1 Bar", conversionFactor = 36.0, description = "Dettol herbal protective grooming bath soaps"),
            Commodity(name = "Indomie Noodles", category = "Packaged Goods", wholesaleUnit = "Carton of 40 Packs", microUnit = "1 Pack", conversionFactor = 40.0, description = "Indomie instant fried chicken noodles"),
            Commodity(name = "Cassava Flour (Lafun)", category = "Processed Tubers", wholesaleUnit = "50kg Bag", microUnit = "Paint Bucket", conversionFactor = 12.0, description = "Traditional white powdered cassava swallow flour"),
            Commodity(name = "Gino Tomato Paste", category = "Packaged Goods", wholesaleUnit = "Carton of 50 Sachets", microUnit = "1 Sachet", conversionFactor = 50.0, description = "Concentrated red triple pulp tomato paste sachet"),
            Commodity(name = "Local Ofada Rice", category = "Grains", wholesaleUnit = "50kg Bag", microUnit = "Paint Bucket", conversionFactor = 12.0, description = "Unpolished organic short grain local Ofada rice"),
            Commodity(name = "Household Bleach", category = "Household", wholesaleUnit = "Carton of 24 Bottles", microUnit = "1 Bottle", conversionFactor = 24.0, description = "Concentrated stain remover and bleach liquid bottles"),
            Commodity(name = "Toilet Roll (Soft)", category = "Household", wholesaleUnit = "Bundle of 48 Rolls", microUnit = "1 Roll", conversionFactor = 48.0, description = "Super soft high quality bathroom napkin tissue rolls"),
            Commodity(name = "Original Vaseline Jelly", category = "Household", wholesaleUnit = "Box of 24 Jars", microUnit = "1 Jar", conversionFactor = 24.0, description = "Vaseline brand moisturising petroleum jelly pots"),
            Commodity(name = "Dry Smoked Catfish", category = "Seafood", wholesaleUnit = "Carton of 10kg", microUnit = "1 Fish", conversionFactor = 25.0, description = "Traditional firewood smoked dry local catfish fingers"),
            Commodity(name = "Abuja Golden Beans", category = "Beans", wholesaleUnit = "50kg Bag", microUnit = "Derica", conversionFactor = 60.0, description = "Premium light brown Abuja honey beans variant"),
            Commodity(name = "Cocoa Beverages (Milo)", category = "Packaged Goods", wholesaleUnit = "Carton of 48 Sachets", microUnit = "1 Sachet", conversionFactor = 48.0, description = "Nestle Milo chocolate malt energy drink sachets")
        )

        dao.insertCommodities(baseCommodities)

        // Refetch to get IDs
        val savedComm = dao.getAllCommoditiesList()
        val markets = listOf(
            "Mile 12 (Mainland)" to 0.85,  // Base price multiple
            "Ikorodu (Mainland)" to 0.88,
            "Isale Eko (Island)" to 1.10,
            "Lekki (Island)" to 1.25
        )

        // Custom wholesale base prices in NGN (Mainland references)
        val wholesaleBases = mapOf(
            "Premium Rice" to 85000.0,
            "Oloyin Beans" to 110000.0,
            "White Garri" to 42000.0,
            "Yellow Garri" to 47000.0,
            "Groundnut Oil" to 58000.0,
            "Palm Oil" to 38000.0,
            "Granulated Sugar" to 95000.0,
            "Dangote Cement" to 8500.0,
            "Local Beef" to 180000.0,
            "Tuber Yam" to 220000.0,
            "Red Onions" to 75000.0,
            "Round Tomatoes" to 35000.0,
            "Rodo Pepper" to 30000.0,
            "Titus Mackerel" to 65000.0,
            "Agege Bread" to 15000.0,
            "Plantain" to 8000.0,
            "Refined Salt" to 14000.0,
            "Chicken Eggs" to 5400.0,
            "Peak Powdered Milk" to 18000.0,
            "Yellow Maize" to 34000.0,
            "Puna Yam (Large)" to 240000.0,
            "Green Plantain" to 9000.0,
            "Palm Kernel Oil" to 40000.0,
            "Washing Detergent" to 18000.0,
            "Antiseptic Soap" to 14400.0,
            "Indomie Noodles" to 7600.0,
            "Cassava Flour (Lafun)" to 32000.0,
            "Gino Tomato Paste" to 12500.0,
            "Local Ofada Rice" to 68000.0,
            "Household Bleach" to 9600.0,
            "Toilet Roll (Soft)" to 14400.0,
            "Original Vaseline Jelly" to 24000.0,
            "Dry Smoked Catfish" to 50000.0,
            "Abuja Golden Beans" to 80000.0,
            "Cocoa Beverages (Milo)" to 16000.0
        )

        val priceList = mutableListOf<MarketPrice>()
        val historyList = mutableListOf<PriceHistory>()

        // Generate current prices and historical price trails (stretching 4 periods back for charts)
        savedComm.forEach { comm ->
            val baseWholesale = wholesaleBases[comm.name] ?: 50000.0
            
            markets.forEach { (marketName, multiple) ->
                val finalWholesale = baseWholesale * multiple
                val finalRetail = finalWholesale / comm.conversionFactor
                
                priceList.add(
                    MarketPrice(
                        commodityId = comm.id,
                        marketLocation = marketName,
                        wholesalePrice = finalWholesale,
                        retailPrice = finalRetail,
                        lastUpdated = System.currentTimeMillis() - 1000 * 60, // 1 min ago
                        updatedBy = "Initial Import"
                    )
                )

                // Add 4-day trailing history to create wonderful volatility data
                val msPerDay = 24 * 60 * 60 * 1000L
                for (i in 4 downTo 1) {
                    // Introduce a random price swing of -10% to +10%
                    val randomChange = 1.0 + ((i % 3 - 1) * 0.05) + ((comm.id % 2 - 0.5) * 0.03)
                    val histWholesale = finalWholesale * randomChange
                    val histRetail = histWholesale / comm.conversionFactor
                    historyList.add(
                        PriceHistory(
                            commodityId = comm.id,
                            marketLocation = marketName,
                            wholesalePrice = histWholesale,
                            retailPrice = histRetail,
                            timestamp = System.currentTimeMillis() - i * msPerDay
                        )
                    )
                }
            }
        }

        dao.insertMarketPrices(priceList)
        dao.insertHistories(historyList)

        // Seed Default Subscription
        if (dao.getSubscriptionStateOnce() == null) {
            dao.insertOrUpdateSubscription(
                SubscriptionState(
                    id = 1,
                    tier = "FREE",
                    status = "inactive",
                    trialStartDate = null,
                    expiryDate = null,
                    dailySmsAlertEnabled = false,
                    phoneNumber = ""
                )
            )
        }
    }
}
