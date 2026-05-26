package com.example.ui.market

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.ui.DobadobaViewModel
import com.example.ui.theme.*

@Composable
fun MarketTab(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val isJuniorModeActive by viewModel.isJuniorModeActive.collectAsState()
    val listings by viewModel.allMarketListings.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    var isAddingItem by remember { mutableStateOf(false) }
    var activeListingForCheckout by remember { mutableStateOf<MarketListing?>(null) }

    if (isJuniorModeActive) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("junior_market_locked_card"),
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
                        contentDescription = "Market Locked",
                        tint = SunriseOrange,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "DobaMarket Restricted 🔒",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SunriseOrange)
                    )
                    Text(
                        text = "Parental controls are active on this profile. Marketplace trading, phone listings, and mobile payments are disabled in DobaJunior Mode.",
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

    val filteredListings = listings.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.location.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                label = { Text("Search items or locations in Malawi...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = SunriseOrange) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SunriseOrange,
                    focusedLabelColor = SunriseOrange
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("market_search_bar")
            )

            Text(
                text = "Zogulitsa Pamudzi (Local Listings)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
            )

            if (filteredListings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FilterListOff, null, tint = MutedTextGray, modifier = Modifier.size(48.dp))
                        Text("No items match your search in Malawi.", color = MutedTextGray)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    gridItems(filteredListings) { item ->
                        MarketProductCard(
                            item = item,
                            onBuyNow = {
                                activeListingForCheckout = item
                                viewModel.startPaymentFlow(item)
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { isAddingItem = true },
            containerColor = SunriseOrange,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_item_fab"),
            shape = CircleShape
        ) {
            Icon(Icons.Default.AddShoppingCart, "add listing")
        }

        if (isAddingItem) {
            CreateListingDialog(
                onDismiss = { isAddingItem = false },
                onSubmit = { title, desc, price, loc ->
                    viewModel.addMarketListing(title, desc, price, loc)
                }
            )
        }

        activeListingForCheckout?.let { item ->
            PaymentDialog(
                item = item,
                viewModel = viewModel,
                onDismiss = { activeListingForCheckout = null }
            )
        }
    }
}

@Composable
fun MarketProductCard(
    item: MarketListing,
    onBuyNow: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("market_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SunriseOrange.copy(0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Inventory, "items", tint = SunriseOrange, modifier = Modifier.size(32.dp))
                    Text("Product Image", color = MutedTextGray, fontSize = 9.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "MWK " + String.format("%,.0f", item.priceMwk),
                fontWeight = FontWeight.Black,
                color = SunriseOrange,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, "or", tint = MutedTextGray, modifier = Modifier.size(11.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = item.location, fontSize = 10.sp, color = MutedTextGray, maxLines = 1)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBuyNow,
                colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Prepay Escrow", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CreateListingDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, Double, String) -> Unit
) {
    var rawTitle by remember { mutableStateOf("") }
    var rawPrice by remember { mutableStateOf("") }
    var rawDesc by remember { mutableStateOf("") }
    var rawLoc by remember { mutableStateOf("Lilongwe") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Gulantsani Chinthu (Add Listing)", fontWeight = FontWeight.Black, fontSize = 16.sp, color = SunriseOrange)

                OutlinedTextField(
                    value = rawTitle,
                    onValueChange = { rawTitle = it },
                    label = { Text("Product name (Mapepala / Zogulitsa)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rawPrice,
                    onValueChange = { rawPrice = it },
                    label = { Text("Price in MWK (Mtengo)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rawDesc,
                    onValueChange = { rawDesc = it },
                    label = { Text("Description (Tsatanetsatane)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rawLoc,
                    onValueChange = { rawLoc = it },
                    label = { Text("Location (Boma / Mudzi)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val prVal = rawPrice.toDoubleOrNull() ?: 0.0
                            onSubmit(rawTitle, rawDesc, prVal, rawLoc)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                        enabled = rawTitle.isNotBlank() && rawPrice.isNotBlank()
                    ) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentDialog(
    item: MarketListing,
    viewModel: DobadobaViewModel,
    onDismiss: () -> Unit
) {
    val process by viewModel.isPaymentProcessing.collectAsState()
    val success by viewModel.isPaymentSuccess.collectAsState()

    var mobileMoneyNo by remember { mutableStateOf("") }
    val paymentTypes = listOf("Airtel Money", "TNM Mpamba")
    var selectedPType by remember { mutableStateOf(paymentTypes[0]) }

    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Dobadoba Escrow Payment 🛡️", fontWeight = FontWeight.Black, fontSize = 16.sp, color = SunriseOrange)

                if (!success) {
                    Text("Prepay Escrow deposit for: \n${item.title}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Amount: MWK " + String.format("%,.0f", item.priceMwk), fontWeight = FontWeight.Black, color = SunriseOrange)

                    Text("Recipient Seller: ${item.sellerName} (${item.location})", fontSize = 11.sp, color = MutedTextGray)

                    Column {
                        Text("Select Mobile Money Operator", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MutedTextGray)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            paymentTypes.forEach { pType ->
                                val sel = selectedPType == pType
                                OutlinedButton(
                                    onClick = { selectedPType = pType },
                                    modifier = Modifier.weight(1f),
                                    border = BorderStroke(1.dp, if (sel) SunriseOrange else BorderGray),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (sel) SunriseOrange.copy(0.08f) else Color.Transparent
                                    )
                                ) {
                                    Text(pType, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = mobileMoneyNo,
                        onValueChange = { mobileMoneyNo = it },
                        label = { Text("Mobile Phone (+265)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("payment_number_input")
                    )

                    if (process) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = SunriseOrange)
                        Text("Requesting USSD prompt popup. Please confirm PIN on your phone...", fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    } else {
                        Button(
                            onClick = {
                                if (mobileMoneyNo.length < 9) {
                                    Toast.makeText(context, "Please enter valid Malawi phone!", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.executeMobileMoneyPayment(mobileMoneyNo, selectedPType)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                            modifier = Modifier.fillMaxWidth().testTag("payment_confirm_btn")
                        ) {
                            Text("Confirm Prepayment", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Icon(Icons.Default.CheckCircle, "success", tint = MalawiGreen, modifier = Modifier.size(64.dp).align(Alignment.CenterHorizontally))
                    Text("PAYMENT COMPLETED SECURELY!", fontWeight = FontWeight.Black, color = MalawiGreen, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text("MWK " + String.format("%,.0f", item.priceMwk) + " is held in Escrow safely. It will release to the seller once the parcel is delivered.", fontSize = 12.sp, textAlign = TextAlign.Center)

                    Button(
                        onClick = {
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Market", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
