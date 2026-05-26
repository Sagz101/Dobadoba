package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.feed.FeedTab
import com.example.ui.discover.DiscoverTab
import com.example.ui.chat.ChatTab
import com.example.ui.profile.ProfileTab
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkThemeMode by remember { mutableStateOf(false) } // default light, bright, open and modern style
            var isDobaPlusActive by remember { mutableStateOf(false) } // premium monetisation feature!

            MyApplicationTheme(darkTheme = darkThemeMode, dynamicColor = false) {
                val dbViewModel: DobadobaViewModel = viewModel()
                val isOnboarded by dbViewModel.isOnboarded.collectAsState()
                val currentTab by dbViewModel.currentTab.collectAsState()
                val userLang by dbViewModel.currentLanguage.collectAsState()
                val context = LocalContext.current

                // Check and restore secure persisted user session on startup
                LaunchedEffect(Unit) {
                    try {
                        val masterKey = androidx.security.crypto.MasterKey.Builder(context)
                            .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                            .build()
                        val sharedPreferences = androidx.security.crypto.EncryptedSharedPreferences.create(
                            context,
                            "secure_user_session",
                            masterKey,
                            androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        )
                        val savedPhone = sharedPreferences.getString("user_phone", null)
                        if (savedPhone != null && savedPhone.isNotBlank()) {
                            dbViewModel.phoneNumber.value = savedPhone
                            dbViewModel.isOnboarded.value = true
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Session persistence resolution failed or not found: ${e.localizedMessage}")
                    }
                }

                val verificationBadge by dbViewModel.verificationStatus.collectAsState()
                val ghostActive by dbViewModel.isGhostModeActive.collectAsState()
                val juniorActive by dbViewModel.isJuniorModeActive.collectAsState()

                var isSettingsOverlayOpen by remember { mutableStateOf(false) }

                var showSetPinDialog by remember { mutableStateOf(false) }
                var showVerifyPinDialog by remember { mutableStateOf(false) }
                var tempPinInput by remember { mutableStateOf("") }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isOnboarded) {
                        OnboardingScreen(viewModel = dbViewModel)
                    } else {
                        // Main App Architecture Shell
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                Column {
                                    TopAppBar(
                                        title = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "DOBADOBA",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 24.sp,
                                                    color = SunriseOrange,
                                                    letterSpacing = (-1).sp,
                                                    modifier = Modifier.testTag("app_logo_title")
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(MalawiGreen.copy(0.12f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "MW",
                                                        color = MalawiGreen,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                                if (isDobaPlusActive) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(CircleShape)
                                                            .background(GoldYellow)
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            "DobaPlus ✨",
                                                            color = MalawiBlack,
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        actions = {
                                            // Toggle DobaPlus Premium badge
                                            IconButton(
                                                onClick = {
                                                    isDobaPlusActive = !isDobaPlusActive
                                                    val toastText = if (isDobaPlusActive) {
                                                        "DobaPlus Premium Active! Extra story slots and exclusive profile badge unlocked! ✨"
                                                    } else {
                                                        "Returned to standard tier"
                                                    }
                                                    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.testTag("plus_badge_toggle")
                                            ) {
                                                Icon(
                                                    imageVector = if (isDobaPlusActive) Icons.Default.Star else Icons.Outlined.StarBorder,
                                                    contentDescription = "DobaPlus",
                                                    tint = if (isDobaPlusActive) GoldYellow else MutedTextGray
                                                )
                                            }

                                            // Eye-safe Dark mode toggle
                                            IconButton(onClick = { darkThemeMode = !darkThemeMode }) {
                                                Icon(
                                                    imageVector = if (darkThemeMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                    contentDescription = "Theme switcher",
                                                    tint = MutedTextGray
                                                )
                                            }

                                            // Identity or profile verify trigger
                                            IconButton(onClick = { isSettingsOverlayOpen = true }) {
                                                Icon(
                                                    imageVector = when(verificationBadge) {
                                                        "Verified Blue" -> Icons.Default.Verified
                                                        "Verified Gold" -> Icons.Default.Verified
                                                        "Verified Green" -> Icons.Default.Verified
                                                        else -> Icons.Default.AccountCircle
                                                    },
                                                    contentDescription = "Profile identity toggles",
                                                    tint = when(verificationBadge) {
                                                        "Verified Blue" -> Color(0xFF0284C7)
                                                        "Verified Gold" -> GoldYellow
                                                        "Verified Green" -> MalawiGreenAccent
                                                        else -> MutedTextGray
                                                    }
                                                )
                                            }

                                            // Localization Language Toggle Button
                                            Button(
                                                onClick = {
                                                    dbViewModel.toggleLanguage()
                                                    val notifyText = if (userLang == "English") "Chicheŵa Chatsegulidwa! (Chichewa language loaded!)" else "English language loaded!"
                                                    Toast.makeText(context, notifyText, Toast.LENGTH_SHORT).show()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange.copy(0.12f)),
                                                contentPadding = PaddingValues(horizontal = 8.dp),
                                                modifier = Modifier
                                                    .height(30.dp)
                                                    .padding(end = 6.dp)
                                                    .testTag("lang_toggle_btn")
                                            ) {
                                                Text(
                                                    text = if (userLang == "English") "EN" else "CH 🇲🇼",
                                                    fontSize = 11.sp,
                                                    color = SunriseOrange,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                    HorizontalDivider(color = BorderGray.copy(0.1f))
                                }
                            },
                            bottomBar = {
                                Column {
                                    HorizontalDivider(color = BorderGray.copy(0.1f))
                                    NavigationBar(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.navigationBarsPadding()
                                    ) {
                                        val barTabs = listOf(
                                            Triple("Home", Icons.Default.Home, Icons.Outlined.Home),
                                            Triple("Discover", Icons.Default.Explore, Icons.Outlined.Explore),
                                            Triple("Post", Icons.Default.PhotoCamera, Icons.Outlined.PhotoCamera),
                                            Triple("Wallet", Icons.Default.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
                                            Triple("Chat", Icons.Default.Message, Icons.Outlined.Message),
                                            Triple("Profile", Icons.Default.Person, Icons.Outlined.Person)
                                        )

                                        barTabs.forEach { tabTuple ->
                                            val isSel = currentTab == tabTuple.first
                                            val animatedScale by animateFloatAsState(
                                                targetValue = if (isSel) 1.25f else 1.0f,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                ),
                                                label = "tabPulse"
                                            )
                                            NavigationBarItem(
                                                selected = isSel,
                                                onClick = { dbViewModel.currentTab.value = tabTuple.first },
                                                icon = {
                                                    Icon(
                                                        imageVector = if (isSel) tabTuple.second else tabTuple.third,
                                                        contentDescription = tabTuple.first,
                                                        modifier = Modifier.scale(animatedScale)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = tabTuple.first,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = SunriseOrange,
                                                    selectedTextColor = SunriseOrange,
                                                    unselectedIconColor = MutedTextGray,
                                                    unselectedTextColor = MutedTextGray,
                                                    indicatorColor = SunriseOrange.copy(alpha = 0.12f)
                                                ),
                                                modifier = Modifier.testTag("tab_${tabTuple.first.lowercase()}")
                                            )
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                AnimatedContent(
                                    targetState = currentTab,
                                    transitionSpec = {
                                        fadeIn(tween(200)) togetherWith fadeOut(tween(200))
                                    },
                                    label = "tab_switch"
                                ) { targetTab ->
                                    when (targetTab) {
                                        "Home" -> FeedTab(viewModel = dbViewModel)
                                        "Discover" -> DiscoverTab(viewModel = dbViewModel)
                                        "Post" -> DobaLensCameraTab(viewModel = dbViewModel)
                                        "Wallet" -> WalletTab(viewModel = dbViewModel)
                                        "Chat" -> ChatTab(viewModel = dbViewModel)
                                        "Profile" -> ProfileTab(viewModel = dbViewModel)
                                        else -> FeedTab(viewModel = dbViewModel)
                                    }
                                }

                                if (isSettingsOverlayOpen) {
                                    AlertDialog(
                                        onDismissRequest = { isSettingsOverlayOpen = false },
                                        title = {
                                            Text(
                                                text = "PROFILE & SAFETY HUB 🇲🇼",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp,
                                                color = SunriseOrange
                                            )
                                        },
                                        text = {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .verticalScroll(rememberScrollState()),
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                // 1. DobaVerify Section
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)),
                                                    border = BorderStroke(1.dp, BorderGray.copy(0.2f))
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        Text("1. DOBAVERIFY IDENTITY 🆔", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = SunriseOrange)
                                                        Text("Upload Malawi national ID (MNR) or business permits to earn your trust badge.", fontSize = 10.sp, color = MutedTextGray)

                                                        var selectedCategoryOption by remember { mutableStateOf(verificationBadge) }

                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                        ) {
                                                            listOf("Verified Blue", "Verified Gold", "Verified Green").forEach { badgeType ->
                                                                val isSelected = selectedCategoryOption == badgeType
                                                                val badgeText = badgeType.substringAfter(" ")
                                                                val badgeColor = when (badgeType) {
                                                                    "Verified Blue" -> Color(0xFF0284C7)
                                                                    "Verified Gold" -> GoldYellow
                                                                    "Verified Green" -> MalawiGreenAccent
                                                                    else -> Color.Gray
                                                                }

                                                                OutlinedButton(
                                                                    onClick = {
                                                                        selectedCategoryOption = badgeType
                                                                        dbViewModel.verificationStatus.value = badgeType
                                                                        Toast.makeText(context, "$badgeType activated instantly!", Toast.LENGTH_SHORT).show()
                                                                    },
                                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                                        containerColor = if (isSelected) badgeColor.copy(0.12f) else Color.Transparent
                                                                    ),
                                                                    border = BorderStroke(1.dp, if (isSelected) badgeColor else BorderGray),
                                                                    modifier = Modifier.weight(1f),
                                                                    contentPadding = PaddingValues(horizontal = 2.dp)
                                                                ) {
                                                                    Text(badgeText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) badgeColor else MaterialTheme.colorScheme.onSurface)
                                                                }
                                                            }
                                                        }

                                                        Button(
                                                            onClick = {
                                                                dbViewModel.pendingVerificationDoc.value = "Malawi_ID_Scan.pdf"
                                                                Toast.makeText(context, "Malawi National ID Uploaded & Verified! Badge updated.", Toast.LENGTH_LONG).show()
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                                            contentPadding = PaddingValues(0.dp)
                                                        ) {
                                                            Text("Re-upload National ID / Permit File", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }

                                                // 2. Ghost Mode Section
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)),
                                                    border = BorderStroke(1.dp, BorderGray.copy(0.2f))
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text("2. GHOST MODE (PRIVACY) 👻", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = SunriseOrange)
                                                            Switch(
                                                                checked = ghostActive,
                                                                onCheckedChange = {
                                                                    dbViewModel.isGhostModeActive.value = it
                                                                    val toastMessage = if (it) "Ghost mode activated! Expiring in 24 hours." else "Ghost mode off"
                                                                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                                                                },
                                                                colors = SwitchDefaults.colors(checkedThumbColor = SunriseOrange)
                                                            )
                                                        }
                                                        if (ghostActive) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .background(SunriseOrange.copy(0.1f))
                                                                    .padding(6.dp)
                                                            ) {
                                                                Text(
                                                                    text = "Active Countdown: 23 hours remaining.\n• Online indicator hidden\n• Message read receipts paused\n• Story views anonymous\n• Watchtime analytics paused",
                                                                    fontSize = 9.sp,
                                                                    lineHeight = 12.sp,
                                                                    color = SunriseOrange,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                        } else {
                                                            Text("Hide online indicator, disable read receipts, and preview stories anonymously.", fontSize = 10.sp, color = MutedTextGray)
                                                        }
                                                    }
                                                }

                                                // 3. DobaJunior Section
                                                Card(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)),
                                                    border = BorderStroke(1.dp, BorderGray.copy(0.2f))
                                                ) {
                                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text("3. DOBAJUNIOR PORTAL 🔒", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = SunriseOrange)
                                                            Switch(
                                                                checked = juniorActive,
                                                                onCheckedChange = { checkState ->
                                                                    if (checkState) {
                                                                        tempPinInput = dbViewModel.parentPin.value
                                                                        isSettingsOverlayOpen = false
                                                                        showSetPinDialog = true
                                                                    } else {
                                                                        tempPinInput = ""
                                                                        isSettingsOverlayOpen = false
                                                                        showVerifyPinDialog = true
                                                                    }
                                                                },
                                                                colors = SwitchDefaults.colors(checkedThumbColor = SunriseOrange),
                                                                modifier = Modifier.testTag("junior_mode_toggle_switch")
                                                            )
                                                        }

                                                        if (juniorActive) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .background(MalawiGreen.copy(0.1f))
                                                                    .padding(8.dp)
                                                            ) {
                                                                Column {
                                                                    Text(
                                                                        text = "🔒 UNICEF online safety guidelines active.\n• DMs from non-friends disabled\n• Adult content filtered out\n• DobaMarket & DobaPay locked inside kids profile\n\nWeekly Parent Summary: 2.4 hours clean usage. 0 flags reported.",
                                                                        fontSize = 9.sp,
                                                                        lineHeight = 12.sp,
                                                                        color = MalawiGreenAccent,
                                                                        fontWeight = FontWeight.Bold
                                                                    )
                                                                    Spacer(modifier = Modifier.height(4.dp))
                                                                    Row(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Text(
                                                                            text = "Parental PIN: ${dbViewModel.parentPin.value}",
                                                                            fontSize = 9.sp,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = MalawiGreenAccent
                                                                        )
                                                                        Text(
                                                                            text = "[Change PIN]",
                                                                            fontSize = 9.sp,
                                                                            fontWeight = FontWeight.ExtraBold,
                                                                            color = SunriseOrange,
                                                                            modifier = Modifier.clickable {
                                                                                tempPinInput = dbViewModel.parentPin.value
                                                                                isSettingsOverlayOpen = false
                                                                                showSetPinDialog = true
                                                                            }
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            Text("Locks DobaMarket & DobaPay, disables DMs from non-friends, and filters content.", fontSize = 10.sp, color = MutedTextGray)
                                                        }
                                                    }
                                                }

                                            }
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = { isSettingsOverlayOpen = false },
                                                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange)
                                            ) {
                                                Text("Done", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    )
                                }

                                // Parental PIN Dialogs at root level to prevent crash
                                if (showSetPinDialog) {
                                    AlertDialog(
                                        onDismissRequest = { 
                                            showSetPinDialog = false 
                                            isSettingsOverlayOpen = true
                                        },
                                        title = { Text("Configure Parents' PIN 🔑", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SunriseOrange) },
                                        text = {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text("Set a 4-digit numeric PIN to restrict access & secure child safety filters.", fontSize = 12.sp, color = MutedTextGray)
                                                OutlinedTextField(
                                                    value = tempPinInput,
                                                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) tempPinInput = it },
                                                    label = { Text("Parent PIN (4 digits)") },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    singleLine = true,
                                                    modifier = Modifier.fillMaxWidth().testTag("junior_set_pin_input"),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = SunriseOrange,
                                                        focusedLabelColor = SunriseOrange
                                                    )
                                                )
                                            }
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = {
                                                    if (tempPinInput.length < 4) {
                                                        Toast.makeText(context, "Chonde! Enter exactly 4 digits.", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        dbViewModel.parentPin.value = tempPinInput
                                                        dbViewModel.isJuniorModeActive.value = true
                                                        showSetPinDialog = false
                                                        isSettingsOverlayOpen = true
                                                        Toast.makeText(context, "DobaJunior activated with PIN: $tempPinInput!", Toast.LENGTH_LONG).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange)
                                            ) {
                                                Text("Activate Lock", fontWeight = FontWeight.Bold)
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { 
                                                showSetPinDialog = false 
                                                isSettingsOverlayOpen = true
                                            }) {
                                                Text("Cancel")
                                            }
                                        }
                                    )
                                }

                                if (showVerifyPinDialog) {
                                    AlertDialog(
                                        onDismissRequest = { 
                                            showVerifyPinDialog = false 
                                            isSettingsOverlayOpen = true
                                        },
                                        title = { Text("Deactivate DobaJunior Mode 🔓", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SunriseOrange) },
                                        text = {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text("Enter the 4-digit Parent PIN to unlock full DobaMarket & DobaPay features.", fontSize = 12.sp, color = MutedTextGray)
                                                OutlinedTextField(
                                                    value = tempPinInput,
                                                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) tempPinInput = it },
                                                    label = { Text("Parent PIN") },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    singleLine = true,
                                                    modifier = Modifier.fillMaxWidth().testTag("junior_verify_pin_input"),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = SunriseOrange,
                                                        focusedLabelColor = SunriseOrange
                                                    )
                                                )
                                            }
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = {
                                                    if (tempPinInput == dbViewModel.parentPin.value) {
                                                        dbViewModel.isJuniorModeActive.value = false
                                                        showVerifyPinDialog = false
                                                        isSettingsOverlayOpen = true
                                                        Toast.makeText(context, "DobaJunior deactivated. Full mode unlocked!", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "PIN olakwika (Incorrect PIN)! Chonde yesaninso.", Toast.LENGTH_LONG).show()
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange)
                                            ) {
                                                Text("Verify & Unlock", fontWeight = FontWeight.Bold)
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { 
                                                showVerifyPinDialog = false 
                                                isSettingsOverlayOpen = true
                                            }) {
                                                Text("Cancel")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
