package com.example.ui

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// ONBOARDING SCREEN
// -------------------------------------------------------------
@Composable
fun OnboardingScreen(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRegistering by remember { mutableStateOf(false) }
    var registrationStep by remember { mutableStateOf(1) } // 1: Info, 2: Interests, 3: Avatar & Bio

    // Login Form State
    var loginPhone by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var useOtpOnlyLogin by remember { mutableStateOf(false) }
    var showForgotPasswordOtp by remember { mutableStateOf(false) }
    var forgotPasswordPhone by remember { mutableStateOf("") }

    // Registration Form State
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regDOB by remember { mutableStateOf("15/05/1998") }
    var regDistrict by remember { mutableStateOf("Lilongwe") }
    var regGender by remember { mutableStateOf("Male") }
    val regSelectedInterests = remember { mutableStateOf(setOf<String>()) }
    var regBio by remember { mutableStateOf("") }
    var regPhotoSelected by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            // Dobadoba branding header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                // Circular Logo simulation with red flag symbol
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MalawiRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🇲🇼",
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Dobadoba",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MalawiRed,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Malawi's Social Hub",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MutedTextGray
                )
            }

            if (!isRegistering) {
                // ==========================================
                // LOGIN SCREEN
                // ==========================================
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Lowani mu Akaunti (Login)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MalawiRed
                        )

                        OutlinedTextField(
                            value = loginPhone,
                            onValueChange = { loginPhone = it },
                            label = { Text("Phone Number", fontWeight = FontWeight.Normal) },
                            placeholder = { Text("e.g. 0888123456") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MalawiRed) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("login_phone_input")
                        )

                        if (!useOtpOnlyLogin) {
                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = { loginPassword = it },
                                label = { Text("Password", fontWeight = FontWeight.Normal) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MalawiRed) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(LightRedTint, shape = RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "SMS OTP code (2026) will be sent to your Airtel/TNM number on login.",
                                    fontSize = 11.sp,
                                    color = MalawiRed,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = useOtpOnlyLogin,
                                    onCheckedChange = { useOtpOnlyLogin = it },
                                    colors = CheckboxDefaults.colors(checkedColor = MalawiRed)
                                )
                                Text("OTP-only entry", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MutedTextGray)
                            }
                            Text(
                                text = "Forgot Password?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MalawiRed,
                                modifier = Modifier.clickable { showForgotPasswordOtp = true }
                            )
                        }

                        Button(
                            onClick = {
                                if (loginPhone.length < 9) {
                                    Toast.makeText(context, "Chonde lowetsani foni yeniyeni (Enter valid phone number)", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                viewModel.phoneNumber.value = loginPhone
                                viewModel.userDisplayName.value = "Ine (Me) 🇲🇼"
                                viewModel.isOnboarded.value = true
                                Toast.makeText(context, "Mwalowa bwino lomwe (Logged in successfully)!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MalawiRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_button")
                        ) {
                            Text(if (useOtpOnlyLogin) "Request OTP & Login" else "Login", fontWeight = FontWeight.Bold)
                        }

                        // Biometrics option
                        OutlinedButton(
                            onClick = {
                                val activity = context as? FragmentActivity
                                if (activity != null && DobadobaBiometricHelper.isBiometricAvailable(context)) {
                                    DobadobaBiometricHelper.authenticate(
                                        activity = activity,
                                        title = "Dobadoba Login Verification",
                                        subtitle = "Scan your fingerprint or enter device pattern/passcode to proceed",
                                        onSuccess = {
                                            viewModel.phoneNumber.value = "0888123456"
                                            viewModel.userDisplayName.value = "Ine (Me) 🇲🇼"
                                            viewModel.isOnboarded.value = true
                                            Toast.makeText(context, "Biometrics verified! Ine logged in.", Toast.LENGTH_SHORT).show()
                                        },
                                        onError = { errorMsg ->
                                            Toast.makeText(context, "Authentication failed: $errorMsg", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    // Fallback for emulator environments where biometric sensor is not enrolled
                                    viewModel.phoneNumber.value = "0888123456"
                                    viewModel.userDisplayName.value = "Ine (Me) 🇲🇼"
                                    viewModel.isOnboarded.value = true
                                    Toast.makeText(context, "Biometrics verified safely (Emulator local bypass)! Ine logged in.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MalawiRed),
                            border = BorderStroke(1.dp, MalawiRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                            Spacer(Modifier.width(8.dp))
                            Text("Use Biometric Login", fontWeight = FontWeight.Medium)
                        }

                        HorizontalDivider(color = BorderGray, modifier = Modifier.padding(vertical = 4.dp))

                        // SSO Options
                        Text(
                            text = "Or continue with social identity:",
                            fontSize = 11.sp,
                            color = MutedTextGray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.phoneNumber.value = "0999000111"
                                    viewModel.userDisplayName.value = "Google User 🇲🇼"
                                    viewModel.isOnboarded.value = true
                                    Toast.makeText(context, "Logged in via Google SSO!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5), contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("Google SSO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    viewModel.phoneNumber.value = "0999888777"
                                    viewModel.userDisplayName.value = "Facebook User 🇲🇼"
                                    viewModel.isOnboarded.value = true
                                    Toast.makeText(context, "Logged in via Facebook SSO!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2), contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("Facebook", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Go to register
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Don't have an account? ", fontSize = 13.sp, color = MutedTextGray)
                            Text(
                                "Register here",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MalawiRed,
                                modifier = Modifier.clickable { isRegistering = true; registrationStep = 1 }
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // REGISTRATION 3-STEP WIZARD
                // ==========================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Wizard Step Indicator Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 1..3) {
                            val active = registrationStep >= i
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (active) MalawiRed else BorderGray)
                            )
                        }
                    }

                    Text(
                        text = "Step $registrationStep of 3: " + when (registrationStep) {
                            1 -> "Basic Information"
                            2 -> "Seed Recommendations"
                            else -> "Verify Identity"
                        },
                        fontSize = 13.sp,
                        color = MalawiRed,
                        fontWeight = FontWeight.Bold
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderGray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            when (registrationStep) {
                                1 -> {
                                    Text(
                                        text = "Your Profile Basics 🇲🇼",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MalawiRed
                                    )

                                    OutlinedTextField(
                                        value = regName,
                                        onValueChange = { regName = it },
                                        label = { Text("Full Name") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = regPhone,
                                        onValueChange = { regPhone = it },
                                        label = { Text("Primary Phone (Airtel/TNM)") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = regEmail,
                                        onValueChange = { regEmail = it },
                                        label = { Text("Email Address (Optional)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = regDOB,
                                        onValueChange = { regDOB = it },
                                        label = { Text("Date of Birth (DD/MM/YYYY)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // District field
                                    OutlinedTextField(
                                        value = regDistrict,
                                        onValueChange = { regDistrict = it },
                                        label = { Text("District/City of Residence") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Column {
                                        Text("Gender:", fontSize = 12.sp, color = MutedTextGray)
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            listOf("Male", "Female").forEach { g ->
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    RadioButton(
                                                        selected = regGender == g,
                                                        onClick = { regGender = g },
                                                        colors = RadioButtonDefaults.colors(selectedColor = MalawiRed)
                                                    )
                                                    Text(g, fontSize = 13.sp)
                                                }
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (regName.isBlank() || regPhone.isBlank()) {
                                                Toast.makeText(context, "Chonde enter name and phone number!", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            registrationStep = 2
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MalawiRed),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Continue to Step 2", fontWeight = FontWeight.Bold)
                                    }
                                }

                                2 -> {
                                    Text(
                                        text = "Select Your Interests 🎯",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MalawiRed
                                    )
                                    Text(
                                        text = "We use these to seed and customize your recommendation feed with local Malawian stories.",
                                        fontSize = 12.sp,
                                        color = MutedTextGray
                                    )

                                    val list = listOf("Farming 🌽", "Music 🎵", "Sports ⚽", "Church ⛪", "Business 💼", "Fashion 👗", "Politics 🗳️")
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        list.forEach { interest ->
                                            val isSel = regSelectedInterests.value.contains(interest)
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        val curr = regSelectedInterests.value.toMutableSet()
                                                        if (isSel) curr.remove(interest) else curr.add(interest)
                                                        regSelectedInterests.value = curr
                                                    },
                                                border = BorderStroke(1.dp, if (isSel) MalawiRed else BorderGray),
                                                colors = CardDefaults.cardColors(containerColor = if (isSel) LightRedTint else Color.White)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(interest, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                                    if (isSel) {
                                                        Icon(Icons.Default.Check, "Selected", tint = MalawiRed, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { registrationStep = 1 },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Back")
                                        }
                                        Button(
                                            onClick = { registrationStep = 3 },
                                            colors = ButtonDefaults.buttonColors(containerColor = MalawiRed),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Next Step")
                                        }
                                    }
                                }

                                3 -> {
                                    Text(
                                        text = "Profile Photo & Bio 📸",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MalawiRed
                                    )

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Simulated photo slots
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape)
                                                .background(if (regPhotoSelected) MalawiRed else BorderGray)
                                                .clickable { regPhotoSelected = true },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (regPhotoSelected) {
                                                Text("✅ Photo", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            } else {
                                                Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = MutedTextGray)
                                            }
                                        }
                                        Text(
                                            text = if (regPhotoSelected) "Photo loaded successfully! Tap to change." else "Upload Profile Photo",
                                            fontSize = 11.sp,
                                            color = if (regPhotoSelected) MalawiRed else MutedTextGray,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedTextField(
                                        value = regBio,
                                        onValueChange = { regBio = it },
                                        label = { Text("Profile Bio (Optional)") },
                                        placeholder = { Text("Describe yourself or your business in Malawi...") },
                                        modifier = Modifier.fillMaxWidth().height(100.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { registrationStep = 2 },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Back")
                                        }
                                        Button(
                                            onClick = {
                                                // Save variables back into viewmodel states
                                                viewModel.userDisplayName.value = if (regName.isNotBlank()) regName else "Nyasa Born"
                                                viewModel.userHandle.value = regName.lowercase().replace(" ", "_")
                                                viewModel.userDistrict.value = regDistrict
                                                viewModel.userDOB.value = regDOB
                                                viewModel.userGender.value = regGender
                                                viewModel.userBio.value = if (regBio.isNotBlank()) regBio else "Proud Malawian explorer on Dobadoba."
                                                viewModel.phoneNumber.value = regPhone
                                                viewModel.isOnboarded.value = true
                                                Toast.makeText(context, "Akaunti yanu yakonzeka (Profile complete)!", Toast.LENGTH_LONG).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MalawiRed),
                                            modifier = Modifier.weight(1.5f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("Complete Sign up", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = { isRegistering = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Already have an account? Login instead", color = MalawiRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Forgot Password OTP Dialog simulation
    if (showForgotPasswordOtp) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordOtp = false },
            title = { Text("Reset Password via SMS OTP 🔑", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MalawiRed) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter your Airtel or TNM mobile number to receive a temporary reset password SMS token.", fontSize = 12.sp)
                    OutlinedTextField(
                        value = forgotPasswordPhone,
                        onValueChange = { forgotPasswordPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (forgotPasswordPhone.length < 9) {
                            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                        } else {
                            showForgotPasswordOtp = false
                            Toast.makeText(context, "SMS OTP sent to $forgotPasswordPhone! Check your device messages.", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MalawiRed)
                ) {
                    Text("Send Code")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordOtp = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


// -------------------------------------------------------------
// STORY VIEWER DIALOG
// -------------------------------------------------------------
@Composable
fun StoryViewerDialog(
    story: Story,
    onDismiss: () -> Unit,
    onVote: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
                .clickable { onDismiss() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (User info, avatar, close button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SunriseOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = story.username.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = story.username,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                if (story.isCloseFriends) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MalawiGreen),
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    ) {
                                        Text(
                                            "Close Friends",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                            if (story.locationTag.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = SunriseOrange,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = story.locationTag,
                                        color = SunriseOrange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Main Story graphical overlay representing visual content
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = if (story.isCloseFriends) {
                                    listOf(MalawiGreen, ChambaSlateLight)
                                } else {
                                    listOf(ChambaSlateLight, SunriseOrange.copy(alpha = 0.5f))
                                }
                            )
                        )
                        .clickable(enabled = false) {}, // prevent click close in child card
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (story.isCloseFriends) Icons.Default.Lock else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (story.isCloseFriends) MalawiGreen else GoldYellow,
                            modifier = Modifier.size(56.dp)
                        )

                        Text(
                            text = story.textOverlay,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        // If story has interactive elements (Poll!)
                        if (story.isPoll) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MalawiBlack.copy(alpha = 0.8f)),
                                border = BorderStroke(1.dp, SunriseOrange.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = story.pollQuestion,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    if (story.userAnswer == 0) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { onVote(true) },
                                                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(story.pollOptionA, fontWeight = FontWeight.Bold)
                                            }
                                            Button(
                                                onClick = { onVote(false) },
                                                colors = ButtonDefaults.buttonColors(containerColor = ChambaSlateLight),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(story.pollOptionB, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        // Vote results view!
                                        val totalVotes = story.pollVotesA + story.pollVotesB
                                        val percentA = if (totalVotes > 0) (story.pollVotesA * 100) / totalVotes else 50
                                        val percentB = 100 - percentA

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Option A result bar
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(38.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (story.userAnswer == 1) SunriseOrange.copy(0.4f) else Color.DarkGray)
                                                    .padding(horizontal = 12.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    text = "${story.pollOptionA} • $percentA%",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            // Option B result bar
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(38.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (story.userAnswer == 2) SunriseOrange.copy(0.4f) else Color.DarkGray)
                                                    .padding(horizontal = 12.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    text = "${story.pollOptionB} • $percentB%",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom CTA
                Text(
                    text = "Dinani malo ena aliwonse kutseka (Tap anywhere to close)",
                    color = MutedTextGray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}


// -------------------------------------------------------------
// CREATE NEW POST DIALOG
// -------------------------------------------------------------
@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Boolean) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isVideoType by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Gawirani Zoyenera (Publish Post)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("What is happening in Malawi today?") },
                    placeholder = { Text("Lembani mfundo yanu pano (write your post)...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SunriseOrange,
                        focusedLabelColor = SunriseOrange,
                        unfocusedBorderColor = MutedTextGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("create_post_text"),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle if user simulates a reel video post
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Treat as Vertical DobaReel?", fontSize = 14.sp)
                    Switch(
                        checked = isVideoType,
                        onCheckedChange = { isVideoType = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SunriseOrange, checkedTrackColor = SunriseOrange.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Tseka (Cancel)", color = MutedTextGray)
                    }
                    Button(
                        onClick = {
                            onSubmit(text, isVideoType)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                        enabled = text.isNotBlank(),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("submit_post_button")
                    ) {
                        Text("Publish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// -------------------------------------------------------------
// MOBILE MONEY PAYMENT DIALOG (Airtel vs Mpamba)
// -------------------------------------------------------------
@Composable
fun PaymentDialog(
    item: MarketListing,
    viewModel: DobadobaViewModel,
    onDismiss: () -> Unit
) {
    var phoneNumberInput by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf(if (item.mobileMoneyType == "TNM Mpamba") "TNM Mpamba" else "Airtel Money") }

    val isProcessing by viewModel.isPaymentProcessing.collectAsState()
    val isSuccess by viewModel.isPaymentSuccess.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isSuccess) {
                    Text(
                        text = "Dobadoba Mobile Money Express 🇲🇼",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = SunriseOrange,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = item.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(text = "Seller: ${item.sellerName} (${item.location})", fontSize = 12.sp, color = MutedTextGray)
                            Text(
                                text = "Total: MWK ${String.format("%,.2f", item.priceMwk)}",
                                color = SunriseOrange,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Mobile Money provider choice
                    Text("Sankhani Njira Pay (Choose payment method)", fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (item.mobileMoneyType == "Both" || item.mobileMoneyType == "Airtel Money") {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { serviceType = "Airtel Money" },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (serviceType == "Airtel Money") MalawiRed.copy(0.1f) else MaterialTheme.colorScheme.background
                                ),
                                border = BorderStroke(1.5.dp, if (serviceType == "Airtel Money") MalawiRed else Color.Transparent)
                            ) {
                                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text("Airtel Money 🔴", fontWeight = FontWeight.Bold, color = if (serviceType == "Airtel Money") MalawiRed else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        if (item.mobileMoneyType == "Both" || item.mobileMoneyType == "TNM Mpamba") {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { serviceType = "TNM Mpamba" },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (serviceType == "TNM Mpamba") MalawiGreenAccent.copy(0.1f) else MaterialTheme.colorScheme.background
                                ),
                                border = BorderStroke(1.5.dp, if (serviceType == "TNM Mpamba") MalawiGreenAccent else Color.Transparent)
                            ) {
                                Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                                    Text("TNM Mpamba 🟢", fontWeight = FontWeight.Bold, color = if (serviceType == "TNM Mpamba") MalawiGreenAccent else MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }

                    // Phone input
                    OutlinedTextField(
                        value = phoneNumberInput,
                        onValueChange = { if (it.length <= 10) phoneNumberInput = it },
                        label = { Text("Your Mobile Money Number") },
                        placeholder = { Text("e.g., 0888123456") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SunriseOrange,
                            focusedLabelColor = SunriseOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pay_phone_input")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isProcessing) {
                        CircularProgressIndicator(color = SunriseOrange, modifier = Modifier.size(36.dp))
                        Text("Sending PIN prompt request to phone...", modifier = Modifier.padding(top = 10.dp), fontSize = 13.sp)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                                fText("Tseka (Cancel)")
                            }
                            Button(
                                onClick = { viewModel.executeMobileMoneyPayment(phoneNumberInput, serviceType) },
                                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("execute_pay_btn"),
                                enabled = phoneNumberInput.length >= 9
                            ) {
                                Text("Pay with $serviceType", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    // Success View
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MalawiGreenAccent,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Payment Completed! ✨",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MalawiGreenAccent
                    )
                    Text(
                        text = "A simulated transaction reference has been registered on Dobadoba. Touch the local merchant at ${item.sellerPhone} to organise collection.",
                        textAlign = TextAlign.Center,
                        color = MutedTextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Zikomo Kwambiri (Perfect!)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SHORT HELPER TO ALLOW NO COMPILING SYNTAX ERROR WITH TEXT WRAPPER
// -------------------------------------------------------------
@Composable
fun fText(text: String, fontStyle: FontStyle? = null) {
    Text(text = text, color = MutedTextGray, fontStyle = fontStyle)
}

// -------------------------------------------------------------
// CREATE NEW MARKETLISTING DIALOG
// -------------------------------------------------------------
@Composable
fun CreateListingDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Lilongwe") }

    val locations = listOf("Lilongwe", "Blantyre", "Zomba", "Mzuzu", "Mangochi", "Dedza", "Salima")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Gulitsani Katundu (Sell Local Item)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item Name (Mutu wa katundu)") },
                    placeholder = { Text("e.g. Clean Dedza Potatoes") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SunriseOrange, focusedLabelColor = SunriseOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("list_title_input")
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price in MWK (Mtengo watsopano)") },
                    placeholder = { Text("e.g., 15000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SunriseOrange, focusedLabelColor = SunriseOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("list_price_input")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("Condition, size, contact instructions...") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SunriseOrange, focusedLabelColor = SunriseOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(bottom = 12.dp),
                    maxLines = 3
                )

                // Simple Horizontal Selectable Row for Location
                Text(
                    "Sankhani Mumene Mulili (Location)",
                    fontSize = 12.sp,
                    color = MutedTextGray,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    locations.forEach { loc ->
                        val isSel = location == loc
                        FilterChip(
                            selected = isSel,
                            onClick = { location = loc },
                            label = { Text(loc, color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SunriseOrange)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Tseka (Cancel)", color = MutedTextGray)
                    }
                    Button(
                        onClick = {
                            val priceValue = priceStr.toDoubleOrNull() ?: 0.0
                            onSubmit(title, desc, priceValue, location)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                        enabled = title.isNotBlank() && priceStr.isNotBlank(),
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("submit_listing_btn")
                    ) {
                        Text("Add Listing", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// VOICE WAVEFORM ANIMATED GRAPHICS REPRESENTATION
// -------------------------------------------------------------
@Composable
fun VoiceWaveformRepresentation(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveformPulse")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseProgress"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barCount = 12
        for (i in 0 until barCount) {
            val phase = (i * Math.PI / barCount).toFloat()
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(30.dp)
                    .graphicsLayer {
                        val scale = 0.15f + 0.85f * kotlin.math.sin(animProgress * Math.PI.toFloat() + phase).coerceIn(-1f, 1f).let { kotlin.math.abs(it) }
                        scaleY = scale
                    }
                    .clip(RoundedCornerShape(2.dp))
                    .background(com.example.ui.theme.MalawiRed)
            )
        }
    }
}

