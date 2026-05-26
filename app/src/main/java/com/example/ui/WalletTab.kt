package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBalanceWallet
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
import com.example.ui.theme.*
import com.example.util.FeeCalculator

@Composable
fun WalletTab(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val balanceMwk by viewModel.dobaPayBalance.collectAsState()
    val coinsBalance by viewModel.dobaCoinsBalance.collectAsState()
    val userProfile by viewModel.userProfileState.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Balance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MalawiRed),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DobaPay Wallet Ledger 🇲🇼",
                            color = Color.White.copy(0.85f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "My QR",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = FeeCalculator.formatMwk(balanceMwk),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // DobaCoins Balance Row
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Coins icon",
                            tint = GoldYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$coinsBalance DobaCoins Rewards",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Quick Transactions Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showDepositDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("wallet_deposit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = "Deposit")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Deposit Fund", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = { showWithdrawDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("wallet_withdraw_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.ArrowUpward, contentDescription = "Withdraw")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Withdraw Cash", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Pay for Gold Badge Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_gold_badge_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (userProfile.isVerified) GoldYellow else BorderGray
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GoldYellow.copy(0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Gold Badge",
                                tint = GoldYellow,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Golden Verification Upgrade ✨",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = if (userProfile.isVerified) GoldYellow else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Upgrade your profile automatically with our trusted seller checkmark icon. Verified users enjoy lower commissions on Escrow deals and gain standard ranking.",
                        fontSize = 12.sp,
                        color = MutedTextGray,
                        lineHeight = 16.sp
                    )

                    if (userProfile.isVerified) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldYellow.copy(0.1f))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, "verified active", tint = MalawiGreen)
                                Text(
                                    text = "Gold trust status actively linked to @dwachikopa!",
                                    color = MalawiGreenAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                if (balanceMwk < 5000) {
                                    Toast.makeText(context, "Balance below MWK 5,000! Chonde deposit funds.", Toast.LENGTH_LONG).show()
                                } else {
                                    viewModel.dobaPayBalance.value -= 5000.0
                                    viewModel.userProfileState.value = viewModel.userProfileState.value.copy(isVerified = true)
                                    viewModel.verificationStatus.value = "Verified Gold"
                                    Toast.makeText(context, "Purchased Golden Badge successfully! ✨", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("purchase_gold_badge_wallet_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldYellow, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Unlock Gold Badge • MWK 5,000", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- Deposit Dialog ---
        if (showDepositDialog) {
            TransactionDialog(
                title = "Deposit Mock Funds 📥",
                isDeposit = true,
                onDismiss = { showDepositDialog = false },
                onConfirm = { provider, phone, amount ->
                    viewModel.dobaPayBalance.value += amount
                    Toast.makeText(context, "Successfully deposited ${FeeCalculator.formatMwk(amount)} from $provider!", Toast.LENGTH_LONG).show()
                    showDepositDialog = false
                }
            )
        }

        // --- Withdraw Dialog ---
        if (showWithdrawDialog) {
            TransactionDialog(
                title = "Withdraw Cash Funds 📤",
                isDeposit = false,
                onDismiss = { showWithdrawDialog = false },
                onConfirm = { provider, phone, amount ->
                    if (balanceMwk < amount) {
                        Toast.makeText(context, "Olasowa (Insufficient balance)! Wallet has ${FeeCalculator.formatMwk(balanceMwk)}", Toast.LENGTH_LONG).show()
                    } else {
                        viewModel.dobaPayBalance.value -= amount
                        Toast.makeText(context, "Dispatched payout of ${FeeCalculator.formatMwk(amount)} to $provider ($phone)!", Toast.LENGTH_LONG).show()
                        showWithdrawDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun TransactionDialog(
    title: String,
    isDeposit: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (provider: String, phone: String, amount: Double) -> Unit
) {
    var selectedProvider by remember { mutableStateOf("Airtel Money") }
    var phoneInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = SunriseOrange,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Specify any Malawian mobile financial system operator.",
                    fontSize = 11.sp,
                    color = MutedTextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Operator toggle buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Airtel Money", "TNM Mpamba").forEach { op ->
                        val act = selectedProvider == op
                        val btnColor = if (op == "Airtel Money") MalawiRed else MalawiGreenAccent
                        OutlinedButton(
                            onClick = { selectedProvider = op },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (act) btnColor.copy(0.12f) else Color.Transparent
                            ),
                            border = BorderStroke(1.5.dp, if (act) btnColor else BorderGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = op,
                                fontWeight = FontWeight.Bold,
                                color = if (act) btnColor else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Phone Number (+265)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_phone_input")
                )

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (MWK)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("transfer_amount_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountInput.toDoubleOrNull() ?: 0.0
                            if (phoneInput.isBlank() || amt <= 0.0) {
                                // check inputs
                            } else {
                                onConfirm(selectedProvider, phoneInput, amt)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                        enabled = phoneInput.isNotBlank() && (amountInput.toDoubleOrNull() ?: 0.0) > 0.0,
                        modifier = Modifier.testTag("transfer_confirm_btn")
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
