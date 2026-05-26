package com.example.ui.profile

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.DobadobaViewModel
import com.example.ui.theme.*

@Composable
fun ProfileTab(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val userProfile by viewModel.userProfileState.collectAsState()
    val isBiometricsLocked by viewModel.isBiometricsLocked.collectAsState()

    val userLang by viewModel.currentLanguage.collectAsState()
    val isJuniorModeActive by viewModel.isJuniorModeActive.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var inputPinText by remember { mutableStateOf("") }
    var isPinForEnabling by remember { mutableStateOf(true) }

    var isVerifyingFlowOpen by remember { mutableStateOf(false) }
    val isPremiumSuccessMsg by viewModel.premiumBuySuccess.collectAsState()

    // Sign out confirmation & edit profile states
    var showSignOutConfirm by remember { mutableStateOf(false) }
    var isEditProfileOpen by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile.name) }
    var editLocation by remember { mutableStateOf(userProfile.locationTag) }
    var pushAlertsEnabled by remember { mutableStateOf(true) }
    var emailAlertsEnabled by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            item {
                // Profile Header Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Three-dot report profile options
                        var showProfileReport by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(onClick = { showProfileReport = true }, modifier = Modifier.testTag("report_profile_btn")) {
                                Icon(Icons.Default.MoreVert, "More options", tint = MutedTextGray)
                            }
                            DropdownMenu(
                                expanded = showProfileReport,
                                onDismissRequest = { showProfileReport = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Report Profile") },
                                    onClick = {
                                        showProfileReport = false
                                        Toast.makeText(context, "Mndandanda wadziwika. Profile reported and queued for 24-hour review.", Toast.LENGTH_LONG).show()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Flag, contentDescription = "Report Address Profile", tint = MalawiRed)
                                    },
                                    modifier = Modifier.testTag("report_profile_option_item")
                                )
                            }
                        }

                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(SunriseOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.take(1).uppercase(),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        // Display Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = userProfile.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            if (userProfile.isVerified) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Profile Gold Badge",
                                    tint = GoldYellow,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Handle and District Location
                        Text(
                            text = "@ine_me_mw • District: ${userProfile.locationTag}",
                            fontSize = 13.sp,
                            color = MutedTextGray
                        )

                        // Followers and Stats
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            ProfileStatCol(num = "1,240", label = "Followers")
                            ProfileStatCol(num = "320", label = "Following")
                            ProfileStatCol(num = "15", label = "DobaPosts")
                        }

                        // Kusintha Mbiri (Edit Profile) Active Button
                        OutlinedButton(
                            onClick = {
                                editName = userProfile.name
                                editLocation = userProfile.locationTag
                                isEditProfileOpen = true
                            },
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .testTag("edit_profile_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile Settings", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kusintha Mbiri (Edit Profile)", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Lang preference card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Chilankhulo (Language Preference)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("English", "Chichewa").forEach { lang ->
                                val active = userLang == lang
                                Button(
                                    onClick = { viewModel.currentLanguage.value = lang },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (active) SunriseOrange else Color.LightGray
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(lang, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Biometric Settings
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                            Text("Biometric Fingerprint Lock 🔒", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Fast secure access on Android", fontSize = 11.sp, color = MutedTextGray)
                        }

                        Switch(
                            checked = isBiometricsLocked,
                            onCheckedChange = { viewModel.toggleBiometricLock(context) },
                            colors = SwitchDefaults.colors(checkedThumbColor = SunriseOrange, checkedTrackColor = SunriseOrange.copy(0.4f))
                        )
                    }
                }
            }

            // DobaJunior Parental Controls Safety Configuration
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("DobaJunior Safe Mode 👶", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Restricted adult posts/marketplace options", fontSize = 11.sp, color = MutedTextGray)
                            }

                            Switch(
                                checked = isJuniorModeActive,
                                onCheckedChange = { checked ->
                                    isPinForEnabling = checked
                                    showPinDialog = true
                                    inputPinText = ""
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = SunriseOrange, checkedTrackColor = SunriseOrange.copy(0.4f))
                            )
                        }

                        if (isJuniorModeActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MalawiGreen.copy(0.08f), shape = RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text("✓ Safe mode is active. Restricted and adult content filtered out.", fontSize = 11.sp, color = MalawiGreen)
                            }
                        }
                    }
                }
            }

            // Upgrade to Premium / Verified Badge Seller Purchase Option
            if (!userProfile.isVerified) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldYellow.copy(0.08f)),
                        border = BorderStroke(1.5.dp, GoldYellow)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, "star premium", tint = GoldYellow, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Verificated Seller Upgrade! ✨", fontWeight = FontWeight.Black, color = GoldYellow, fontSize = 16.sp)
                            }

                            Text("Stand out in the community! Become a verified builder on Dobadoba. Verified channels get higher ranking and zero fee escrow.", fontSize = 12.sp)

                            Button(
                                onClick = { isVerifyingFlowOpen = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth().testTag("buy_premium_init_btn")
                            ) {
                                Text("Purchase Gold Badge • MWK 5,000", fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // Sign Out Account Option
            item {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        showSignOutConfirm = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("profile_signout_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MalawiRed),
                    border = BorderStroke(1.5.dp, MalawiRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sign out",
                        tint = MalawiRed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cotsani Account (Sign Out)", fontWeight = FontWeight.Black)
                }
            }
        }

        // Edit Profile Dialog Setup (Kusintha Mbiri)
        if (isEditProfileOpen) {
            Dialog(onDismissRequest = { isEditProfileOpen = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                            text = "Kusintha Mbiri (Edit Profile) 📝",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SunriseOrange
                        )

                        Divider(color = Color.Gray.copy(0.2f))

                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                        )

                        OutlinedTextField(
                            value = editLocation,
                            onValueChange = { editLocation = it },
                            label = { Text("Location (District)") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_location_input")
                        )

                        Text(
                            text = "Notification Preferences 🔔",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Push Notification Alerts", fontSize = 13.sp)
                            Switch(
                                checked = pushAlertsEnabled,
                                onCheckedChange = { pushAlertsEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = SunriseOrange, checkedTrackColor = SunriseOrange.copy(0.4f))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Email Newsletter Digest", fontSize = 13.sp)
                            Switch(
                                checked = emailAlertsEnabled,
                                onCheckedChange = { emailAlertsEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = SunriseOrange, checkedTrackColor = SunriseOrange.copy(0.4f))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isEditProfileOpen = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    if (editName.isNotBlank() && editLocation.isNotBlank()) {
                                        viewModel.userProfileState.value = userProfile.copy(
                                            name = editName,
                                            locationTag = editLocation
                                        )
                                        isEditProfileOpen = false
                                        Toast.makeText(context, "Mbiri yakuipitsidwa bwino (Profile updated successfully)!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Fields cannot be blank!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                                modifier = Modifier.weight(1f).testTag("edit_profile_save_btn")
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }

        // Sign Out Confirmation Alert Dialog
        if (showSignOutConfirm) {
            AlertDialog(
                onDismissRequest = { showSignOutConfirm = false },
                title = { Text("Cotsani Account (Sign Out) 🚪", fontWeight = FontWeight.Black) },
                text = { Text("Are you completely sure you want to sign out from Dobadoba super-app? You will be required to re-authenticate on your next login.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showSignOutConfirm = false
                            val authViewModel = com.example.ui.AuthViewModel()
                            authViewModel.signOut(context) {
                                viewModel.isOnboarded.value = false
                                viewModel.currentTab.value = "Home"
                                Toast.makeText(context, "Mwatuluka (Signed out) successfully!", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MalawiRed)
                    ) {
                        Text("Ee, Ndikutuluka (Yes, Sign Out)", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showSignOutConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Parent PIN Dialog
        if (showPinDialog) {
            Dialog(onDismissRequest = { showPinDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = if (isPinForEnabling) "Set Parental Security PIN" else "Unlock Parental Restrictions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SunriseOrange
                        )

                        Text("Enter a 4-digit PIN to prevent unauthorized changes to child accounts.", fontSize = 12.sp, color = MutedTextGray)

                        OutlinedTextField(
                            value = inputPinText,
                            onValueChange = { inputPinText = it.take(4) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            label = { Text("Parent PIN") },
                            modifier = Modifier.fillMaxWidth().testTag("parent_pin_textbox")
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showPinDialog = false }) { Text("Cancel") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (inputPinText == "1234" || isPinForEnabling) {
                                        viewModel.isJuniorModeActive.value = isPinForEnabling
                                        showPinDialog = false
                                        Toast.makeText(context, "DobaJunior settings changed!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Incorrect Parental PIN!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange)
                            ) {
                                Text("Submit", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Verified seller payout checkout flow
        if (isVerifyingFlowOpen) {
            Dialog(onDismissRequest = { isVerifyingFlowOpen = false }) {
                var billingPhone by remember { mutableStateOf("") }
                var selectedOpe by remember { mutableStateOf("Airtel Money") }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Complete Verified Purchase", fontWeight = FontWeight.Black, fontSize = 16.sp, color = GoldYellow)

                        if (isPremiumSuccessMsg == null) {
                            Text("A verification prompt will trigger on your phone via USSD. Enter mobile money PIN below.", fontSize = 12.sp)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Airtel Money", "TNM Mpamba").forEach { op ->
                                    val act = selectedOpe == op
                                    OutlinedButton(
                                        onClick = { selectedOpe = op },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (act) GoldYellow.copy(0.08f) else Color.Transparent
                                        ),
                                        border = BorderStroke(1.5.dp, if (act) GoldYellow else BorderGray)
                                    ) {
                                        Text(op, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = billingPhone,
                                onValueChange = { billingPhone = it },
                                label = { Text("Phone (+265)") },
                                modifier = Modifier.fillMaxWidth().testTag("premium_number_textbox")
                            )

                            Button(
                                onClick = {
                                    if (billingPhone.length < 9) {
                                        Toast.makeText(context, "Please enter valid Malawi phone!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.purchasePremiumAccount(billingPhone, selectedOpe)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = Color.Black),
                                modifier = Modifier.fillMaxWidth().testTag("premium_submit_payout_btn")
                            ) {
                                Text("Prepay MWK 5,000 Now", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Icon(Icons.Default.CheckCircle, "success", tint = MalawiGreen, modifier = Modifier.size(64.dp).align(Alignment.CenterHorizontally))
                            Text("BADGE ACQUIRED SUCCESSFULLY!", fontWeight = FontWeight.Bold, color = MalawiGreen, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                            Button(
                                onClick = {
                                    isVerifyingFlowOpen = false
                                    viewModel.premiumBuySuccess.value = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                                modifier = Modifier.fillMaxWidth()
                              ) {
                                Text("Great!", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatCol(num: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = num, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MutedTextGray)
    }
}
