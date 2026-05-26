@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DobaSuiteSubTab(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val window = (context as? android.app.Activity)?.window
    DisposableEffect(Unit) {
        window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    val balanceMwk by viewModel.dobaPayBalance.collectAsState()
    val coinsBalance by viewModel.dobaCoinsBalance.collectAsState()
    val isJuniorModeActive by viewModel.isJuniorModeActive.collectAsState()

    var activeMiniApp by remember { mutableStateOf<String?>(null) } // Name of active app overlay

    val suiteApps = listOf(
        SuiteAppInfo("DobaPay", "Mobile Money Wallet", "Airtel Money, TNM Mpamba, split & tipping", Icons.Default.Wallet, SunriseOrange),
        SuiteAppInfo("DobaDeliver", "Last-Mile Escrow Delivery", "Verified local riders matching & escrow release", Icons.Default.LocalShipping, MalawiGreen),
        SuiteAppInfo("DobaGames", "Casual Games Arcade", "Chichewa Word scramble, football trivia & picture quiz", Icons.Default.SportsEsports, GoldYellow),
        SuiteAppInfo("DobaKaraoke", "Social Karaoke Recorder", "Record backing videos & share on weekly leaderboard", Icons.Default.Mic, MalawiRed),
        SuiteAppInfo("Predict & Win", "Sports Fixtures Predictions", "TNM Super league, AFCON, EPL pickers", Icons.Default.SportsSoccer, Color(0xFF0284C7)),
        SuiteAppInfo("DobaRadio", "Voice-Only Live Rooms", "Live voice discussions up to 500 listeners", Icons.Default.Radio, Color(0xFF8B5CF6)),
        SuiteAppInfo("DobaNeighbour", "Hyperlocal District Feed", "Village road alerts, township lost-and-found", Icons.Default.HomeWork, Color(0xFFEC4899)),
        SuiteAppInfo("Chichewa Keyboard", "Smart Phrases & Autocompletes", "Culturally curated stickers & chichewa slang", Icons.Default.Keyboard, Color(0xFF14B8A6)),
        SuiteAppInfo("DobaLearn", "Expert Educational Channel", "Farming, sewing, coding tutorials with downloads", Icons.Default.School, Color(0xFFF97316)),
        SuiteAppInfo("DobaCause", "Zero Fee Crowdfunding", "Emergency local campaigns with transparent log ledgers", Icons.Default.VolunteerActivism, Color(0xFF84CC16)),
        SuiteAppInfo("DobaChama & Coins", "Rotational Savings & Coins Shop", "Premium coins top-up, airtime reseller franchise, standing orders & Chamas", Icons.Default.Star, MalawiRed)
    )

    val isLight = MaterialTheme.colorScheme.background == SunriseWarm

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Dashboard Wallet Balance card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isLight) MaterialTheme.colorScheme.surface else ChambaSlateLight
                ),
                border = BorderStroke(1.dp, if (isLight) BorderGray.copy(0.4f) else Color(0xFF2E333D)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DOBAPAY WALLET 💳",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedTextGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "MWK ${String.format("%,.2f", balanceMwk)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = SunriseOrange
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldYellow.copy(0.12f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = "DobaCoins", tint = GoldYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$coinsBalance Coins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLight) MalawiBlack else Color.White
                            )
                        }
                    }
                }
            }

            Text(
                text = "MALAWIAN DOBASUITE (PHASE 2)",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MutedTextGray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Grid of Phase 2 Mini Apps
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(suiteApps) { app ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable {
                                if (isJuniorModeActive && (app.name == "DobaPay" || app.name == "DobaDeliver" || app.name == "DobaCause" || app.name == "Predict & Win")) {
                                    Toast.makeText(context, "DobaJunior Child Lock Active! Wallet, delivery, crowdfunding, and predictions are restricted.", Toast.LENGTH_LONG).show()
                                } else {
                                    activeMiniApp = app.name
                                }
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isLight) 1.dp else 2.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isLight) BorderGray.copy(0.5f) else Color(0xFF2E333D)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(app.accentColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = app.icon,
                                    contentDescription = app.name,
                                    tint = app.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = app.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = app.summary,
                                    fontSize = 10.sp,
                                    color = MutedTextGray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Full-screen Mini-app overlays
        AnimatedVisibility(
            visible = activeMiniApp != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .clickable(enabled = false) {} // block touch-through
            ) {
                activeMiniApp?.let { appName ->
                    Scaffold(
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        text = appName.uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        letterSpacing = 1.sp,
                                        color = SunriseOrange
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { activeMiniApp = null }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (appName) {
                                "DobaPay" -> DobaPayScreen(viewModel = viewModel)
                                "DobaDeliver" -> DobaDeliverScreen(viewModel = viewModel)
                                "DobaGames" -> DobaGamesScreen(viewModel = viewModel)
                                "DobaKaraoke" -> DobaKaraokeScreen(viewModel = viewModel)
                                "Predict & Win" -> PredictWinScreen(viewModel = viewModel)
                                "DobaRadio" -> DobaRadioScreen(viewModel = viewModel)
                                "DobaNeighbour" -> DobaNeighbourScreen(viewModel = viewModel)
                                "Chichewa Keyboard" -> ChichewaKeyboardScreen(viewModel = viewModel)
                                "DobaLearn" -> DobaLearnScreen(viewModel = viewModel)
                                "DobaCause" -> DobaCauseScreen(viewModel = viewModel)
                                "DobaChama & Coins" -> DobaChamaAndCoinsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class SuiteAppInfo(
    val name: String,
    val title: String,
    val summary: String,
    val icon: ImageVector,
    val accentColor: Color
)

// ==========================================
// 1. DOBAPAY SCREEN
// ==========================================
@Composable
fun DobaPayScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val window = (context as? android.app.Activity)?.window
    DisposableEffect(Unit) {
        window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    val isJuniorModeActive by viewModel.isJuniorModeActive.collectAsState()
    val balanceMwk by viewModel.dobaPayBalance.collectAsState()
    val transactions by viewModel.dobaPayTransactions.collectAsState()
    val dailyLimit by viewModel.dobaPayDailyLimit.collectAsState()

    var activeTab by remember { mutableStateOf("Transfer") } // Transfer or Split or Limits & History

    var targetNumber by remember { mutableStateOf("") }
    var sendAmountText by remember { mutableStateOf("") }
    var selectedWalletOption by remember { mutableStateOf("Airtel Money") }
    var pinEntered by remember { mutableStateOf("") }

    var groupSplitAmountText by remember { mutableStateOf("15000") }
    var numFriendsToSplit by remember { mutableStateOf(4f) }

    val isLight = MaterialTheme.colorScheme.background == SunriseWarm

    if (isJuniorModeActive) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("junior_pay_locked_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SunriseOrange.copy(0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Wallet Locked",
                        tint = SunriseOrange,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "DobaPay Restricted 🔒",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SunriseOrange)
                    )
                    Text(
                        text = "Parental safety guidelines are active. Direct wallet balances, split costs, transfers, and billing are locked inside DobaJunior Mode.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedTextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SunriseOrange.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "To disable restrictions, open the top-right Profile Hub and enter your Parental PIN.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = SunriseOrange,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Pay tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Transfer", "Split Expense", "History").forEach { tab ->
                val isSel = activeTab == tab
                Button(
                    onClick = { activeTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSel) SunriseOrange else MaterialTheme.colorScheme.surface,
                        contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(tab, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (activeTab) {
            "Transfer" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Send Money (No Bank Required 🇲🇼)", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Text("Choose Network Operator:", fontSize = 11.sp, color = MutedTextGray)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("Airtel Money", "TNM Mpamba").forEach { option ->
                                val isOptSel = selectedWalletOption == option
                                OutlinedButton(
                                    onClick = { selectedWalletOption = option },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isOptSel) SunriseOrange.copy(0.12f) else Color.Transparent
                                    ),
                                    border = BorderStroke(1.dp, if (isOptSel) SunriseOrange else BorderGray),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(option, fontSize = 12.sp, color = if (isOptSel) SunriseOrange else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = targetNumber,
                            onValueChange = { targetNumber = it },
                            label = { Text("Recipient Phone (+265)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = sendAmountText,
                            onValueChange = { sendAmountText = it },
                            label = { Text("Amount in MWK") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = pinEntered,
                            onValueChange = { pinEntered = it },
                            label = { Text("Enter Wallet PIN (Default: 1234)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val amt = sendAmountText.toDoubleOrNull() ?: 0.0
                                if (targetNumber.length < 9) {
                                    Toast.makeText(context, "Chonde! Enter valid phone standard length.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val ok = viewModel.executeSendMoney(targetNumber, amt, selectedWalletOption, pinEntered)
                                if (ok) {
                                    Toast.makeText(context, "Kutumiza kwatha! K${String.format("%,.0f", amt)} sent to $targetNumber.", Toast.LENGTH_LONG).show()
                                    targetNumber = ""
                                    sendAmountText = ""
                                    pinEntered = ""
                                } else {
                                    Toast.makeText(context, "Ndalama zochepa, account limit, kapena PIN yolosera yolakwika!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Confirm & Authorise Transfer", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            "Split Expense" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Instant Group Bill Splitter ➗", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Ideal for splitting local meal, taxi fare, or maize group purchases with friends on DobaChat.", fontSize = 11.sp, color = MutedTextGray)

                        OutlinedTextField(
                            value = groupSplitAmountText,
                            onValueChange = { groupSplitAmountText = it },
                            label = { Text("Total Bill Amount (MWK)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Split between ${numFriendsToSplit.toInt()} friends:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = numFriendsToSplit,
                            onValueChange = { numFriendsToSplit = it },
                            valueRange = 2f..10f,
                            steps = 7,
                            colors = SliderDefaults.colors(activeTrackColor = SunriseOrange, thumbColor = SunriseOrange)
                        )

                        val totalBill = groupSplitAmountText.toDoubleOrNull() ?: 0.0
                        val splitValue = totalBill / numFriendsToSplit.toInt()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MalawiGreen.copy(0.12f), shape = RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Each Friend Pays:", fontSize = 11.sp, color = MalawiGreen, fontWeight = FontWeight.Bold)
                                Text("MWK ${String.format("%,.2f", splitValue)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MalawiGreen)
                            }
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Group cost requests sent out in Chonde Chat room!", Toast.LENGTH_LONG).show()
                                viewModel.addDobaTransaction("Cost Split Request initiated", -splitValue)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Request Splits on DobaChat", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            "History" -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Daily limit adjuster
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Configure Daily Limit Security 🛡️", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Limit: MWK ${String.format("%,.0f", dailyLimit)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SunriseOrange)
                            Slider(
                                value = dailyLimit.toFloat(),
                                onValueChange = { viewModel.dobaPayDailyLimit.value = it.toDouble() },
                                valueRange = 5000f..100000f,
                                colors = SliderDefaults.colors(activeTrackColor = SunriseOrange, thumbColor = SunriseOrange)
                            )
                        }
                    }

                    // Ledger
                    Text("Wallet Transaction Ledger (Local)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MutedTextGray)
                    transactions.forEach { txn ->
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
                                    Text(txn.description, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${txn.dateString} • ${txn.id}", fontSize = 10.sp, color = MutedTextGray)
                                }
                                Text(
                                    text = (if (txn.isCredit) "+" else "") + "MWK ${String.format("%,.0f", txn.amountMwk)}",
                                    color = if (txn.isCredit) MalawiGreen else MalawiRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. DOBADELIVER SCREEN
// ==========================================
@Composable
fun DobaDeliverScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val activeDeliveries by viewModel.activeDeliveries.collectAsState()
    val deliveryRadius by viewModel.deliveryRadiusKm.collectAsState()
    val deliveryFeeMwk by viewModel.deliveryFeeMwk.collectAsState()

    val isLight = MaterialTheme.colorScheme.background == SunriseWarm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("DobaMarket Last-Mile Delivery Settings 📦", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Sellers configure delivery parameters below to auto-match verified local riders.", fontSize = 11.sp, color = MutedTextGray)

                Text("Delivery Services Radius: $deliveryRadius km", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = deliveryRadius.toFloat(),
                    onValueChange = { viewModel.deliveryRadiusKm.value = it.toInt() },
                    valueRange = 5f..50f,
                    colors = SliderDefaults.colors(activeTrackColor = MalawiGreen, thumbColor = MalawiGreen)
                )

                Text("Standard Fee: MWK ${String.format("%,.0f", deliveryFeeMwk)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = deliveryFeeMwk.toFloat(),
                    onValueChange = { viewModel.deliveryFeeMwk.value = it.toDouble() },
                    valueRange = 1000f..5000f,
                    colors = SliderDefaults.colors(activeTrackColor = MalawiGreen, thumbColor = MalawiGreen)
                )
            }
        }

        Text("Active Delivery Escrow Tracking (Real-Time) 🗺️", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MutedTextGray)

        activeDeliveries.forEach { delivery ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(delivery.id, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MutedTextGray)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (delivery.speedStatus.contains("Delivered")) MalawiGreen.copy(0.12f)
                                    else SunriseOrange.copy(0.12f)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = delivery.speedStatus,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (delivery.speedStatus.contains("Delivered")) MalawiGreen else SunriseOrange
                            )
                        }
                    }

                    Text(delivery.itemName, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Text("Destination: ${delivery.destination}", fontSize = 11.sp, color = MutedTextGray)

                    HorizontalDivider(color = if (isLight) BorderGray else Color(0xFF2E333D))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Assigned Rider:", fontSize = 10.sp, color = MutedTextGray)
                            Text("${delivery.assignedRider} • ${delivery.riderPhone}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("Fee: MWK ${String.format("%,.0f", delivery.deliveryFeeMwk)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (!delivery.speedStatus.contains("Delivered")) {
                        Button(
                            onClick = {
                                viewModel.confirmDeliveryReceived(delivery.id)
                                Toast.makeText(context, "Zikomo! Escrow payment of MWK ${String.format("%,.0f", delivery.deliveryFeeMwk)} released to ${delivery.assignedRider}.", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Text("Confirm Delivery & Release Escrow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. DOBAGAMES SCREEN (CASUAL 50KB ARCADE)
// ==========================================
@Composable
fun DobaGamesScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val balanceCoins by viewModel.dobaCoinsBalance.collectAsState()

    var activeGameMode by remember { mutableStateOf("Trivia") } // Trivia, Word Puzzle, Picture Quiz

    // Trivia Game State Local
    val triviaIndex by viewModel.currentTriviaIndex.collectAsState()
    val triviaScore by viewModel.triviaScore.collectAsState()
    var selectedTriviaAnswer by remember { mutableStateOf<Int?>(null) }
    var showTriviaExplanation by remember { mutableStateOf(false) }

    // Word Scramble State Local
    val wordPuzzleUnscrambled by viewModel.wordPuzzleUnscrambled.collectAsState()
    val activePuzzleWord = viewModel.activePuzzleWord
    var hasSolvedScramble by remember { mutableStateOf(false) }

    // Picture Quiz State Local
    val pictureQuizGuess by viewModel.pictureQuizGuess.collectAsState()
    val activePictureQuiz = viewModel.activePictureQuiz
    var hasSolvedPictureQuiz by remember { mutableStateOf(false) }

    val isLight = MaterialTheme.colorScheme.background == SunriseWarm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Arcade header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GoldYellow.copy(0.1f)),
            border = BorderStroke(1.dp, GoldYellow)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("DOBAGAMES MINI-ARCADE 🕹️", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = if (isLight) MalawiBlack else Color.White)
                    Text("Casual retro games optimized for 2D/2G connections under 50KB!", fontSize = 10.sp, color = MutedTextGray)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldYellow)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("$balanceCoins Coins", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MalawiBlack)
                }
            }
        }

        // Mini games tab selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Trivia", "Word Scramble", "Picture Quiz").forEach { mode ->
                val isSel = activeGameMode == mode
                Button(
                    onClick = { activeGameMode = mode },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSel) GoldYellow else MaterialTheme.colorScheme.surface,
                        contentColor = if (isSel) MalawiBlack else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(mode, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (activeGameMode) {
            "Trivia" -> {
                if (triviaIndex < viewModel.triviaQuizList.size) {
                    val activeQuestion = viewModel.triviaQuizList[triviaIndex]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Malawian Trivia: Q${triviaIndex + 1} of ${viewModel.triviaQuizList.size}", fontWeight = FontWeight.Bold, color = MutedTextGray, fontSize = 11.sp)
                            Text(activeQuestion.question, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)

                            activeQuestion.options.forEachIndexed { optIndex, optText ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (!showTriviaExplanation) {
                                                selectedTriviaAnswer = optIndex
                                            }
                                        }
                                        .background(
                                            if (selectedTriviaAnswer == optIndex) GoldYellow.copy(0.12f)
                                            else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedTriviaAnswer == optIndex,
                                        onClick = { if (!showTriviaExplanation) selectedTriviaAnswer = optIndex },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldYellow)
                                    )
                                    Text(optText, fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp))
                                }
                            }

                            if (!showTriviaExplanation) {
                                Button(
                                    onClick = {
                                        if (selectedTriviaAnswer == null) {
                                            Toast.makeText(context, "Chonde sankhani mmodzi!", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        showTriviaExplanation = true
                                        if (selectedTriviaAnswer == activeQuestion.correctIndex) {
                                            viewModel.triviaScore.value++
                                            viewModel.awardDobaCoins(20)
                                            Toast.makeText(context, "Zolondola! +20 DobaCoins! 🎉", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Zolakwika! Koma muphunzira mavesi atsopano.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Submit Answer", color = MalawiBlack, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (selectedTriviaAnswer == activeQuestion.correctIndex) MalawiGreen.copy(0.1f)
                                                else MalawiRed.copy(0.1f),
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (selectedTriviaAnswer == activeQuestion.correctIndex) MalawiGreen else MalawiRed,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Text(activeQuestion.factExplanation, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.currentTriviaIndex.value++
                                            selectedTriviaAnswer = null
                                            showTriviaExplanation = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Next Question")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Trivia Completed! 🏆", fontWeight = FontWeight.Black, fontSize = 18.sp, color = GoldYellow)
                            Text("Your Score: $triviaScore out of ${viewModel.triviaQuizList.size}", fontSize = 14.sp)
                            Button(
                                onClick = {
                                    viewModel.currentTriviaIndex.value = 0
                                    viewModel.triviaScore.value = 0
                                    selectedTriviaAnswer = null
                                    showTriviaExplanation = false
                                }
                            ) {
                                Text("Play Again")
                             }
                        }
                    }
                }
            }

            "Word Scramble" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Unscramble the Malawian Town/Word! ✏️", fontWeight = FontWeight.Bold)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GoldYellow.copy(0.12f), shape = RoundedCornerShape(12.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(activePuzzleWord.scrambled, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = GoldYellow)
                        }

                        OutlinedTextField(
                            value = wordPuzzleUnscrambled,
                            onValueChange = { viewModel.wordPuzzleUnscrambled.value = it },
                            label = { Text("Your Scramble Solve (Chichewa/Location)") },
                            singleLine = true,
                            enabled = !hasSolvedScramble,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (!hasSolvedScramble) {
                            Button(
                                onClick = {
                                    if (wordPuzzleUnscrambled.uppercase().trim() == activePuzzleWord.actual) {
                                        hasSolvedScramble = true
                                        viewModel.awardDobaCoins(15)
                                        Toast.makeText(context, "Mwathetsa bwinobwino! solved: ${activePuzzleWord.actual}. +15 DobaCoins!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Kulosera kwalakwika, yesesani mzache!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                        Text("Submit Word Choice", color = MalawiBlack, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MalawiGreen.copy(0.1f), shape = RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text("Zolondola kwambiri! Clue/Fact: ${activePuzzleWord.chichewaClueOnCorrect}", fontSize = 12.sp, color = MalawiGreen)
                            }
                        }
                    }
                }
            }

            "Picture Quiz" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("Low-Data Landmark Picture Quiz 🗺️", fontWeight = FontWeight.Bold)

                        // Placeholder vector drawing matching low connection guidelines
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(listOf(SunriseOrange.copy(0.1f), GoldYellow.copy(0.15f)))
                                )
                                .border(1.dp, BorderGray.copy(0.3f), shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CloudQueue, contentDescription = "Water bodies symbol", tint = SunriseOrange, modifier = Modifier.size(36.dp))
                                Text("LANDMARK CLUE: fresh water lake", fontSize = 10.sp, color = MutedTextGray)
                            }
                        }

                        Text("Clue: ${activePictureQuiz.clue}", fontSize = 11.sp, color = MutedTextGray)

                        OutlinedTextField(
                            value = pictureQuizGuess,
                            onValueChange = { viewModel.pictureQuizGuess.value = it },
                            label = { Text("Which landmark is this? (caps lock suggested)") },
                            singleLine = true,
                            enabled = !hasSolvedPictureQuiz,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (!hasSolvedPictureQuiz) {
                            Button(
                                onClick = {
                                    if (pictureQuizGuess.uppercase().trim() == activePictureQuiz.correctWord) {
                                        hasSolvedPictureQuiz = true
                                        viewModel.awardDobaCoins(15)
                                        Toast.makeText(context, "Zoona! +15 DobaCoins for finding: ${activePictureQuiz.correctWord}", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Ayi! Yesani tsopano.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Verify Landmark", color = MalawiBlack, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MalawiGreen.copy(0.1f), shape = RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Text("Awesome guess! Lake Malawi holds 10% of global fresh water fish diversity!", fontSize = 12.sp, color = MalawiGreen)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. DOBAKARAOKE SCREEN
// ==========================================
@Composable
fun DobaKaraokeScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val tracks = viewModel.karaokeTracks
    val selectedTrack by viewModel.selectedKaraokeTrack.collectAsState()
    val isRecording by viewModel.isRecordingKaraoke.collectAsState()
    val progress by viewModel.recordingProgress.collectAsState()
    val videos by viewModel.savedSingingVideos.collectAsState()

    var activeViewTab by remember { mutableStateOf("Tracks") } // Tracks or Live Ranking

    val isLight = MaterialTheme.colorScheme.background == SunriseWarm
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Tracks", "Weekly Leaderboard").forEach { tab ->
                val isSel = activeViewTab == tab
                Button(
                    onClick = { activeViewTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSel) MalawiRed else MaterialTheme.colorScheme.surface,
                        contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(tab, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        when (activeViewTab) {
            "Tracks" -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Record Your Singing (Server-Side Audio Output) 🎙️", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Select a backing track to record a 15-second musical clip.", fontSize = 11.sp, color = MutedTextGray)

                        tracks.forEach { track ->
                            val isSel = selectedTrack == track
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectedKaraokeTrack.value = track }
                                    .background(
                                        if (isSel) MalawiRed.copy(0.1f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(track.trackTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${track.genre} • ${track.duration}", fontSize = 10.sp, color = MutedTextGray)
                                }
                                RadioButton(
                                    selected = isSel,
                                    onClick = { viewModel.selectedKaraokeTrack.value = track },
                                    colors = RadioButtonDefaults.colors(selectedColor = MalawiRed)
                                )
                            }
                        }

                        if (isRecording) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Recording Video on Server Backings... 🎤", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MalawiRed)
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MalawiRed
                                )
                                VoiceWaveformRepresentation() // visual helper from common
                            }
                        }

                        Button(
                            onClick = {
                                if (!isRecording) {
                                    viewModel.isRecordingKaraoke.value = true
                                    viewModel.recordingProgress.value = 0.0f
                                    scope.launch {
                                        for (i in 1..20) {
                                            kotlinx.coroutines.delay(150)
                                            viewModel.recordingProgress.value = i / 20.0f
                                        }
                                        viewModel.isRecordingKaraoke.value = false
                                        val list = viewModel.savedSingingVideos.value.toMutableList()
                                        list.add(0, SingingVideo("Ine (My Voice) 🇲🇼", selectedTrack.trackTitle, 1, "👏🤩"))
                                        viewModel.savedSingingVideos.value = list
                                    }
                                } else {
                                    viewModel.isRecordingKaraoke.value = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isRecording) MutedTextGray else MalawiRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isRecording) "Cancel / Save segment" else "Start Video Record", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            "Weekly Leaderboard" -> {
                Text("Weekly Singing Leaderboard (Malawi Votes) 🏆", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                videos.forEachIndexed { idx, vid ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("#${idx + 1}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = SunriseOrange, modifier = Modifier.padding(end = 12.dp))
                                Column {
                                    Text(vid.singerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(vid.backingTrackPlayed, fontSize = 11.sp, color = MutedTextGray)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${vid.friendVoteCount} Votes ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Button(
                                    onClick = {
                                        val update = viewModel.savedSingingVideos.value.map {
                                            if (it.singerName == vid.singerName) {
                                                it.copy(friendVoteCount = it.friendVoteCount + 1)
                                            } else it
                                        }
                                        viewModel.savedSingingVideos.value = update
                                        Toast.makeText(context, "Mwavota! Voted for ${vid.singerName}.", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MalawiRed),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Vote", fontSize = 10.sp)
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
// 5. PREDICT & WIN SCREEN (purely social)
// ==========================================
@Composable
fun PredictWinScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val predictions by viewModel.currentPredictions.collectAsState()
    val coinsBalance by viewModel.dobaCoinsBalance.collectAsState()

    val isLight = MaterialTheme.colorScheme.background == SunriseWarm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Sports Predict & Win (No Real Gambling) ⚽", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text("Predict local Super League fixtures, AFCON or EPL outcomes. Correct picks earn DobaCoins to redeem premium Profile badges or sticker packs!", fontSize = 11.sp, color = MutedTextGray)
            }
        }

        predictions.forEach { fix ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(fix.tourName.uppercase() + " • " + fix.dateText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MutedTextGray)
                    Text(fix.fixName, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)

                    if (fix.prediction == "Not Predicted") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Home Win", "Draw", "Away Win").forEach { opt ->
                                OutlinedButton(
                                    onClick = {
                                        viewModel.submitFixturePrediction(fix.id, opt)
                                        Toast.makeText(context, "Prediction submitted! +15 DobaCoins!", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(opt, fontSize = 10.sp, maxLines = 1)
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MalawiGreen.copy(0.12f), shape = RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text("Your Prediction: ${fix.prediction} (Valid prediction submitted) ✅", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MalawiGreen)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. DOBA_RADIO SCREEN
// ==========================================
@Composable
fun DobaRadioScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val rooms by viewModel.liveRadioRooms.collectAsState()
    val isHandRaised by viewModel.isHandRaised.collectAsState()

    var activeOnStage by remember { mutableStateOf<RadioRoom?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(0.12f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("DobaRadio - Live Voice Spaces 📻", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Voice-compressed live audio broadcast rooms engineered for minimal data usage.", fontSize = 11.sp, color = MutedTextGray)
            }
        }

        if (activeOnStage != null) {
            val liveRoom = activeOnStage!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SunriseOrange)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ACTIVE BROADCAST ROOM 🔴", fontSize = 11.sp, color = SunriseOrange, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { activeOnStage = null },
                            colors = ButtonDefaults.buttonColors(containerColor = MalawiRed),
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Leave Stage", fontSize = 10.sp)
                        }
                    }

                    Text(liveRoom.title, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Text(liveRoom.hostDesc, fontSize = 12.sp, color = MutedTextGray)

                    VoiceWaveformRepresentation() // nice graphics representation

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MalawiGreen))
                        Text("${liveRoom.listeners + (if (isHandRaised) 1 else 0)} listening live", fontSize = 11.sp, color = MutedTextGray)
                    }

                    Button(
                        onClick = {
                            viewModel.isHandRaised.value = !isHandRaised
                            if (!isHandRaised) {
                                Toast.makeText(context, "You requested to join the stage as speaker!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isHandRaised) MalawiGreen else MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isHandRaised) "Stage Hand Raised (Requesting)" else "Raise Hand to Speak on Stage")
                    }
                }
            }
        } else {
            rooms.forEach { room ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeOnStage = room },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(room.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(room.hostDesc, fontSize = 11.sp, color = MutedTextGray)
                            Text("${room.listeners} listeners", fontSize = 10.sp, color = MutedTextGray)
                        }
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play voice audio", tint = SunriseOrange)
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. DOBANEIGHBOUR SCREEN
// ==========================================
@Composable
fun DobaNeighbourScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val radius by viewModel.neighbourRadiusKm.collectAsState()
    val selectedDistrict by viewModel.userSelectedDistrict.collectAsState()
    val posts by viewModel.neighbourPosts.collectAsState()

    var isAddingPost by remember { mutableStateOf(false) }
    var inputPostTitle by remember { mutableStateOf("") }
    var inputPostBody by remember { mutableStateOf("") }

    val isLight = MaterialTheme.colorScheme.background == SunriseWarm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("DobaNeighbour - GPS District Post Feed 🏡", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Requires location permission. Filter community notices by township radius parameters.", fontSize = 11.sp, color = MutedTextGray)

                Text("Selected Township/District:", fontSize = 11.sp, color = MutedTextGray)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.defaultDistrictListText.forEach { tube ->
                        val isSel = selectedDistrict == tube
                        Button(
                            onClick = { viewModel.userSelectedDistrict.value = tube },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) SunriseOrange else MaterialTheme.colorScheme.surface,
                                contentColor = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(tube, fontSize = 11.sp)
                        }
                    }
                }

                Text("Township Sweep Radius: ${String.format("%.0f", radius)} km", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = radius,
                    onValueChange = { viewModel.neighbourRadiusKm.value = it },
                    valueRange = 1f..50f,
                    colors = SliderDefaults.colors(activeTrackColor = SunriseOrange, thumbColor = SunriseOrange)
                )
            }
        }

        Button(
            onClick = { isAddingPost = !isAddingPost },
            colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isAddingPost) "Close Composer" else "Broadcast Community Alert in $selectedDistrict")
        }

        if (isAddingPost) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = inputPostTitle,
                        onValueChange = { inputPostTitle = it },
                        label = { Text("Alert/Notice Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputPostBody,
                        onValueChange = { inputPostBody = it },
                        label = { Text("Type township incident details...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            viewModel.addNeighbourPost(inputPostTitle, inputPostBody, selectedDistrict)
                            Toast.makeText(context, "Alert Broadcast sent successfully!", Toast.LENGTH_SHORT).show()
                            inputPostTitle = ""
                            inputPostBody = ""
                            isAddingPost = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Publish Broadcast Notification")
                    }
                }
            }
        }

        posts.filter { it.district == selectedDistrict }.forEach { notice ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(notice.district.uppercase(), fontWeight = FontWeight.Bold, color = SunriseOrange, fontSize = 10.sp)
                        Text("Active Alert", fontSize = 9.sp, color = MutedTextGray)
                    }

                    Text(notice.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Text(notice.bodyText, fontSize = 12.sp, lineHeight = 16.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("By: ${notice.publisher}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MutedTextGray)
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. CHICHEWA KEYBOARD & STICKERS
// ==========================================
@Composable
fun ChichewaKeyboardScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val phrases = viewModel.sampleChichewaDictionary
    val stickers = viewModel.availableStickers

    var typedInputText by remember { mutableStateOf("") }
    var smartAutocorrectSuggestion by remember { mutableStateOf("") }

    val isLight = MaterialTheme.colorScheme.background == SunriseWarm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Chichewa Autocorrect Smart Keyboard Demo ⌨️", fontWeight = FontWeight.Bold)
                Text("Smart predictive keyboard engine. Type Chichewa letters below or tap suggestions bar to fill instantly.", fontSize = 11.sp, color = MutedTextGray)
            }
        }

        OutlinedTextField(
            value = typedInputText,
            onValueChange = {
                typedInputText = it
                // Simple prediction mapping matching under 50kb connection
                smartAutocorrectSuggestion = when {
                    it.lowercase().startsWith("mu") -> "Muli bwanji?"
                    it.lowercase().startsWith("zi") -> "Zikomo kwambiri!"
                    it.lowercase().startsWith("nd") -> "Ndili bwino kupita patsogolo."
                    it.lowercase().startsWith("cho") -> "Chonde ndithandizeni."
                    else -> ""
                }
            },
            label = { Text("Smart Keyboard Typing Emulator") },
            modifier = Modifier.fillMaxWidth()
        )

        if (smartAutocorrectSuggestion.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.12f))
                    .clickable {
                        typedInputText = smartAutocorrectSuggestion
                        smartAutocorrectSuggestion = ""
                    }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Suggestion Click-to-Fill: $smartAutocorrectSuggestion", fontWeight = FontWeight.Bold, color = SunriseOrange, fontSize = 12.sp)
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Autofill phrase input", tint = SunriseOrange)
            }
        }

        Text("Culturally Curated Phrases dictionary:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MutedTextGray)

        phrases.forEach { phrase ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        typedInputText = phrase.phraseText
                        Toast.makeText(context, phrase.translation, Toast.LENGTH_SHORT).show()
                    },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(phrase.phraseText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Translate: ${phrase.translation}", fontSize = 11.sp, color = MutedTextGray)
                }
            }
        }

        Text("Culturally Rich Chichewa Stickers (Under 20KB) 🎭", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MutedTextGray)

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            stickers.forEach { sticker ->
                Card(
                    modifier = Modifier
                        .size(110.dp)
                        .clickable {
                            Toast.makeText(context, "Sticker copied to clipboard: ${sticker.rawVisualText}", Toast.LENGTH_SHORT).show()
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isLight) BorderGray else Color(0xFF2E333D))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(sticker.rawVisualText, fontSize = 24.sp, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(sticker.cleanDesc, fontSize = 9.sp, color = MutedTextGray, textAlign = TextAlign.Center, lineHeight = 10.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. DOBALEARN SCREEN
// ==========================================
@Composable
fun DobaLearnScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val tutorials by viewModel.educationalTutorials.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(0.12f))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("DobaLearn Educational Channel 🎓", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Short tutorials curated by Malawian experts. Submitting verified expert videos is moderated by an editorial council in Lilongwe.", fontSize = 11.sp, color = MutedTextGray)
            }
        }

        tutorials.forEach { t ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TUTORIAL SERIES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SunriseOrange)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (t.isDownloaded) MalawiGreen.copy(0.12f) else MutedTextGray.copy(0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (t.isDownloaded) "Dowloaded Offline" else "Online Streams",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (t.isDownloaded) MalawiGreen else MutedTextGray
                            )
                        }
                    }

                    Text(t.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Text(t.summary, fontSize = 12.sp, color = MutedTextGray, lineHeight = 16.sp)
                    Text("By: ${t.expertCreatorDesc}", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.toggleOfflineDownloadTutorial(t.id)
                                Toast.makeText(context, if (!t.isDownloaded) "Saved to local DobaLearn offline cache!" else "Removed from offline cache", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (t.isDownloaded) MalawiRed else MalawiGreen
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (t.isDownloaded) "Remove Cash Cache" else "Download Offline (2MB)")
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Streaming tutorial video loop segment...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Play Video")
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 10. DOBACAUSE SCREEN
// ==========================================
@Composable
fun DobaCauseScreen(viewModel: DobadobaViewModel) {
    val context = LocalContext.current
    val causes by viewModel.dobaCauses.collectAsState()
    val contribLogs by viewModel.dobaCauseLogs.collectAsState()

    var activeContributionTarget by remember { mutableStateOf<DobaCauseItem?>(null) }
    var inputDonationText by remember { mutableStateOf("5000") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MalawiGreen.copy(0.1f)),
            border = BorderStroke(1.dp, MalawiGreen)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("DobaCause - Transparent Crowdfund 🤝", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Zero (0%) platform fee emergency community crowdfund. Monitored with a public contribution ledger.", fontSize = 11.sp, color = MutedTextGray)
            }
        }

        if (activeContributionTarget != null) {
            val target = activeContributionTarget!!
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MalawiGreen)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("ACTIVE DONATION PIPELINE", fontSize = 10.sp, color = MalawiGreen, fontWeight = FontWeight.Bold)
                    Text(target.title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)

                    OutlinedTextField(
                        value = inputDonationText,
                        onValueChange = { inputDonationText = it },
                        label = { Text("Donation Amount (MWK)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { activeContributionTarget = null },
                            colors = ButtonDefaults.buttonColors(containerColor = MutedTextGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val amtNum = inputDonationText.toDoubleOrNull() ?: 0.0
                                val ok = viewModel.donorTipCause(target.id, amtNum, "Integrated DobaPay Wallet")
                                if (ok) {
                                    Toast.makeText(context, "Zikomo! K${String.format("%,.0f", amtNum)} has been contributed successfully and logged publicly.", Toast.LENGTH_LONG).show()
                                    activeContributionTarget = null
                                } else {
                                    Toast.makeText(context, "Failed! Double check balance in your DobaPay wallet.", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Confirm Donation")
                        }
                    }
                }
            }
        } else {
            causes.forEach { cause ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(cause.verifierTeamStub.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MutedTextGray)
                        Text(cause.title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Text(cause.description, fontSize = 12.sp, color = MutedTextGray)

                        // Progress math
                        val progressPercent = (cause.currentMwk / cause.goalMwk).coerceIn(0.0, 1.0).toFloat()
                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier.fillMaxWidth(),
                            color = MalawiGreen
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Raised: K${String.format("%,.0f", cause.currentMwk)}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MalawiGreen)
                            Text("Goal: K${String.format("%,.0f", cause.goalMwk)}", fontSize = 11.sp, color = MutedTextGray)
                        }

                        Button(
                            onClick = { activeContributionTarget = cause },
                            colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Contribute (via DobaPay)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text("Public Real-Time Contribution Ledger", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MutedTextGray)
            contribLogs.forEach { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(log.contributionLine, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(log.causeId, fontSize = 10.sp, color = MutedTextGray)
                    }
                }
            }
        }
    }
}
