package com.example.ui.chat

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.DobadobaViewModel
import com.example.ui.theme.*

@Composable
fun ChatTab(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.allMessages.collectAsState()
    val isOnline by viewModel.isConnected.collectAsState()
    val isGhostModeActive by viewModel.isGhostModeActive.collectAsState()

    var chatMode by remember { mutableStateOf("Group") } // Group or Direct
    var activeMessageText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val filteredMessages = messages.filter { msg ->
        if (chatMode == "Group") {
            msg.recipient == "Chonde Group"
        } else {
            msg.recipient != "Chonde Group"
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Connectivity Status Banner / 2G-3G Testing Simulating Header
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isOnline) MalawiGreen.copy(0.1f) else MalawiRed.copy(0.1f)
                ),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) MalawiGreen else MalawiRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOnline) {
                                if (isGhostModeActive) "Ghost Mode Active 👻 Indicator Hidden" else "Zili pa Intaneti (Online Mode)"
                            } else {
                                "Offline Mode (2G/3G queued simulation)"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) MalawiGreen else MalawiRed
                        )
                    }

                    Button(
                        onClick = { viewModel.toggleConnectivity() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOnline) MalawiRed.copy(0.12f) else MalawiGreen.copy(0.12f)
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("network_toggle_button")
                    ) {
                        Text(
                            text = if (isOnline) "Disconnect" else "Sync / Match",
                            color = if (isOnline) MalawiRed else MalawiGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Chat Category Tabs
            TabRow(
                selectedTabIndex = if (chatMode == "Group") 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SunriseOrange
            ) {
                Tab(
                    selected = chatMode == "Group",
                    onClick = { chatMode = "Group" },
                    text = { Text("Chonde Group (Community)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = chatMode == "Direct",
                    onClick = { chatMode = "Direct" },
                    text = { Text("Macheza (Direct Notes)", fontWeight = FontWeight.Bold) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = false,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMessages) { msg ->
                    val isMe = msg.sender == "Ine (Me)"
                    val alignment = if (isMe) Alignment.End else Alignment.Start
                    val bubbleBg = if (isMe) SunriseOrange else MaterialTheme.colorScheme.surfaceVariant

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = alignment
                    ) {
                        Text(
                            text = msg.sender,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedTextGray,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            if (isMe) {
                                Text(
                                    text = if (msg.status == "QUEUED") "Queued ⌛" else "Sent ✓",
                                    fontSize = 9.sp,
                                    color = MutedTextGray,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .testTag("chat_msg_${msg.id}"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = bubbleBg)
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Chat Input Bar at bottom
            HorizontalDivider(color = BorderGray.copy(0.1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = activeMessageText,
                    onValueChange = { activeMessageText = it },
                    placeholder = { Text("Lembani uthenga... (type message)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (activeMessageText.isNotBlank()) {
                                viewModel.sendChatMessage(
                                    text = activeMessageText,
                                    recipient = if (chatMode == "Group") "Chonde Group" else "Chimwemwe (Friend)"
                                )
                                activeMessageText = ""
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SunriseOrange,
                        focusedLabelColor = SunriseOrange
                    )
                )

                IconButton(
                    onClick = {
                        if (activeMessageText.isNotBlank()) {
                            viewModel.sendChatMessage(
                                text = activeMessageText,
                                recipient = if (chatMode == "Group") "Chonde Group" else "Chimwemwe (Friend)"
                            )
                            activeMessageText = ""
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clip(CircleShape)
                        .background(SunriseOrange)
                        .testTag("chat_send_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = Color.White)
                }
            }
        }
    }
}
