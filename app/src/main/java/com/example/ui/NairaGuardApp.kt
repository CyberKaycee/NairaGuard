package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NairaGuardApp(viewModel: NairaGuardViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val subState by viewModel.subscription.collectAsStateWithLifecycle()
    
    // Support adaptive layouts by using window dimensions
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NairaSuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "NG",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NairaGuard",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    if (subState.tier == "PLUS") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(AccentAmberBg)
                                .border(1.dp, AccentAmberBorder, RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF59E0B))
                                )
                                Text(
                                    text = "PLUS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentAmberText
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { viewModel.changeScreen(Screen.SUBSCRIPTION) }
                                .background(AccentPurpleBg)
                                .border(1.dp, AccentPurpleBorder, RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "GET PLUS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentPurpleText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = { viewModel.changeScreen(Screen.ADMIN) },
                        modifier = Modifier
                            .testTag("admin_nav_button")
                            .size(32.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Go to Admin Command Center",
                            modifier = Modifier.size(16.dp),
                            tint = if (currentScreen == Screen.ADMIN) NairaSuccessGreen else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SoftGrey,
                    titleContentColor = DeepCharcoal
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("app_bottom_bar"),
                containerColor = SoftGrey,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.DASHBOARD,
                    onClick = { viewModel.changeScreen(Screen.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Prices Dashboard") },
                    label = { Text("Discover") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = DeepCharcoal,
                        indicatorColor = AccentBlueBg,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_discover")
                )
                
                NavigationBarItem(
                    selected = currentScreen == Screen.ARBITRAGE,
                    onClick = { viewModel.changeScreen(Screen.ARBITRAGE) },
                    icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Geographic Arbitrage") },
                    label = { Text("Arbitrage") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = DeepCharcoal,
                        indicatorColor = AccentBlueBg,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_arbitrage")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.MARGIN_CALC,
                    onClick = { viewModel.changeScreen(Screen.MARGIN_CALC) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Margin Calculator") },
                    label = { Text("Margins") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = DeepCharcoal,
                        indicatorColor = AccentBlueBg,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_calculator")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.SMS_AGENT,
                    onClick = { viewModel.changeScreen(Screen.SMS_AGENT) },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Forward to Ingest Agent") },
                    label = { Text("AI Ingest") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = DeepCharcoal,
                        indicatorColor = AccentBlueBg,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_ai")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.SUBSCRIPTION,
                    onClick = { viewModel.changeScreen(Screen.SUBSCRIPTION) },
                    icon = { Icon(Icons.Default.AccountBox, contentDescription = "Alerts and Subscriptions") },
                    label = { Text("Plus Alert") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        selectedTextColor = DeepCharcoal,
                        indicatorColor = AccentBlueBg,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_plus")
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                label = "ScreenTransition",
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { screen ->
                when (screen) {
                    Screen.DASHBOARD -> DashboardView(viewModel)
                    Screen.ARBITRAGE -> ArbitrageView(viewModel)
                    Screen.MARGIN_CALC -> MarginCalculatorView(viewModel)
                    Screen.SMS_AGENT -> SuperAgentView(viewModel)
                    Screen.ADMIN -> AdminCommandView(viewModel)
                    Screen.SUBSCRIPTION -> SubscriptionView(viewModel)
                    Screen.ALERTS -> SubscriptionView(viewModel)
                }
            }
        }
    }
}

// ==========================================
// 1. DASHBOARD / DISCOVER VIEW
// ==========================================
// ==========================================
// 1. DASHBOARD / DISCOVER VIEW
// ==========================================
@Composable
fun DashboardView(viewModel: NairaGuardViewModel) {
    val commodities by viewModel.commodities.collectAsStateWithLifecycle()
    val prices by viewModel.prices.collectAsStateWithLifecycle()
    val subState by viewModel.subscription.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var activeCommodityForDetails by remember { mutableStateOf<Commodity?>(null) }
    var selectedMarketFilter by remember { mutableStateOf("All Lagos") }

    val categories = listOf("All", "Grains", "Beans", "Processed Tubers", "Oils", "Vegetables", "Meats", "Tubers", "Livestock", "Household", "Packaged Goods")

    // User Feedback Dialog Trigger states
    var showSubmissionDialog by remember { mutableStateOf(false) }
    var submissionDialogType by remember { mutableStateOf("Inaccuracy") } // "Inaccuracy" or "Suggestion"
    var submissionCommodityId by remember { mutableStateOf<Int?>(null) }
    var submissionCommodityName by remember { mutableStateOf("") }
    var submissionMarketLocation by remember { mutableStateOf("Mile 12 (Mainland)") }
    var submissionWholesalePrice by remember { mutableStateOf("") }
    var submissionRetailPrice by remember { mutableStateOf("") }
    var submissionMessage by remember { mutableStateOf("") }

    // Categories Filter
    val filteredCommodities = commodities.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
        (it.name.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightCream)
            .padding(12.dp)
    ) {
        // Welcoming small text to replace bulky card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Lagos Food Indices",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DeepCharcoal
                )
                Text(
                    text = "Protective price points cached from hubs",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "v1.2 Ingest",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurpleText,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AccentPurpleBg)
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }

        // Market segmented selector (Interactive)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All Lagos", "Mainland (Mile 12)", "Island (Isale Eko)").forEach { market ->
                val isSelected = selectedMarketFilter == market
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(if (isSelected) DeepCharcoal else SoftGrey)
                        .border(1.dp, if (isSelected) DeepCharcoal else BorderGrey, RoundedCornerShape(50))
                        .clickable { selectedMarketFilter = market }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = market,
                        color = if (isSelected) Color.White else Color(0xFF475569),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 3-Column Statistical widgets (Matched to High-Density HTML)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Volatility Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentBlueBg)
                    .border(1.dp, AccentBlueBorder, RoundedCornerShape(12.dp))
                    .padding(6.dp)
            ) {
                Column {
                    Text(
                        text = "AVG VOLATILITY",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "+4.2%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "today",
                        fontSize = 8.sp,
                        color = Color.Gray
                    )
                }
            }

            // Best Arbitrage Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentGreenBg)
                    .border(1.dp, AccentGreenBorder, RoundedCornerShape(12.dp))
                    .padding(6.dp)
            ) {
                Column {
                    Text(
                        text = "BEST ARBITRAGE",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = NairaSuccessGreen,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "Rice",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Isale Eko",
                        fontSize = 8.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Plus Trial Access Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentPurpleBg)
                    .border(1.dp, AccentPurpleBorder, RoundedCornerShape(12.dp))
                    .padding(6.dp)
            ) {
                Column {
                    Text(
                        text = "PLUS ACCESS",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentPurpleText,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(1.dp))
                    val isLocked = subState.tier != "PLUS"
                    Text(
                        text = if (isLocked) "7d Trial" else "Active",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = if (isLocked) "Locked" else "Premium",
                        fontSize = 8.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Search Bar (Compact Height)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .testTag("search_commodity_input"),
            placeholder = { Text("Search staple commodities...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search", modifier = Modifier.size(18.dp))
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NairaSuccessGreen,
                unfocusedBorderColor = BorderGrey,
                focusedContainerColor = SoftGrey,
                unfocusedContainerColor = SoftGrey
            )
        )

        // Categories Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { viewModel.selectedCategory.value = cat },
                    label = { Text(cat, fontSize = 11.sp) },
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
            }
        }

        // Spot something wrong or suggest new price point suggestion card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .clickable {
                    submissionDialogType = "Suggestion"
                    submissionCommodityId = null
                    submissionCommodityName = ""
                    submissionMarketLocation = "Mile 12 (Mainland)"
                    submissionWholesalePrice = ""
                    submissionRetailPrice = ""
                    submissionMessage = ""
                    showSubmissionDialog = true
                }
                .testTag("onboard_feedback_trigger"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Default.Feedback,
                    contentDescription = "Offer Feedback Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Spot a price error or missing staples?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Suggest new prices or commodities directly to NairaGuard.",
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("SUGGEST", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Real-Time Header with Pulse light
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "MARKET INDICES (REAL-TIME)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(NairaSuccessGreen)
                )
                Text(
                    text = "Live: 14:32 WAT",
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }

        // Price List
        if (filteredCommodities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = "No Commodities Found",
                        modifier = Modifier.size(48.dp),
                        tint = DeepCharcoal.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No commodities match search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredCommodities) { comm ->
                    val commPrices = prices.filter { it.commodityId == comm.id }
                    
                    // Filter based on active market selection row
                    val displayPrices = when (selectedMarketFilter) {
                        "Mainland (Mile 12)" -> commPrices.filter { it.marketLocation.contains("Mainland") || it.marketLocation.contains("Mile 12") }
                        "Island (Isale Eko)" -> commPrices.filter { it.marketLocation.contains("Island") || it.marketLocation.contains("Isale Eko") }
                        else -> commPrices
                    }

                    val avgWholesale = if (displayPrices.isNotEmpty()) displayPrices.map { it.wholesalePrice }.average() else {
                        if (commPrices.isNotEmpty()) commPrices.map { it.wholesalePrice }.average() else 0.0
                    }
                    val avgRetail = if (displayPrices.isNotEmpty()) displayPrices.map { it.retailPrice }.average() else {
                        if (commPrices.isNotEmpty()) commPrices.map { it.retailPrice }.average() else 0.0
                    }

                    CommodityPriceCard(
                        commodity = comm,
                        avgWholesale = avgWholesale,
                        avgRetail = avgRetail,
                        isPlus = subState.tier == "PLUS",
                        onDetailsClick = { activeCommodityForDetails = comm }
                    )
                }
            }
        }
    }

    // Detail Modal / BottomSheet
    activeCommodityForDetails?.let { comm ->
        val commPrices = prices.filter { it.commodityId == comm.id }
        val allHistory by viewModel.history.collectAsStateWithLifecycle()
        val commHistory = allHistory.filter { it.commodityId == comm.id }

        AlertDialog(
            onDismissRequest = { activeCommodityForDetails = null },
            confirmButton = {
                TextButton(onClick = { activeCommodityForDetails = null }) {
                    Text("Close")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Eco,
                        contentDescription = "Commodity Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(comm.name, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = comm.category.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = comm.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Unit map card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Wholesale Unit", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Text(comm.wholesaleUnit, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Conversion Arrow")
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Retail Unit", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                Text(
                                    "${comm.conversionFactor.roundToInt()} ${comm.microUnit}s / unit",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Trend Chart Container
                    Text("5-Day Wholesale Volatility Trend", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    if (commHistory.isNotEmpty()) {
                        NairaLineChart(
                            history = commHistory,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(vertical = 8.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Awaiting price history stream", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Geographic Price Arbitrage", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    if (subState.tier == "PLUS") {
                        // PREMIUM UNLOCKED
                        Column {
                            commPrices.forEach { price ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .drawBehind {
                                            val strokeWidth = 1.dp.toPx()
                                            drawLine(
                                                color = Color.LightGray.copy(alpha = 0.5f),
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = strokeWidth
                                            )
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(price.marketLocation, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text(
                                            "Source: ${price.updatedBy} · Last sync: ${System.currentTimeMillis().minus(price.lastUpdated).div(1000).div(60)}m ago",
                                            fontSize = 9.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            "₦${viewModel.formatNaira(price.wholesalePrice)} / ${comm.wholesaleUnit.split(" ")[0]}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            "₦${viewModel.formatNaira(price.retailPrice)} per ${comm.microUnit}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // PREMIUM LOCKED GATE
                        SubscriptionGate(
                            message = "Island vs. Mainland comparison details are locked under the PLUS plan."
                        ) {
                            viewModel.changeScreen(Screen.SUBSCRIPTION)
                            activeCommodityForDetails = null
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            submissionDialogType = "Inaccuracy"
                            submissionCommodityId = comm.id
                            submissionCommodityName = comm.name
                            submissionMarketLocation = commPrices.firstOrNull()?.marketLocation ?: "Mile 12 (Mainland)"
                            submissionWholesalePrice = ""
                            submissionRetailPrice = ""
                            submissionMessage = "Discrepancy reported for ${comm.name}."
                            showSubmissionDialog = true
                            activeCommodityForDetails = null
                        },
                        modifier = Modifier.fillMaxWidth().testTag("add_feedback_for_${comm.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TerracottaOrange),
                        border = BorderStroke(1.dp, TerracottaOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Report Error Status", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Report Price Inaccuracy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }

    if (showSubmissionDialog) {
        val commoditiesList by viewModel.commodities.collectAsStateWithLifecycle()
        
        AlertDialog(
            onDismissRequest = { showSubmissionDialog = false },
            title = {
                Text(
                    text = if (submissionDialogType == "Inaccuracy") "⚠️ Report Price Inaccuracy" else "💡 Suggest Pricing or Staples",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val wholesale = submissionWholesalePrice.toDoubleOrNull() ?: 0.0
                        val retail = submissionRetailPrice.toDoubleOrNull() ?: 0.0
                        if (submissionCommodityName.isNotBlank() && wholesale > 0) {
                            val computedRetail = if (retail > 0) retail else {
                                val assocComm = commoditiesList.find { it.id == submissionCommodityId }
                                val factor = assocComm?.conversionFactor ?: 12.0
                                wholesale / factor
                            }
                            viewModel.submitUserFeedback(
                                feedbackType = submissionDialogType,
                                commodityId = submissionCommodityId,
                                commodityName = submissionCommodityName,
                                marketLocation = submissionMarketLocation,
                                reportedWholesalePrice = wholesale,
                                reportedRetailPrice = computedRetail,
                                message = submissionMessage
                            )
                            showSubmissionDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NairaSuccessGreen),
                    enabled = submissionCommodityName.isNotBlank() && submissionWholesalePrice.isNotEmpty() && (submissionWholesalePrice.toDoubleOrNull() ?: 0.0) > 0,
                    modifier = Modifier.testTag("submit_vendor_feedback_btn")
                ) {
                    Text("Submit Report", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmissionDialog = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Feedback Type Segmented Control
                    Text("Feedback Type", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Inaccuracy", "Suggestion").forEach { type ->
                            val isSelected = submissionDialogType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) DeepCharcoal else SoftGrey)
                                    .clickable { submissionDialogType = type }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) Color.White else Color.DarkGray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Commodity Input/Dropdown Selection
                    Text("Commodity / Staple Name", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    if (submissionCommodityId != null) {
                        OutlinedTextField(
                            value = submissionCommodityName,
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        var selectFromDropdown by remember { mutableStateOf(true) }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { selectFromDropdown = !selectFromDropdown }) {
                                Text(if (selectFromDropdown) "Keyboard Name" else "Select Existing List", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        if (selectFromDropdown) {
                            var dropdownExpanded by remember { mutableStateOf(false) }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color.LightGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { dropdownExpanded = true }
                                    .padding(vertical = 12.dp, horizontal = 12.dp)
                            ) {
                                Text(
                                    text = if (submissionCommodityName.isEmpty()) "Tap to select commodity..." else submissionCommodityName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            DropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                                commoditiesList.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text(c.name) },
                                        onClick = {
                                            submissionCommodityId = c.id
                                            submissionCommodityName = c.name
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = submissionCommodityName,
                                onValueChange = { submissionCommodityName = it; submissionCommodityId = null },
                                placeholder = { Text("E.g. Sweet Plantains, White Salt...") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("feedback_commodity_input"),
                                singleLine = true
                            )
                        }
                    }

                    // Market Location Selection
                    Text("Market Hub Location", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    var marketDropdownExpanded by remember { mutableStateOf(false) }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { marketDropdownExpanded = true }
                            .padding(vertical = 12.dp, horizontal = 12.dp)
                    ) {
                        Text(text = submissionMarketLocation, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    val adminLocations = listOf("Mile 12 (Mainland)", "Ikorodu (Mainland)", "Isale Eko (Island)", "Lekki (Island)")
                    DropdownMenu(expanded = marketDropdownExpanded, onDismissRequest = { marketDropdownExpanded = false }) {
                        adminLocations.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc) },
                                onClick = {
                                    submissionMarketLocation = loc
                                    marketDropdownExpanded = false
                                }
                            )
                        }
                    }

                    // Prices Reported
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Wholesale Price (₦)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            OutlinedTextField(
                                value = submissionWholesalePrice,
                                onValueChange = { submissionWholesalePrice = it },
                                placeholder = { Text("E.g. 85000") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                modifier = Modifier.testTag("feedback_wholesale_price_input")
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Subunit Retail (₦)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            OutlinedTextField(
                                value = submissionRetailPrice,
                                onValueChange = { submissionRetailPrice = it },
                                placeholder = { Text("Cup/Sachet Price") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                modifier = Modifier.testTag("feedback_retail_price_input")
                            )
                        }
                    }

                    // Message Note
                    Text("Reporting Vendor Notes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    OutlinedTextField(
                        value = submissionMessage,
                        onValueChange = { submissionMessage = it },
                        placeholder = { Text("Tell the admin why this price is updated or correct...") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        maxLines = 3
                    )
                }
            }
        )
    }
}

@Composable
fun CommodityPriceCard(
    commodity: Commodity,
    avgWholesale: Double,
    avgRetail: Double,
    isPlus: Boolean,
    onDetailsClick: () -> Unit
) {
    // Generate abbreviation from first 3 letters
    val cleanName = commodity.name.replace("(", "").replace(")", "").trim()
    val abbr = if (cleanName.length >= 3) cleanName.substring(0, 3).uppercase() else cleanName.uppercase()

    // Determine bubble color combos matching high-density mockup
    val (bgBubbleColor, textBubbleColor) = when (commodity.category) {
        "Grains" -> Color(0xFFFFF7ED) to Color(0xFFEA580C)         // orange-50 / orange-600
        "Beans" -> Color(0xFFFEF2F2) to Color(0xFFDC2626)          // red-50 / red-600
        "Processed Tubers" -> Color(0xFFFEF9C3) to Color(0xFF854D0E) // yellow-50 / yellow-700
        "Oils" -> Color(0xFFECFDF5) to Color(0xFF047857)           // emerald-50 / emerald-700
        "Vegetables" -> Color(0xFFF0FDF4) to Color(0xFF16A34A)     // green-50 / green-600
        else -> Color(0xFFEFF6FF) to Color(0xFF2563EB)             // blue-50 / blue-600
    }

    // Border line logic: highlight VIP staple indices with a left amber indicator border
    val hasGoldBorder = isPlus && (commodity.name.contains("Rice") || commodity.name.contains("Garri"))
    val leftAccentBorderPaddingModifier = if (hasGoldBorder) {
        Modifier.drawBehind {
            val strokeWidth = 10f // 4.dp equivalent
            drawLine(
                color = Color(0xFFF59E0B), // gold
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth
            )
        }
    } else Modifier

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailsClick() }
            .then(leftAccentBorderPaddingModifier)
            .testTag("commodity_card_${commodity.id}"),
        colors = CardDefaults.cardColors(containerColor = SoftGrey),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderGrey)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left icon bubble & names
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Colored letter bubble
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgBubbleColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = abbr,
                        fontWeight = FontWeight.Bold,
                        color = textBubbleColor,
                        fontSize = 11.sp,
                        letterSpacing = (-0.5).sp
                    )
                }
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Column {
                    Text(
                        text = commodity.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Conversion: ${commodity.wholesaleUnit} = ${commodity.conversionFactor.toInt()} ${commodity.microUnit}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }

            // Right side: Price & Highlights
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₦${if(avgWholesale > 0) String.format("%,.0f", avgWholesale) else "0"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Small unit highlight
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "₦${if(avgRetail > 0) String.format("%.0f", avgRetail) else "0"}/${commodity.microUnit}",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                    }

                    if (isPlus) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Plus Unlocked",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(11.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Plus Locked",
                            tint = Color.LightGray,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. ARBITRAGE VIEW (PLUS SCREEN)
// ==========================================
@Composable
fun ArbitrageView(viewModel: NairaGuardViewModel) {
    val subState by viewModel.subscription.collectAsStateWithLifecycle()
    val commodities by viewModel.commodities.collectAsStateWithLifecycle()
    val prices by viewModel.prices.collectAsStateWithLifecycle()

    if (subState.tier != "PLUS") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "PLUS feature locked icon",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Arbitrage Tools are Locked",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "With NairaGuard PLUS, track mainland-to-island price arbitrage. Buy wholesale in mainland hubs (Mile 12, Ikorodu) and retail on the Islands (Isale Eko, Lekki) with extreme margin clarity.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.changeScreen(Screen.SUBSCRIPTION) },
                modifier = Modifier.testTag("unlock_plus_button")
            ) {
                Text("Unlock NairaGuard PLUS")
            }
        }
    } else {
        // Active PLUS Feature
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Compare,
                            contentDescription = "Compare Icon",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Live Lagos Arbitrage Map",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Buy mainland, Sell Island. This lists buy-wholesale opportunities against retail sale points.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            items(commodities) { comm ->
                val commPrices = prices.filter { it.commodityId == comm.id }
                
                // Fetch Mainland prices (Mile 12 or Ikorodu)
                val mainlandPriceObj = commPrices.find { it.marketLocation.contains("Mile 12") }
                    ?: commPrices.find { it.marketLocation.contains("Mainland") }
                
                // Fetch Island prices (Isale Eko or Lekki)
                val islandPriceObj = commPrices.find { it.marketLocation.contains("Lekki") }
                    ?: commPrices.find { it.marketLocation.contains("Island") }

                if (mainlandPriceObj != null && islandPriceObj != null) {
                    val purchasePrice = mainlandPriceObj.wholesalePrice
                    val islandRetailEq = islandPriceObj.retailPrice
                    
                    // Arbitrage margin calculations
                    val totalRevenues = islandRetailEq * comm.conversionFactor
                    val grossProfit = totalRevenues - purchasePrice
                    val marginPercentage = if (totalRevenues > 0) (grossProfit / totalRevenues) * 100 else 0.0

                    ArbitrageItemCard(
                        commodity = comm,
                        buyMarket = mainlandPriceObj.marketLocation,
                        buyPrice = purchasePrice,
                        sellMarket = islandPriceObj.marketLocation,
                        sellPricePerUnit = islandRetailEq,
                        potentialProfit = grossProfit,
                        marginPercent = marginPercentage
                    )
                }
            }
        }
    }
}

@Composable
fun ArbitrageItemCard(
    commodity: Commodity,
    buyMarket: String,
    buyPrice: Double,
    sellMarket: String,
    sellPricePerUnit: Double,
    potentialProfit: Double,
    marginPercent: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = commodity.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (marginPercent > 20) NairaSuccessGreen.copy(alpha = 0.15f)
                            else AmberGold.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Margin: ${String.format("%.1f", marginPercent)}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (marginPercent > 20) NairaSuccessGreen else TerracottaOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Buy card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("BUY WHOLESALE", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(buyMarket.split(" ")[0], fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            "₦${String.format("%,.0f", buyPrice)} / ${commodity.wholesaleUnit.split(" ")[0]}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward Arbitrage Flow",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(18.dp),
                    tint = Color.Gray
                )

                // Sell card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("SELL RETAIL", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(sellMarket.split(" ")[0], fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            "₦${String.format("%.1f", sellPricePerUnit)} per ${commodity.microUnit}",
                            fontWeight = FontWeight.Bold,
                            color = NairaSuccessGreen,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = NairaSuccessGreen.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Potential Profit / unit", style = MaterialTheme.typography.bodySmall, color = NairaSuccessGreen)
                    Text(
                        "₦ ${String.format("%,.2f", potentialProfit)}",
                        fontWeight = FontWeight.Bold,
                        color = NairaSuccessGreen,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. MARGIN CALCULATOR VIEW (PLUS SCREEN)
// ==========================================
@Composable
fun MarginCalculatorView(viewModel: NairaGuardViewModel) {
    val subState by viewModel.subscription.collectAsStateWithLifecycle()
    val commodities by viewModel.commodities.collectAsStateWithLifecycle()
    val prices by viewModel.prices.collectAsStateWithLifecycle()

    if (subState.tier != "PLUS") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "PLUS Locked Icon",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Premium Margin Calculator Locked",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Instantly input custom logistics costs, purchase overheads, and target retail pricing to evaluate margins for daily Lagos retail runs.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.changeScreen(Screen.SUBSCRIPTION) },
                modifier = Modifier.testTag("unlock_plus_calc_button")
            ) {
                Text("Unlock NairaGuard PLUS")
            }
        }
    } else {
        // ACTIVE MARGIN CALCULATOR ACTIVE
        val selectedComm by viewModel.calcSelectedCommodity.collectAsStateWithLifecycle()
        val purchaseCost by viewModel.calcPurchasePrice.collectAsStateWithLifecycle()
        val retailSellingPrice by viewModel.calcSellingPricePerUnit.collectAsStateWithLifecycle()
        val transportCost by viewModel.calcTransportCost.collectAsStateWithLifecycle()
        val otherCost by viewModel.calcOtherCosts.collectAsStateWithLifecycle()

        var isCommMenuExpanded by remember { mutableStateOf(false) }

        // Setup default selection if none
        if (selectedComm == null && commodities.isNotEmpty()) {
            val defaultComm = commodities.find { it.name == "Premium Rice" } ?: commodities[0]
            viewModel.calcSelectedCommodity.value = defaultComm
            
            // Auto seed purchase price
            val commPrices = prices.filter { it.commodityId == defaultComm.id }
            val mainlandPrice = commPrices.find { it.marketLocation.contains("Mile 12") }?.wholesalePrice ?: 85000.0
            viewModel.calcPurchasePrice.value = mainlandPrice

            // Auto seed selling price unit
            val islandPrice = commPrices.find { it.marketLocation.contains("Lekki") }?.retailPrice ?: 850.0
            viewModel.calcSellingPricePerUnit.value = islandPrice
        }

        // Calculations
        val activeComm = selectedComm
        val conversion = activeComm?.conversionFactor ?: 128.0
        val totalInvestment = purchaseCost + transportCost + otherCost
        val projectedSales = retailSellingPrice * conversion
        val netGain = projectedSales - totalInvestment
        val markup = if (totalInvestment > 0) (netGain / totalInvestment) * 100 else 0.0
        val profitMargin = if (projectedSales > 0) (netGain / projectedSales) * 100 else 0.0

        val safetyColor = when {
            netGain <= 0 -> TerracottaOrange
            profitMargin < 12.0 -> AmberGold
            else -> NairaSuccessGreen
        }

        val safetyStatus = when {
            netGain <= 0 -> "LOSS DETECTED (Alert! Check logistical costs or raise retail price!)"
            profitMargin < 12.0 -> "RISKY / LOW MARGIN (Very tight spread. Vulnerable to spoilage & price variations.)"
            else -> "SAFE PROFIT MARGIN (Strong healthy returns for retail vendor!)"
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Dynamic Retail Margin Worksheet",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            // Commodity Dropdown Selector
            Text("Select Staple Retail Commodity", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { isCommMenuExpanded = true }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activeComm?.name ?: "No commodity selected",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand dropdown")
                }

                DropdownMenu(
                    expanded = isCommMenuExpanded,
                    onDismissRequest = { isCommMenuExpanded = false }
                ) {
                    commodities.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c.name) },
                            onClick = {
                                viewModel.calcSelectedCommodity.value = c
                                isCommMenuExpanded = false

                                // Default seed purchase + selling per unit from live prices in database
                                val commPrices = prices.filter { it.commodityId == c.id }
                                val rawPurchase = commPrices.find { it.marketLocation.contains("Mile 12") }?.wholesalePrice ?: 85000.0
                                val retailTarget = commPrices.find { it.marketLocation.contains("Lekki") }?.retailPrice ?: (rawPurchase / c.conversionFactor * 1.15)
                                
                                viewModel.calcPurchasePrice.value = rawPurchase
                                viewModel.calcSellingPricePerUnit.value = retailTarget
                            },
                            modifier = Modifier.testTag("calc_dropdown_item_${c.id}")
                        )
                    }
                }
            }

            if (activeComm != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Wholesale Unit: ${activeComm.wholesaleUnit}", fontSize = 12.sp, color = Color.Gray)
                            Text("Subunit unit: ${activeComm.microUnit}", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // INPUT FORMS
            Text("Purchasing & Running Overhead", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Purchase cost
                    OutlinedTextField(
                        value = purchaseCost.toString(),
                        onValueChange = { viewModel.calcPurchasePrice.value = it.toDoubleOrNull() ?: 0.0 },
                        label = { Text("Wholesale Import Cost (₦)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_calc_purchase"),
                        singleLine = true
                    )

                    // Transport costs
                    OutlinedTextField(
                        value = transportCost.toString(),
                        onValueChange = { viewModel.calcTransportCost.value = it.toDoubleOrNull() ?: 0.0 },
                        label = { Text("Lagos Transport & Logistics Overhead (₦)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_calc_transport"),
                        singleLine = true
                    )

                    // Other operational costs
                    OutlinedTextField(
                        value = otherCost.toString(),
                        onValueChange = { viewModel.calcOtherCosts.value = it.toDoubleOrNull() ?: 0.0 },
                        label = { Text("Rent, Handling, Bags, etc. (₦)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_calc_other"),
                        singleLine = true
                    )
                }
            }

            Text("Retail Target Yield", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Target retail unit
                    OutlinedTextField(
                        value = retailSellingPrice.toString(),
                        onValueChange = { viewModel.calcSellingPricePerUnit.value = it.toDoubleOrNull() ?: 0.0 },
                        label = { Text("Target Retail Selling Price per ${activeComm?.microUnit} (₦)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_calc_retail"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total conversion: ${conversion.roundToInt()} units will generate ₦${viewModel.formatNaira(projectedSales)} gross yield.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            // OUTPUT METRIC HIGHLIGHTS
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = safetyColor.copy(alpha = 0.12f)),
                border = BorderStroke(2.dp, safetyColor.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "MARGIN ANALYSIS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = safetyColor
                    )
                    Text(
                        text = "₦ ${viewModel.formatNaira(netGain)} Net Profit",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = safetyColor
                    )
                    Text(
                        text = safetyStatus,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = safetyColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Markup %", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                "${String.format("%.1f", markup)}%",
                                fontWeight = FontWeight.Bold,
                                color = safetyColor,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Text("Total Expenses", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                "₦${viewModel.formatNaira(totalInvestment)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Return on Investment", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                "${String.format("%.1f", profitMargin)}% Net Margin",
                                fontWeight = FontWeight.Bold,
                                color = safetyColor,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. INTELLIGENT WHATSAPP/SMS INGEST AGENT
// ==========================================
@Composable
fun SuperAgentView(viewModel: NairaGuardViewModel) {
    val userInputRawMsg by viewModel.agentInputText.collectAsStateWithLifecycle()
    val isAgentParsing by viewModel.isAgentParsing.collectAsStateWithLifecycle()
    val agentResultOutput by viewModel.agentParseResult.collectAsStateWithLifecycle()
    val listParsedHistory by viewModel.agentLastParsedUpdate.collectAsStateWithLifecycle()

    val sampleInputs = listOf(
        "Mile 12: Premium Rice bag wholesale is now 92,000 naira. Also white garri bag is 43000 naira",
        "Isale Eko Market Update! Oloyin Beans 50kg bag rises to 125,000 NGN. Rodo pepper basket is 32000 naira.",
        "Lekki Hub: Tuber Yam heap (100 tubers) priced at 235,000 NGN. Chicken Eggs crate is 5,800."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Gemini Logo",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "NairaGuard AI Wholesaler Ingestion",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Trained on Gemini 3.5 Flash. Transmit raw SMS, WhatsApp alerts or messy voice transcripts from wholesalers to automatically normalize them.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Text("Select Quick Sample Message", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sampleInputs.forEachIndexed { idx, sample ->
                OutlinedButton(
                    onClick = { viewModel.agentInputText.value = sample },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("ai_sample_msg_$idx")
                ) {
                    Text(
                        text = "Sample ${idx + 1}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // INPUT FIELD REPRESENTING WHATSAPP INPUT / High Density Slate Luxury Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSlateBg)
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AccentBlue)
                        )
                        Text(
                            text = "SMS Superagent v1.2 Ingest Engine",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
                
                OutlinedTextField(
                    value = userInputRawMsg,
                    onValueChange = { viewModel.agentInputText.value = it },
                    placeholder = { 
                        Text(
                            text = "Paste text, e.g. \"Mile12 Rice 50kg now 82k...\"", 
                            color = Color(0xFF64748B), 
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(86.dp)
                        .testTag("ai_messy_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = Color(0xFF475569),
                        focusedContainerColor = DarkSlateInput,
                        unfocusedContainerColor = DarkSlateInput,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color(0xFFCBD5E1),
                        focusedPlaceholderColor = Color(0xFF64748B),
                        unfocusedPlaceholderColor = Color(0xFF64748B)
                    ),
                    maxLines = 4
                )

                Button(
                    onClick = { viewModel.runIntelligentAgent() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("ai_parse_submit_button"),
                    enabled = !isAgentParsing && userInputRawMsg.isNotEmpty(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        disabledContainerColor = Color(0xFF334155),
                        contentColor = Color.White,
                        disabledContentColor = Color(0xFF64748B)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isAgentParsing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = Color.White,
                                strokeWidth = 1.5.dp
                            )
                            Text("Parsing Message...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Bolt, 
                                contentDescription = "AI Submit Run", 
                                modifier = Modifier.size(14.dp)
                            )
                            Text("Parse & Normalize with Gemini", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // RENDER OUPUT SCREEN RESULT
        agentResultOutput?.let { result ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (result.startsWith("Error")) TerracottaOrange.copy(alpha = 0.12f)
                    else NairaSuccessGreen.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.5.dp, 
                    if (result.startsWith("Error")) TerracottaOrange.copy(alpha = 0.4f) 
                    else NairaSuccessGreen.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth().testTag("ai_response_box")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (result.startsWith("Error")) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                            contentDescription = "Status Status",
                            tint = if (result.startsWith("Error")) TerracottaOrange else NairaSuccessGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (result.startsWith("Error")) "Ingested Engine Error" else "AI Ingestion Log Saved",
                            fontWeight = FontWeight.Bold,
                            color = if (result.startsWith("Error")) TerracottaOrange else NairaSuccessGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = result,
                        fontSize = 13.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (listParsedHistory.isNotEmpty()) {
            Text("Ingested Logs (This Session)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listParsedHistory.forEach { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(log.commodity, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(log.market, fontSize = 11.sp, color = Color.Gray)
                            }
                            Text(
                                "₦${viewModel.formatNaira(log.price)}",
                                fontWeight = FontWeight.Bold,
                                color = NairaSuccessGreen,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. SECURE ADMIN COMMAND CENTER
// ==========================================
@Composable
fun AdminCommandView(viewModel: NairaGuardViewModel) {
    val commodities by viewModel.commodities.collectAsStateWithLifecycle()
    val prices by viewModel.prices.collectAsStateWithLifecycle()
    val subscription by viewModel.subscription.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0 = Manual prices, 1 = Conversions, 2 = Subscription Status, 3 = Analytics
    val listLocations = listOf("Mile 12 (Mainland)", "Ikorodu (Mainland)", "Isale Eko (Island)", "Lekki (Island)")

    // Edit states
    var selectedCommAdmin by remember { mutableStateOf<Commodity?>(null) }
    var selectedLocationAdmin by remember { mutableStateOf("Mile 12 (Mainland)") }
    var inputWholesalePriceAdmin by remember { mutableStateOf("") }
    var showAdminManualSuccessMsg by remember { mutableStateOf(false) }

    // Conversion edit states
    var selectCommConvAdmin by remember { mutableStateOf<Commodity?>(null) }
    var inputConvFactorAdmin by remember { mutableStateOf("") }
    var showAdminConvSuccessMsg by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Main Admin Header
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin command emblem",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Secure Admin Command Center",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Alter conversion rules, update live pricing manually, view registration charts, and monitor premium subscriber statuses.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // TABS SELECTORS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Input Prices", "Set Conversions", "Subscribers", "Analytics", "Feedback Queue").forEachIndexed { idx, label ->
                FilterChip(
                    selected = activeTab == idx,
                    onClick = { activeTab = idx },
                    label = { Text(label) },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("admin_tab_$idx")
                )
            }
        }

        // TAB ACTIONS
        when (activeTab) {
            0 -> {
                // INPUT RAW WHOLESALE PRICES MANUALLY
                Text("Publish Manual Price Index Updates", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("1. Select Commodity Staple", style = MaterialTheme.typography.bodySmall)
                        // Simple dropdown selector implementation
                        var isExpanded by remember { mutableStateOf(false) }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = true }
                                .padding(vertical = 12.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = selectedCommAdmin?.name ?: "Tap to choose commodity...",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                            commodities.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.name) },
                                    onClick = {
                                        selectedCommAdmin = c
                                        isExpanded = false
                                        // seed input
                                        val pList = prices.filter { it.commodityId == c.id }
                                        val defaultPrice = pList.find { it.marketLocation == selectedLocationAdmin }?.wholesalePrice ?: 50000.0
                                        inputWholesalePriceAdmin = defaultPrice.toString()
                                    },
                                    modifier = Modifier.testTag("admin_price_selector_${c.id}")
                                )
                            }
                        }

                        Text("2. Target Market Location", style = MaterialTheme.typography.bodySmall)
                        var isLocationExpanded by remember { mutableStateOf(false) }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isLocationExpanded = true }
                                .padding(vertical = 12.dp, horizontal = 12.dp)
                        ) {
                            Text(text = selectedLocationAdmin, fontWeight = FontWeight.Bold)
                        }
                        DropdownMenu(expanded = isLocationExpanded, onDismissRequest = { isLocationExpanded = false }) {
                            listLocations.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text(loc) },
                                    onClick = {
                                        selectedLocationAdmin = loc
                                        isLocationExpanded = false
                                        selectedCommAdmin?.let { c ->
                                            val pList = prices.filter { it.commodityId == c.id }
                                            val defaultPrice = pList.find { it.marketLocation == loc }?.wholesalePrice ?: 50000.0
                                            inputWholesalePriceAdmin = defaultPrice.toString()
                                        }
                                    }
                                )
                            }
                        }

                        Text("3. Input Wholesale Price (₦)", style = MaterialTheme.typography.bodySmall)
                        OutlinedTextField(
                            value = inputWholesalePriceAdmin,
                            onValueChange = { inputWholesalePriceAdmin = it },
                            placeholder = { Text("Enter value in Naira...") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_price_input"),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val comm = selectedCommAdmin
                                val cost = inputWholesalePriceAdmin.toDoubleOrNull()
                                if (comm != null && cost != null && cost > 0) {
                                    viewModel.adminUpdatePrice(comm.id, selectedLocationAdmin, cost)
                                    showAdminManualSuccessMsg = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_price_submit"),
                            shape = RoundedCornerShape(8.dp),
                            enabled = selectedCommAdmin != null && inputWholesalePriceAdmin.isNotEmpty()
                        ) {
                            Text("Publish Market Price Log")
                        }

                        if (showAdminManualSuccessMsg) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NairaSuccessGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.0.dp, NairaSuccessGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Database live updated successfully!", fontSize = 12.sp, color = NairaSuccessGreen, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { showAdminManualSuccessMsg = false }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear msg", tint = NairaSuccessGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // MANAGE SUB-UNIT ATOMIC CONVERSION FACTOR
                Text("Modify Sub-unit Atomic Conversion Factor Rules", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "If wholesalers change container bags sizes (e.g. Rice bag changes from 120 cups to 128 cups due to supply shrinkflation), update the conversion metric rules. NairaGuard auto-recalculates retail units rates instantly.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("1. Select Commodity Model", style = MaterialTheme.typography.bodySmall)
                        var isConvExpanded by remember { mutableStateOf(false) }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isConvExpanded = true }
                                .padding(vertical = 12.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = selectCommConvAdmin?.name ?: "Tap to choose commodity...",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        DropdownMenu(expanded = isConvExpanded, onDismissRequest = { isConvExpanded = false }) {
                            commodities.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c.name) },
                                    onClick = {
                                        selectCommConvAdmin = c
                                        isConvExpanded = false
                                        inputConvFactorAdmin = c.conversionFactor.roundToInt().toString()
                                    },
                                    modifier = Modifier.testTag("admin_conv_selector_${c.id}")
                                )
                            }
                        }

                        selectCommConvAdmin?.let { c ->
                            Text(
                                "Active rule: 1 ${c.wholesaleUnit} yields ${c.conversionFactor.roundToInt()} ${c.microUnit}s.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text("2. Set New Subunits Factor Value", style = MaterialTheme.typography.bodySmall)
                        OutlinedTextField(
                            value = inputConvFactorAdmin,
                            onValueChange = { inputConvFactorAdmin = it },
                            placeholder = { Text("Numbers of micro-units inside a bag/carton...") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_conv_input"),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val comm = selectCommConvAdmin
                                val factor = inputConvFactorAdmin.toDoubleOrNull()
                                if (comm != null && factor != null && factor > 0) {
                                    viewModel.adminUpdateConversion(comm.id, factor)
                                    showAdminConvSuccessMsg = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_conv_submit"),
                            shape = RoundedCornerShape(8.dp),
                            enabled = selectCommConvAdmin != null && inputConvFactorAdmin.isNotEmpty()
                        ) {
                            Text("Overwrite Conversion Factor Rule")
                        }

                        if (showAdminConvSuccessMsg) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NairaSuccessGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.0.dp, NairaSuccessGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Global rule overwritten successfully!", fontSize = 12.sp, color = NairaSuccessGreen, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { showAdminConvSuccessMsg = false }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear status", tint = NairaSuccessGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // SUBSCRIBERS STATUS CONTROLLER
                Text("Monitor Subscribers Tier Status", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "NairaGuard currently acts on a multi-tier licensing rule. FREE tier profiles can only visualize composite averages. Monitored users list:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                // Current sub override
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Quick Toggle Current User Tier (Testing Override)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.cancelSubscription() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TerracottaOrange)
                            ) {
                                Text("Set Current to FREE")
                            }
                            Button(
                                onClick = { viewModel.subscribePlusMonthly() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NairaSuccessGreen)
                            ) {
                                Text("Force Active PLUS")
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Current State: ${subscription.tier} - Member Status: ${subscription.status.uppercase()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                // Active User registry list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.mockUsers.forEach { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Primary Market: ${user.market} · Registered: ${user.registerDate}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (user.status) {
                                                "active" -> NairaSuccessGreen.copy(alpha = 0.15f)
                                                "trial" -> AmberGold.copy(alpha = 0.15f)
                                                else -> Color.Gray.copy(alpha = 0.15f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = user.subscription,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (user.status) {
                                            "active" -> NairaSuccessGreen
                                            "trial" -> AmberGold
                                            else -> Color.DarkGray
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // ANALYTICS & REGISTRATION CHARTS
                Text("Lagos Consumer Demographics & Registrations", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                // Registration Chartdrawn with Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val regCount = listOf(35, 48, 62, 51, 89, 120, 150)
                    
                    val maxVal = 160f
                    val chartWidth = size.width
                    val chartHeight = size.height
                    
                    val barSpacing = 40f
                    val barWidth = (chartWidth - (barSpacing * (days.size + 1))) / days.size

                    // Draw baseline
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(0f, chartHeight - 40f),
                        end = Offset(chartWidth, chartHeight - 40f),
                        strokeWidth = 2f
                    )

                    // Render Bars
                    days.forEachIndexed { i, day ->
                        val count = regCount[i]
                        val pct = count / maxVal
                        val barHeight = (chartHeight - 80f) * pct
                        val startX = barSpacing + i * (barWidth + barSpacing)
                        val startY = chartHeight - 40f - barHeight

                        // Bar background
                        drawRect(
                            color = TerracottaOrange,
                            topLeft = Offset(startX, startY),
                            size = Size(barWidth, barHeight)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Total registers: 555 active merchant outlets in the last 7-day Lagos market run.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Sub ratio analytics summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Active PLUS", fontSize = 11.sp, color = Color.Gray)
                            Text("182 Vendors", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NairaSuccessGreen)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Trial PLUS", fontSize = 11.sp, color = Color.Gray)
                            Text("64 Vendors", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AmberGold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("FREE Standard", fontSize = 11.sp, color = Color.Gray)
                            Text("309 Vendors", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }
            4 -> {
                val feedbackList by viewModel.feedbackList.collectAsStateWithLifecycle()
                
                Text(text = "Vendor Feedback & Price Corrections Queue", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "Review suggestions and price inaccuracy reports filed by retail vendors. Approving integration automatically syncs live items and triggers alert pipelines.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                if (feedbackList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftGrey)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Feedback, contentDescription = "Empty Feedback", modifier = Modifier.size(32.dp), tint = Color.LightGray)
                            Text("No feedback reports submitted yet.", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        feedbackList.forEach { feedback ->
                            Card(
                                modifier = Modifier.fillMaxWidth().testTag("feedback_card_${feedback.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (feedback.status) {
                                        "Integrated" -> NairaSuccessGreen.copy(alpha = 0.03f)
                                        "Dismissed" -> Color.LightGray.copy(alpha = 0.05f)
                                        else -> Color.White
                                    }
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    when (feedback.status) {
                                        "Integrated" -> NairaSuccessGreen.copy(alpha = 0.3f)
                                        "Dismissed" -> Color.LightGray
                                        else -> BorderGrey
                                    }
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        if (feedback.feedbackType == "Inaccuracy") TerracottaOrange.copy(alpha = 0.12f)
                                                        else AccentBlueBg
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = feedback.feedbackType.uppercase(),
                                                    color = if (feedback.feedbackType == "Inaccuracy") TerracottaOrange else AccentBlue,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = feedback.commodityName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = DeepCharcoal
                                            )
                                        }
                                        
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (feedback.status) {
                                                        "Integrated" -> NairaSuccessGreen.copy(alpha = 0.15f)
                                                        "Dismissed" -> Color.Gray.copy(alpha = 0.15f)
                                                        else -> Color(0xFFFEF3C7)
                                                    }
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = feedback.status,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (feedback.status) {
                                                    "Integrated" -> NairaSuccessGreen
                                                    "Dismissed" -> Color.DarkGray
                                                    else -> Color(0xFFD97706)
                                                }
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Market: ${feedback.marketLocation}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.DarkGray
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Column {
                                            Text("Reported Wholesale", fontSize = 10.sp, color = Color.Gray)
                                            Text("₦${viewModel.formatNaira(feedback.reportedWholesalePrice)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                        }
                                        Column {
                                            Text("Reported Subunit Retail", fontSize = 10.sp, color = Color.Gray)
                                            Text("₦${viewModel.formatNaira(feedback.reportedRetailPrice)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                                        }
                                    }

                                    if (feedback.message.isNotEmpty()) {
                                        Text(
                                            text = "\"${feedback.message}\"",
                                            fontSize = 11.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    if (feedback.status == "Pending") {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = { viewModel.adminProcessFeedback(feedback, false) },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                                modifier = Modifier.weight(1f).height(32.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Dismiss", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                            
                                            Button(
                                                onClick = { viewModel.adminProcessFeedback(feedback, true) },
                                                colors = ButtonDefaults.buttonColors(containerColor = NairaSuccessGreen),
                                                modifier = Modifier.weight(1.5f).height(32.dp),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Approve & Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        TextButton(
                                            onClick = { viewModel.adminDeleteFeedback(feedback.id) },
                                            modifier = Modifier.align(Alignment.End).height(24.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp)
                                        ) {
                                            Text("Delete Report", color = Color.Red, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. SUBSCRIPTION VIEW (ALERTS SETUP AND SUBSCRIPTION STATUS)
// ==========================================
@Composable
fun SubscriptionView(viewModel: NairaGuardViewModel) {
    val subState by viewModel.subscription.collectAsStateWithLifecycle()
    val alertLogs by viewModel.smsAlertLogs.collectAsStateWithLifecycle()

    var userPhoneInput by remember { mutableStateOf(subState.phoneNumber) }
    var userTriggerPriceInput by remember { mutableStateOf("") }
    var selectedCommForAlert by remember { mutableStateOf<Commodity?>(null) }
    var selectedMarketForAlert by remember { mutableStateOf("Mile 12 (Mainland)") }
    var isAlertAbove by remember { mutableStateOf(true) }

    val commodities by viewModel.commodities.collectAsStateWithLifecycle()
    val alertsList by viewModel.alerts.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Membership details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (subState.tier == "PLUS") NairaSuccessGreen.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                1.5.dp,
                if (subState.tier == "PLUS") NairaSuccessGreen else Color.LightGray.copy(alpha = 0.4f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "CURRENT TIERS STATUS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (subState.tier == "PLUS") NairaSuccessGreen else Color.Gray
                        )
                        Text(
                            text = "NairaGuard ${subState.tier}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (subState.tier == "PLUS") NairaSuccessGreen else Color.LightGray
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (subState.tier == "PLUS") "PLUS UNLOCKED" else "FREE LICENSE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (subState.tier == "PLUS") {
                    Text(
                        text = "Licensed Membership Status: ${subState.status.uppercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    subState.expiryDate?.let { date ->
                        val daysRemaining = ((date - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
                        Text(
                            text = "Subscription valid for another $daysRemaining days.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { viewModel.cancelSubscription() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cancel_sub_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Downgrade to Standard FREE Account", color = TerracottaOrange)
                    }
                } else {
                    Text(
                        "Free licenses can only view combined standard averages of Lagos indices. Upgrade to access comparison matrix tools, margin logs and live automated custom alerts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.activatePlusTrial() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_trial_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Start 7-Day FREE Trial")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = { viewModel.subscribePlusMonthly() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("subscribe_monthly_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NairaSuccessGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Subscribe to PLUS (₦ 3,000 / month)")
                    }
                }
            }
        }

        if (subState.tier == "PLUS") {
            // Live price alerts config - PLUS exclusive
            Text("Create Custom Price Alert", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    var isAlertSelectExpanded by remember { mutableStateOf(false) }

                    if (selectedCommForAlert == null && commodities.isNotEmpty()) {
                        selectedCommForAlert = commodities[0]
                    }

                    Text("1. Select Commodity", fontSize = 11.sp, color = Color.Gray)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isAlertSelectExpanded = true }
                            .padding(vertical = 12.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = selectedCommForAlert?.name ?: "Tap to choose commodity...",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    DropdownMenu(expanded = isAlertSelectExpanded, onDismissRequest = { isAlertSelectExpanded = false }) {
                        commodities.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c.name) },
                                onClick = {
                                    selectedCommForAlert = c
                                    isAlertSelectExpanded = false
                                },
                                modifier = Modifier.testTag("alert_selector_${c.id}")
                            )
                        }
                    }

                    Text("2. Target Market Location", fontSize = 11.sp, color = Color.Gray)
                    var isMarketSelExpanded by remember { mutableStateOf(false) }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isMarketSelExpanded = true }
                            .padding(vertical = 12.dp, horizontal = 12.dp)
                    ) {
                        Text(text = selectedMarketForAlert, fontWeight = FontWeight.Bold)
                    }
                    DropdownMenu(expanded = isMarketSelExpanded, onDismissRequest = { isMarketSelExpanded = false }) {
                        listOf("Mile 12 (Mainland)", "Ikorodu (Mainland)", "Isale Eko (Island)", "Lekki (Island)").forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc) },
                                onClick = {
                                    selectedMarketForAlert = loc
                                    isMarketSelExpanded = false
                                }
                            )
                        }
                    }

                    Text("3. Retail Target Trigger Limit (₦)", fontSize = 11.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = userTriggerPriceInput,
                        onValueChange = { userTriggerPriceInput = it },
                        placeholder = { Text("Price per ${selectedCommForAlert?.microUnit ?: "unit"}...") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("alert_target_input"),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Trigger when price is:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isAlertAbove, onClick = { isAlertAbove = true })
                            Text("Above", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            RadioButton(selected = !isAlertAbove, onClick = { isAlertAbove = false })
                            Text("Below", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = {
                            val target = userTriggerPriceInput.toDoubleOrNull()
                            val comm = selectedCommForAlert
                            if (target != null && target > 0 && comm != null) {
                                viewModel.addPriceAlert(comm.id, selectedMarketForAlert, target, isAlertAbove)
                                userTriggerPriceInput = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("alert_submit_button"),
                        shape = RoundedCornerShape(8.dp),
                        enabled = userTriggerPriceInput.isNotEmpty()
                    ) {
                        Text("Establish Live Threshold Alert")
                    }
                }
            }

            if (alertsList.isNotEmpty()) {
                Text("Your Threshold Alerts", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    alertsList.forEach { alert ->
                        val comm = commodities.find { it.id == alert.commodityId }
                        if (comm != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("${comm.name} (${alert.marketLocation.split(" ")[0]})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            text = "Trigger limit: ₦${viewModel.formatNaira(alert.targetPrice)} per ${comm.microUnit} (${if (alert.isAbove) "Above" else "Below"})",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        if (alert.isTriggered) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(TerracottaOrange.copy(alpha = 0.15f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("TRIGGERED 🚨", fontSize = 9.sp, color = TerracottaOrange, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.removePriceAlert(alert.id) },
                                        modifier = Modifier.testTag("delete_alert_${alert.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Alert", tint = TerracottaOrange)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Automated daily SMS alerts
        Text("Daily SMS Market Broadcaster Dispatcher", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Automated Daily Market SMS Alerts", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Receive early morning digests of live Lagos prices before markets open.", fontSize = 10.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = subState.dailySmsAlertEnabled,
                        onCheckedChange = { viewModel.toggleSmsAlerts(it) },
                        modifier = Modifier.testTag("sms_alert_toggle")
                    )
                }

                if (subState.dailySmsAlertEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = userPhoneInput,
                        onValueChange = { 
                            userPhoneInput = it
                            viewModel.updateSmsPhone(it)
                        },
                        label = { Text("Your WhatsApp/SMS Phone Number") },
                        placeholder = { Text("e.g. +234 812 3456 789") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sms_phone_input"),
                        singleLine = true
                    )
                }
            }
        }

        // Live Simulated Log Center
        if (alertLogs.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Simulated SMS Logs", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                TextButton(onClick = { viewModel.smsAlertLogs.value = emptyList() }) {
                    Text("Clear Logs")
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = CardDefaults.cardColors(containerColor = DeepCharcoal)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(alertLogs) { log ->
                        Text(
                            text = log,
                            color = Color(0xFF00FF66),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// PREMIUM LOCKED CARD PLACEHOLDER
@Composable
fun SubscriptionGate(message: String, onUpgrade: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Lock,
                contentDescription = "Lock",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onUpgrade,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.minimumInteractiveComponentSize().testTag("upgrade_gate_trigger")
            ) {
                Text("Unlock NairaGuard PLUS", fontSize = 12.sp)
            }
        }
    }
}

// CUSTOM LINE CHART CANVAS ELEMENT TO RENDER PRICE TREND VOLATILITY
@Composable
fun NairaLineChart(history: List<PriceHistory>, modifier: Modifier = Modifier) {
    val reversedHist = history.takeLast(5)
    if (reversedHist.size < 2) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Gathering historical price indices...", fontSize = 11.sp)
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val chartWidth = size.width
        val chartHeight = size.height

        val maxPrice = reversedHist.maxOf { it.wholesalePrice }
        val minPrice = reversedHist.minOf { it.wholesalePrice }
        val range = (maxPrice - minPrice).coerceAtLeast(1.0)
        
        val marginX = 20f
        val marginY = 30f
        val drawWidth = chartWidth - marginX * 2
        val drawHeight = chartHeight - marginY * 2

        val stepX = drawWidth / (reversedHist.size - 1)

        val points = reversedHist.mapIndexed { idx, hist ->
            val fractY = (hist.wholesalePrice - minPrice) / range
            val x = marginX + idx * stepX
            val y = chartHeight - marginY - (fractY * drawHeight).toFloat()
            Offset(x, y)
        }

        // Draw background horizontal grids
        for (i in 0..2) {
            val hGridY = marginY + (i / 2f) * drawHeight
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(marginX, hGridY),
                end = Offset(chartWidth - marginX, hGridY),
                strokeWidth = 1f
            )
        }

        // DRAW SHADED GRADIENT BENEATH CURVE
        val fillPath = Path().apply {
            moveTo(points.first().x, chartHeight - marginY)
            points.forEach { pt ->
                lineTo(pt.x, pt.y)
            }
            lineTo(points.last().x, chartHeight - marginY)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent),
                startY = marginY,
                endY = chartHeight - marginY
            )
        )

        // DRAW THE CORE LINE
        val strokePath = Path().apply {
            val first = points.first()
            moveTo(first.x, first.y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(
            path = strokePath,
            color = primaryColor,
            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // DRAW JOIN COORD POINTS (RIPPLES)
        points.forEachIndexed { i, pt ->
            // Point anchor dot
            drawCircle(
                color = primaryColor,
                radius = 8f,
                center = pt
            )
            // Accent halo
            drawCircle(
                color = Color.White,
                radius = 4f,
                center = pt
            )
        }
    }
}
