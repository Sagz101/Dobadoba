package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.util.FeeCalculator
import kotlinx.coroutines.launch

@Composable
fun DobaChamaAndCoinsScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Balance Flows
    val walletBalance by viewModel.dobaPayBalance.collectAsState()
    val coinsBalance by viewModel.dobaCoinsBalance.collectAsState()
    
    // Database flows
    val feesList by viewModel.allFeesList.collectAsState()
    val airtimeBundles by viewModel.allAirtimeBundlesFlow.collectAsState()
    val standingOrders by viewModel.allStandingOrdersFlow.collectAsState()
    val giftsList by viewModel.allGiftsFlow.collectAsState()
    val chamasList by viewModel.allChamasFlow.collectAsState()
    val policiesList by viewModel.allPoliciesFlow.collectAsState()
    val marketListings by viewModel.discoverPosts.collectAsState() // use discover market listings

    // Intermediary tab
    var selectedTab by remember { mutableStateOf("Coins") } // Coins, Reseller, Standing, Chama, Ads, Insurance, Admin

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Balances Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MalawiRed),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DobaPay Balance 🇲🇼",
                            color = Color.White.copy(0.7f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = FeeCalculator.formatMwk(walletBalance),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "Coins", tint = Color(0xFFFFD700), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$coinsBalance Coins",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Horizontal Navigation Tabs For Sprints
        ScrollableTabRow(
            selectedTabIndex = when(selectedTab) {
                "Coins" -> 0
                "Reseller" -> 1
                "Standing" -> 2
                "Chama" -> 3
                "Ads" -> 4
                "Insurance" -> 5
                else -> 6
            },
            edgePadding = 16.dp,
            containerColor = Color.Transparent,
            contentColor = MalawiRed
        ) {
            val tabs = listOf(
                "Coins" to "🪙 Coins Shop",
                "Reseller" to "📞 Airtime Resell",
                "Standing" to "📅 Schedule SO",
                "Chama" to "👥 Chamas Circles",
                "Ads" to "📢 Self-Serve Ads",
                "Insurance" to "🛡️ Cover Plans",
                "Admin" to "📊 Revenue Admin"
            )
            tabs.forEach { (key, title) ->
                Tab(
                    selected = selectedTab == key,
                    onClick = { selectedTab = key },
                    text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Screen Layouts
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when(selectedTab) {
                "Coins" -> CoinsShopTab(viewModel, walletBalance, coinsBalance, context)
                "Reseller" -> AirtimeResellerTab(viewModel, airtimeBundles, walletBalance, context)
                "Standing" -> StandingOrdersTab(viewModel, standingOrders, walletBalance, context)
                "Chama" -> ChamasTab(viewModel, chamasList, walletBalance, context)
                "Ads" -> SelfServeAdsTab(viewModel, marketListings, walletBalance, context)
                "Insurance" -> InsuranceTab(viewModel, policiesList, walletBalance, context)
                else -> RevenueAdminTab(viewModel, feesList, chamasList, policiesList, giftsList)
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 1: COINS SHOP
// -----------------------------------------------------------------
@Composable
fun CoinsShopTab(
    viewModel: DobadobaViewModel,
    walletBalance: Double,
    coinsBalance: Int,
    context: android.content.Context
) {
    val showConfetti by viewModel.showConfettiAnimation.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showConfetti) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = BorderStroke(1.dp, Color(0xFF81C784))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✨ CONGRATULATIONS! ✨", fontWeight = FontWeight.Black, color = Color(0xFF2E7D32), fontSize = 16.sp)
                        Text("DobaCoins Purchased Successfully! Confetti animation triggered.", color = Color(0xFF388E3C), fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text("Select DobaCoins Reward Bundle", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        val packs = listOf(
            Triple(1000.0, 1000, "Standard Starter"),
            Triple(2500.0, 2800, "Economic Value (+12% bonus)"),
            Triple(5000.0, 6000, "Super Saver Ultimate (+20% bonus)")
        )

        items(packs) { (price, coins, label) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    if (walletBalance < price) {
                        Toast.makeText(context, "Insufficient DobaPay funds!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.handlePurchaseDobaCoins(price, coins)
                        Toast.makeText(context, "Bought $coins Coins for ${FeeCalculator.formatMwk(price)}!", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (coins > 1000) MalawiRed.copy(0.4f) else Color.LightGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (coins > 1000) MalawiRed else Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "", tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$coins Coins", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                    }
                    Button(
                        onClick = {
                            if (walletBalance < price) {
                                Toast.makeText(context, "Insufficient DobaPay funds!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.handlePurchaseDobaCoins(price, coins)
                                Toast.makeText(context, "Bought $coins Coins for ${FeeCalculator.formatMwk(price)}!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MalawiRed)
                    ) {
                        Text(FeeCalculator.formatMwk(price))
                    }
                }
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Try Virutal Broadcaster Tips (70/30 Split Demo)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        val gifts = listOf(
            "Lake Malawi Fish" to 50,
            "Baobab Tree" to 200,
            "Malawi Flag Wave" to 500,
            "Dobadoba Crown" to 5000
        )

        items(gifts) { (name, value) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = "", tint = MalawiRed, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("$value Coins required", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Button(
                        onClick = {
                            if (coinsBalance < value) {
                                Toast.makeText(context, "Not enough DobaCoins! Please top up.", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.handleSendLiveGift(name, value, "DJ Gogo")
                                Toast.makeText(context, "Gifted $name! Broadcaster received 70% MWK share.", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("Gift ($value)")
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 2: AIRTIME RESELLER
// -----------------------------------------------------------------
@Composable
fun AirtimeResellerTab(
    viewModel: DobadobaViewModel,
    bundles: List<AirtimeBundle>,
    walletBalance: Double,
    context: android.content.Context
) {
    var queryPhone by remember { mutableStateOf("0888123456") }
    var securityPinEntered by remember { mutableStateOf("") }
    val lastToppedUpText by viewModel.lastToppedUpText.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Telecom Margin: 2% of price is tracked directly as franchise income!", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                    Text(lastToppedUpText, fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                }
            }
        }

        item {
            Text("Telecom Airtime & Data Packs reselling", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            OutlinedTextField(
                value = queryPhone,
                onValueChange = { queryPhone = it },
                label = { Text("Recipient Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }

        item {
            OutlinedTextField(
                value = securityPinEntered,
                onValueChange = { securityPinEntered = it },
                label = { Text("Security Wallet PIN (default: 1234)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        val filteredBundles = bundles.ifEmpty {
            listOf(
                AirtimeBundle(telco = "Airtel", title = "500MB Social Bundle", priceMwk = 500.0, isData = true),
                AirtimeBundle(telco = "Airtel", title = "2GB Daily Max", priceMwk = 1200.0, isData = true),
                AirtimeBundle(telco = "Airtel", title = "10GB Weekly Ultimate", priceMwk = 5000.0, isData = true),
                AirtimeBundle(telco = "TNM", title = "4GB Tik Tok Pass", priceMwk = 2000.0, isData = true),
                AirtimeBundle(telco = "TNM", title = "MK 500 Airtime Promo", priceMwk = 500.0, isData = false)
            )
        }

        items(filteredBundles) { bundle ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, if (bundle.telco == "Airtel") Color.Red.copy(0.3f) else Color.Green.copy(0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${bundle.telco} - ${bundle.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(if (bundle.isData) "Mobile Data Bundle" else "Airtime credit", fontSize = 12.sp, color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            if (securityPinEntered != "1234") {
                                Toast.makeText(context, "Incorrect wallet PIN!", Toast.LENGTH_SHORT).show()
                            } else if (walletBalance < bundle.priceMwk) {
                                Toast.makeText(context, "Insufficient DobaPay wallet funds", Toast.LENGTH_SHORT).show()
                            } else {
                                val ok = viewModel.executeAirtimeBundlePurchase(bundle, securityPinEntered)
                                if (ok) {
                                    Toast.makeText(context, "Resold successfully to $queryPhone! margin registered.", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (bundle.telco == "Airtel") MalawiRed else Color(0xFF00C853))
                    ) {
                        Text("Pay ${FeeCalculator.formatMwk(bundle.priceMwk)}")
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 3: STANDING ORDERS
// -----------------------------------------------------------------
@Composable
fun StandingOrdersTab(
    viewModel: DobadobaViewModel,
    orders: List<StandingOrderEntity>,
    walletBalance: Double,
    context: android.content.Context
) {
    var recipientPhone by remember { mutableStateOf("0999580421") }
    var orderAmountStr by remember { mutableStateOf("5000") }
    var orderDescription by remember { mutableStateOf("Monthly Rent / Food contribution") }
    var orderFrequency by remember { mutableStateOf("Monthly") } // Weekly, Monthly

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📅 Automated Standby Scheduler", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("WorkManager runs daily checks and auto-debits scheduled amounts with 1% transfer fees. Generates warning alerts if local wallet falls short.", fontSize = 12.sp, color = Color.DarkGray)
                }
            }
        }

        item {
            Text("Create Standing Order / Subscription Scheduler", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        item {
            OutlinedTextField(
                value = recipientPhone,
                onValueChange = { recipientPhone = it },
                label = { Text("Recipient Phone number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }

        item {
            OutlinedTextField(
                value = orderAmountStr,
                onValueChange = { orderAmountStr = it },
                label = { Text("Amount (MWK)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        item {
            OutlinedTextField(
                value = orderDescription,
                onValueChange = { orderDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Weekly", "Monthly").forEach { freq ->
                    Button(
                        onClick = { orderFrequency = freq },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (orderFrequency == freq) MalawiRed else Color.DarkGray)
                    ) {
                        Text(freq)
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val doubleAmt = orderAmountStr.toDoubleOrNull() ?: 0.0
                    if (doubleAmt <= 0) {
                        Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.handleCreateStandingOrder(
                            recipientPhone,
                            doubleAmt,
                            orderFrequency,
                            "2026-06-01",
                            orderDescription
                        )
                        Toast.makeText(context, "Standing order added securely!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MalawiRed)
            ) {
                Text("Schedule Periodic Order (+1% Transfer Fee)")
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Active Scheduled list (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        if (orders.isEmpty()) {
            item {
                Text("No standing orders configured yet. Create one above to test.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            items(orders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (order.isPaused) Color.LightGray.copy(0.3f) else MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(order.description, fontWeight = FontWeight.Bold)
                                Text("To: ${order.recipientPhone} (${order.frequency})", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text(FeeCalculator.formatMwk(order.amountMwk), fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.handleTogglePauseStandingOrder(order) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                            ) {
                                Text(if (order.isPaused) "Resume" else "Pause")
                            }
                            Button(
                                onClick = { viewModel.handleDeleteStandingOrder(order) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MalawiRed)
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 4: CHAMAS CIRCLES
// -----------------------------------------------------------------
@Composable
fun ChamasTab(
    viewModel: DobadobaViewModel,
    chamas: List<ChamaEntity>,
    walletBalance: Double,
    context: android.content.Context
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var chamaName by remember { mutableStateOf("") }
    var chamaDesc by remember { mutableStateOf("") }
    var contributionStr by remember { mutableStateOf("10000") }

    var activeChamaDetail by remember { mutableStateOf<ChamaEntity?>(null) }
    var isStandingOrderActive by remember { mutableStateOf(true) } // Status toggle

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("👥 DobaChama Rotational Digital Savings", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF2E7D32))
                        Text("Join community financial circles with automated ledger contribution tracking. Click any item for details.", fontSize = 12.sp, color = Color(0xFF1B5E20))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Discovered Community Chamas (${chamas.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            val itemsList = chamas.ifEmpty {
                listOf(
                    ChamaEntity(
                        id = 1,
                        name = "Zomba Chitenge Tailors Club",
                        description = "Rotational fund for heavy-duty sewing machine acquisitions",
                        contributionAmountMwk = 15000.0,
                        payoutIndex = 0,
                        nextPayoutDate = "2026-06-01",
                        monthlyFeeStatus = "Paid"
                    ),
                    ChamaEntity(
                        id = 2,
                        name = "Mchinji Soya Distributors",
                        description = "Trading group pooling warehouse fees together",
                        contributionAmountMwk = 25000.0,
                        payoutIndex = 1,
                        nextPayoutDate = "2026-06-15",
                        monthlyFeeStatus = "Not Paid"
                    )
                )
            }

            items(itemsList) { chama ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeChamaDetail = chama },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Color.Gray.copy(0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(chama.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SunriseOrange)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MalawiGreen.copy(0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(chama.monthlyFeeStatus, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MalawiGreen)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(chama.description, fontSize = 12.sp, color = MutedTextGray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Share: ${FeeCalculator.formatMwk(chama.contributionAmountMwk)}", fontWeight = FontWeight.Bold)
                            Text("Next Pay: ${chama.nextPayoutDate}", color = MalawiRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.handleJoinChamaGroup(chama)
                                    Toast.makeText(context, "Successfully joined ${chama.name}!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                            ) {
                                Text("Join Circle", fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    if (walletBalance < chama.contributionAmountMwk) {
                                        Toast.makeText(context, "Wallet balance too low!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.handleContributeToChama(chama)
                                        Toast.makeText(context, "Dispatched Contribution of ${FeeCalculator.formatMwk(chama.contributionAmountMwk)} successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MalawiRed)
                            ) {
                                Text("Pay Share", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to initiate new groups (Sprints 3.1)
        FloatingActionButton(
            onClick = {
                chamaName = ""
                chamaDesc = ""
                contributionStr = "10000"
                showCreateDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("create_chama_fab"),
            containerColor = SunriseOrange,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Launch Chama Group")
        }

        // Create Chama Group Dialog
        if (showCreateDialog) {
            Dialog(onDismissRequest = { showCreateDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Launch New Chama 👥",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SunriseOrange
                        )

                        OutlinedTextField(
                            value = chamaName,
                            onValueChange = { chamaName = it },
                            label = { Text("Chama Circle Name") },
                            modifier = Modifier.fillMaxWidth().testTag("add_chama_name_textbox")
                        )

                        OutlinedTextField(
                            value = chamaDesc,
                            onValueChange = { chamaDesc = it },
                            label = { Text("Group Goal / Description") },
                            modifier = Modifier.fillMaxWidth().testTag("add_chama_description_textbox")
                        )

                        OutlinedTextField(
                            value = contributionStr,
                            onValueChange = { contributionStr = it },
                            label = { Text("Rotational Contribution (MWK)") },
                            modifier = Modifier.fillMaxWidth().testTag("add_chama_contribution_textbox"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showCreateDialog = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    val amt = contributionStr.toDoubleOrNull() ?: 0.0
                                    if (chamaName.trim().isEmpty() || chamaDesc.trim().isEmpty()) {
                                        Toast.makeText(context, "Group name/description cannot be blank!", Toast.LENGTH_SHORT).show()
                                    } else if (amt <= 0) {
                                        Toast.makeText(context, "Contribution must be greater than zero!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.handleCreateChamaGroup(chamaName, chamaDesc, amt)
                                        showCreateDialog = false
                                        Toast.makeText(context, "Chama circle '${chamaName}' launched successfully!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MalawiRed),
                                modifier = Modifier.weight(1f).testTag("add_chama_submit_btn")
                            ) {
                                Text("Register")
                            }
                        }
                    }
                }
            }
        }

        // Chama Details Overlay Screen (Sprints 3.2 & 3.3)
        if (activeChamaDetail != null) {
            val chama = activeChamaDetail!!
            Dialog(onDismissRequest = { activeChamaDetail = null }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = chama.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SunriseOrange
                        )

                        Text(chama.description, fontSize = 13.sp, color = MutedTextGray)

                        HorizontalDivider(color = Color.Gray.copy(0.2f))

                        // Progress to target rotational pool (FIX 3.2 Progress bar)
                        Text("Active Pooling Progress Volume", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = { 0.65f },
                                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                                color = MalawiGreen,
                                trackColor = MalawiGreen.copy(0.2f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pooled MWK 45,000", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MalawiGreen)
                                Text("Goal: MWK 150,000", fontSize = 11.sp, color = MutedTextGray)
                            }
                        }

                        // Rotational schedule + member payout positions (FIX 3.2 member list)
                        Text("Payout Positions Rotations Schedule", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Gray.copy(0.06f), shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            listOf(
                                Triple("Chimwemwe Phiri", "Position 1 (Paid)", true),
                                Triple("Limbani Banda", "Position 2 (Next out)", false),
                                Triple("Your Account (@ine_me_mw)", "Position 3 (Pending)", false)
                            ).forEach { (m, pos, done) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (done) MalawiGreen else SunriseOrange),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(m.take(1), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(m, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(
                                        text = pos,
                                        fontSize = 11.sp,
                                        color = if (done) MalawiGreen else MutedTextGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Historical Ledger Contribution (FIX 3.2 historic ledger)
                        Text("Historic Contribution Ledger (Last 30 Days)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                Triple("Rotational Pool Payout • Phiri", "+ MWK 45,000", "2026-05-15"),
                                Triple("Your Rotational Share Paid", "- MWK 15,000", "2026-05-12"),
                                Triple("Member Bandas Pool Payment", "+ MWK 15,000", "2026-05-10")
                            ).forEach { (txt, amt, dt) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(txt, fontSize = 12.sp)
                                        Text(dt, fontSize = 10.sp, color = MutedTextGray)
                                    }
                                    Text(
                                        text = amt,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (amt.startsWith("+")) MalawiGreen else MalawiRed
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color.Gray.copy(0.2f))

                        // Standing Orders Autoplay Toggle Control (FIX 3.3)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SunriseOrange.copy(0.06f), shape = RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Auto-contribute Standing Order", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SunriseOrange)
                                Text("Auto debit MWK ${chama.contributionAmountMwk} from wallet weekly to avoid pool delays.", fontSize = 11.sp, color = MutedTextGray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = isStandingOrderActive,
                                onCheckedChange = {
                                    isStandingOrderActive = it
                                    val msg = if (it) "Standing order activated for ${chama.name}." else "Standing order paused for ${chama.name}."
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = SunriseOrange, checkedTrackColor = SunriseOrange.copy(0.4f)),
                                modifier = Modifier.testTag("chama_standing_order_toggle")
                            )
                        }

                        Button(
                            onClick = { activeChamaDetail = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Close Detail Panel")
                        }
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 5: SELF-SERVE ADS
// -----------------------------------------------------------------
@Composable
fun SelfServeAdsTab(
    viewModel: DobadobaViewModel,
    listings: List<Post>,
    walletBalance: Double,
    context: android.content.Context
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📢 Self-Serve Spotlight Ads", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Promote listings to head of feeds for MK 2,000/week or run district campaigns targeting specific target regions under Malawi Red theme.", fontSize = 12.sp, color = Color.DarkGray)
                }
            }
        }

        item {
            Text("Boost items locally with Spotlight", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        val displayListings = listOf(
            MarketListing(1, "Premium Chitenge Attire", "Lilongwe tailored dress", 15000.0, "Tailor Queen", "0999123456", "Lilongwe"),
            MarketListing(2, "Hybrid Solar Lantern 🇲🇼", "High quality solar system", 22000.0, "Solar Star", "0888998877", "Blantyre"),
            MarketListing(3, "High Yield Organic Beans", "100kg bag", 45000.0, "Farmer Agri", "0999554433", "Mzuzu")
        )

        items(displayListings) { listing ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(listing.title, fontWeight = FontWeight.Bold)
                        Text(FeeCalculator.formatMwk(listing.priceMwk), fontSize = 12.sp, color = MalawiRed)
                    }
                    Button(
                        onClick = {
                            if (walletBalance < 2000.0) {
                                Toast.makeText(context, "Wallet balance too low!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.handleBoostMarketListing(listing)
                                Toast.makeText(context, "Boosted successfully for 7 days! Spotlight active.", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MalawiRed)
                    ) {
                        Text("Spotlight (MK 2,000)")
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 6: INSURANCE TAB
// -----------------------------------------------------------------
@Composable
fun InsuranceTab(
    viewModel: DobadobaViewModel,
    policies: List<PolicyEntity>,
    walletBalance: Double,
    context: android.content.Context
) {
    var insuranceName by remember { mutableStateOf("Limbani Banda") }
    var nrcNumber by remember { mutableStateOf("BT-1245-A67") }
    var beneficiaryPhone by remember { mutableStateOf("0999887766") }
    var selectedPlan by remember { mutableStateOf("Weather Index Crop Cover") } // Crop cover, Phone safeguard, Accident cover

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🛡️ DobaCover Microinsurance Partnership [NICO Life]", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Resilient agricultural weather index insurance crops cover protect your hard-earned investments from drought.", fontSize = 12.sp, color = Color.DarkGray)
                }
            }
        }

        item {
            Text("Purchase secure insurance policy cover", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        item {
            OutlinedTextField(
                value = insuranceName,
                onValueChange = { insuranceName = it },
                label = { Text("Insured Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = nrcNumber,
                onValueChange = { nrcNumber = it },
                label = { Text("NRC Number (National ID)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = beneficiaryPhone,
                onValueChange = { beneficiaryPhone = it },
                label = { Text("Beneficiary Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }

        val plans = listOf(
            "Weather Index Crop Cover" to 3500.0,
            "Major Hospital Cash safeguard" to 2500.0,
            "Motor/Phone Accident cover" to 1500.0
        )

        items(plans) { (name, premium) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { selectedPlan = name },
                border = BorderStroke(1.dp, if (selectedPlan == name) MalawiRed else Color.LightGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(name, fontWeight = FontWeight.Bold)
                        Text("Monthly Premium: ${FeeCalculator.formatMwk(premium)}", fontSize = 12.sp, color = Color.Gray)
                    }
                    RadioButton(
                        selected = selectedPlan == name,
                        onClick = { selectedPlan = name },
                        colors = RadioButtonDefaults.colors(selectedColor = MalawiRed)
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    val matchingPremium = plans.find { it.first == selectedPlan }?.second ?: 1500.0
                    if (walletBalance < matchingPremium) {
                        Toast.makeText(context, "Insufficient DobaPay wallet funds", Toast.LENGTH_SHORT).show()
                    } else {
                        val ok = viewModel.handlePurchaseInsurancePolicy(insuranceName, nrcNumber, beneficiaryPhone, selectedPlan, matchingPremium)
                        if (ok) {
                            Toast.makeText(context, "Policy issued under secure certificate!", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MalawiRed)
            ) {
                Text("Issue Smart Certificate Policy")
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Your Active Cover Certificates (${policies.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        val displayPolicies = policies.ifEmpty {
            listOf(
                PolicyEntity(1, "Limbani Banda", "ZA-6789-Z01", "0999580421", "Weather Index Crop Cover", 3500.0, "POLICY-REV-NUM-8241")
            )
        }

        items(displayPolicies) { policy ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(policy.productType, fontWeight = FontWeight.Bold)
                    Text("Insured: ${policy.fullName} (NRC: ${policy.nrcNumber})", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Hash: ${policy.pdfString}", color = Color.Gray, fontSize = 12.sp)
                        Text("Premium: ${FeeCalculator.formatMwk(policy.premiumMwk)}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------
// TAB 7: REVENUE ADMIN DASHBOARD
// -----------------------------------------------------------------
@Composable
fun RevenueAdminTab(
    viewModel: DobadobaViewModel,
    fees: List<FeeEntity>,
    chamas: List<ChamaEntity>,
    policies: List<PolicyEntity>,
    gifts: List<GiftEntity>
) {
    val totalAirtimeCommissions = fees.filter { it.associatedTransferId.startsWith("AIR-") }.sumOf { it.feeAmountMwk }
    val totalLiveGiftsPlatShare = fees.filter { it.associatedTransferId.startsWith("LF-GIFT-") }.sumOf { it.feeAmountMwk }
    val totalMarketEscrowFees = fees.filter { it.associatedTransferId.startsWith("ESCROW-FEE-") }.sumOf { it.feeAmountMwk }
    val totalSpotlightRevenue = fees.filter { it.associatedTransferId.startsWith("BOOST-") }.sumOf { it.feeAmountMwk }
    val totalAdsRevenue = fees.filter { it.associatedTransferId.startsWith("AD-") }.sumOf { it.feeAmountMwk }
    val overallRevenue = fees.sumOf { it.feeAmountMwk }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Admin Operations Consolidated Dashboard 📊", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MalawiRed)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Calculated Platform Revenue:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(FeeCalculator.formatMwk(overallRevenue), fontSize = 28.sp, fontWeight = FontWeight.Black, color = MalawiRed)
                    Text("Compiled from robust offline database queries & Workmanager sync logs", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }

        item {
            Text("Revenue Streams Breakdowns", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        val streams = listOf(
            "Airtime Sales Commissions (2% Franchise Margin)" to totalAirtimeCommissions,
            "Gifting Division (30% Platform Retainer)" to totalLiveGiftsPlatShare,
            "Spotlight Ads Placement Boost Premium (100% Client Pay)" to totalSpotlightRevenue,
            "Last Mile Delivery Escrow Platform Fee (1.5% Fee)" to totalMarketEscrowFees,
            "DobaAds Promoted Campaigns Processing (3% Fee)" to totalAdsRevenue
        )

        items(streams) { (label, amt) ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Text(FeeCalculator.formatMwk(amt), fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("All Platform Transaction Audit Logs (${fees.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        if (fees.isEmpty()) {
            item {
                Text("No audited logs present in DB yet. Initiate some payment flows above.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            items(fees) { fee ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ID: ${fee.associatedTransferId}", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            Text("Timestamp: ${fee.timestamp}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Text(FeeCalculator.formatMwk(fee.feeAmountMwk), fontWeight = FontWeight.Bold, color = MalawiRed)
                    }
                }
            }
        }
    }
}
