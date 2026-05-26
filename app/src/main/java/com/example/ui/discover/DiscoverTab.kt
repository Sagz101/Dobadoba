package com.example.ui.discover

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import org.maplibre.android.MapLibre
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.annotations.MarkerOptions
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun DiscoverTab(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentSubView by remember { mutableStateOf("Main") } // Main, Travel, Map, Groups, Reels
    var searchQuery by remember { mutableStateOf("") }

    // DobaTravel State
    var travelMode by remember { mutableStateOf("Bus") } // Bus, Flight, Minibus
    var originCity by remember { mutableStateOf("Lilongwe") }
    var destinationCity by remember { mutableStateOf("Blantyre") }
    var departureDate by remember { mutableStateOf("25/05/2026") }
    var passengersCount by remember { mutableStateOf(1) }
    var isTravelSearched by remember { mutableStateOf(false) }
    var selectedSeat by remember { mutableStateOf<String?>(null) }
    var travelPaymentMethod by remember { mutableStateOf("Airtel Money") }
    var bookedTripTicket by remember { mutableStateOf<TravelTrip?>(null) }
    var isBookingConfirmed by remember { mutableStateOf(false) }

    // DobaMap State
    var mapSearchQuery by remember { mutableStateOf("") }
    var selectedMapCity by remember { mutableStateOf("Lilongwe") }

    val userGroups by viewModel.userGroupsList.collectAsState()
    val travelHistory by viewModel.travelBookings.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MalawiRed)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentSubView != "Main") {
                    IconButton(
                        onClick = { currentSubView = "Main" },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = when (currentSubView) {
                        "Main" -> "Discover"
                        "Travel" -> "DobaTravel"
                        "Map" -> "DobaMap — Malawi"
                        "Groups" -> "My Communities"
                        else -> "Discover"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                if (currentSubView == "Main") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(0.18f))
                            .clickable {
                                Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show()
                            }
                            .padding(6.dp)
                    ) {
                        Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            when (currentSubView) {
                "Main" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search places, bookings, groups...", fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MalawiRed) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MalawiRed,
                                unfocusedBorderColor = BorderGray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .testTag("discover_search_bar")
                        )

                        Text(
                            text = "Trending in Malawi 🇲🇼",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedTextGray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val hashtags = listOf("#MalawiFootball", "#LakeofStars", "#MalawiFood", "#BlantyreBeat", "#AXATravels", "#ChamboSnacks")
                            hashtags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(LightRedTint)
                                        .clickable {
                                            searchQuery = tag.substring(1)
                                            Toast.makeText(context, "Showing posts for $tag", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MalawiRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Quick Actions",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedTextGray,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        val actions = listOf(
                            Triple("Book Travel", Icons.Default.DirectionsBus, "Travel"),
                            Triple("DobaMap", Icons.Default.Map, "Map"),
                            Triple("Watch Reels", Icons.Default.PlayCircle, "Reels"),
                            Triple("DobaGroups", Icons.Default.Groups, "Groups")
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            userScrollEnabled = false
                        ) {
                            gridItems(actions) { action ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (action.third == "Reels") {
                                                viewModel.currentTab.value = "Discover" // Simulating Reels view trigger
                                                Toast.makeText(context, "Opening DobaReels at bottom!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                currentSubView = action.third
                                            }
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, BorderGray)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = action.second,
                                            contentDescription = action.first,
                                            tint = SunriseOrange,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = action.first,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(LightRedTint, shape = RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Offline Bundle Pack Active",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MalawiRed
                                    )
                                    Text(
                                        "Precompiled districts coordinates offline to run smoothly with intermittent data.",
                                        fontSize = 10.sp,
                                        color = MutedTextGray,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                "Travel" -> {
                    // Local state variables for station search and seat selection
                    var busStationSearchQuery by remember { mutableStateOf("") }
                    var selectedBusStation by remember { mutableStateOf<String?>(null) }
                    var selectingSeatsForSchedule by remember { mutableStateOf<Triple<String, String, Double>?>(null) } // Triple(OperatorName, DepartureTime, PriceDouble)
                    var selectedSeatsList by remember { mutableStateOf<List<Int>>(emptyList()) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Switch travel modes (Bus, Flight, Minibus)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Bus", "Flight", "Minibus").forEach { mode ->
                                val active = travelMode == mode
                                Button(
                                    onClick = {
                                        travelMode = mode
                                        isTravelSearched = false
                                        isBookingConfirmed = false
                                        selectedBusStation = null
                                        selectingSeatsForSchedule = null
                                        selectedSeatsList = emptyList()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (active) MalawiRed else Color(0xFFF5F5F5),
                                        contentColor = if (active) Color.White else MutedTextGray
                                    ),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(mode, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Section 1: Bus Terminal & Station Finder (FIX 4.1)
                        if (travelMode == "Bus" && selectedBusStation == null && !isBookingConfirmed) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text("Bus Terminal Station Depot Finder 🚌", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SunriseOrange)
                                    Text("Dynamically search and select a departing bus terminal station in Malawi:", fontSize = 11.sp, color = MutedTextGray)

                                    OutlinedTextField(
                                        value = busStationSearchQuery,
                                        onValueChange = { busStationSearchQuery = it },
                                        placeholder = { Text("Search depot, terminal...", fontSize = 12.sp) },
                                        leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("bus_station_search_input")
                                    )

                                    val stations = listOf(
                                        "Lilongwe Depot (Devil Street)",
                                        "Blantyre Wenela Terminal",
                                        "Mzuzu Main Bus Station",
                                        "Zomba Town Terminal Depot",
                                        "Liwonde Junction Depot Station"
                                    )
                                    val filteredStations = stations.filter { it.contains(busStationSearchQuery, ignoreCase = true) }

                                    filteredStations.forEach { station ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.Gray.copy(0.06f))
                                                .clickable {
                                                    selectedBusStation = station
                                                    Toast.makeText(context, "Terminal selected: $station. Showing departing schedules.", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.LocationOn, "Location", tint = MalawiRed, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(station, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Icon(Icons.Default.ChevronRight, "Chevron", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        } else if (travelMode == "Bus" && selectedBusStation != null && selectingSeatsForSchedule == null && !isBookingConfirmed) {
                            // Selected station header
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SunriseOrange.copy(0.08f)),
                                border = BorderStroke(1.dp, SunriseOrange.copy(0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Selected Terminal Station", fontSize = 10.sp, color = MutedTextGray, fontWeight = FontWeight.Bold)
                                        Text(selectedBusStation!!, fontWeight = FontWeight.Black, fontSize = 13.sp, color = SunriseOrange)
                                    }
                                    TextButton(onClick = { selectedBusStation = null }) {
                                        Text("Change", fontWeight = FontWeight.Bold, color = MalawiRed)
                                    }
                                }
                            }
                        }

                        // Search parameters card if selected terminal or not bus
                        if ((travelMode != "Bus" || selectedBusStation != null) && selectingSeatsForSchedule == null && !isBookingConfirmed) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text("Origin City (Koyambira)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MutedTextGray)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("Lilongwe", "Blantyre", "Mzuzu").forEach { city ->
                                            val active = originCity == city
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(if (active) LightRedTint else Color(0xFFF5F5F5))
                                                    .clickable { originCity = city }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(city, fontSize = 11.sp, color = if (active) MalawiRed else MutedTextGray)
                                            }
                                        }
                                    }

                                    Text("Destination City (Kopitira)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MutedTextGray)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("Blantyre", "Lilongwe", "Mangochi").forEach { city ->
                                            val active = destinationCity == city
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(if (active) LightRedTint else Color(0xFFF5F5F5))
                                                    .clickable { destinationCity = city }
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text(city, fontSize = 11.sp, color = if (active) MalawiRed else MutedTextGray)
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = departureDate,
                                            onValueChange = { departureDate = it },
                                            label = { Text("Trip Date") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = passengersCount.toString(),
                                            onValueChange = { passengersCount = it.toIntOrNull() ?: 1 },
                                            label = { Text("Seats") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.width(80.dp)
                                        )
                                    }

                                    Button(
                                        onClick = { isTravelSearched = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Find Available Schedules 🔍", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // List of available schedules departing from the searched station/terminals
                        if (isTravelSearched && selectingSeatsForSchedule == null && !isBookingConfirmed) {
                            Text(
                                "Available Operators in Malawi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            val schedules = listOf(
                                Triple("AXA Coach Services", "08:15 AM • Premium Streamline", 18500.0),
                                Triple("Sososo Coaches", "10:30 AM • Air Conditioned", 19000.0),
                                Triple("Zupco Express", "01:45 PM • Standard Economy", 12000.0)
                            )

                            schedules.forEach { sc ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, BorderGray),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(sc.first, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("MWK " + String.format("%,.0f", sc.third), color = SunriseOrange, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        }
                                        Text(sc.second, fontSize = 12.sp, color = MutedTextGray)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                selectedSeatsList = emptyList()
                                                selectingSeatsForSchedule = Triple(sc.first, sc.second.substringBefore(" •"), sc.third)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Select Seats & Reserve Ticket", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Section 2: Seat Selection & Live Capacity (FIX 4.2)
                        if (selectingSeatsForSchedule != null && !isBookingConfirmed) {
                            val schedule = selectingSeatsForSchedule!!
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, BorderGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Bus Seating Arrangement Chart 💺", fontWeight = FontWeight.Black, fontSize = 15.sp, color = SunriseOrange)
                                    Text("Operator: ${schedule.first} • Departure: ${schedule.second}", fontSize = 12.sp, color = MutedTextGray)

                                    // Display seat status legend
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(16.dp).background(Color.LightGray.copy(0.4f), shape = RoundedCornerShape(3.dp)))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Available", fontSize = 11.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(16.dp).background(MalawiGreen, shape = RoundedCornerShape(3.dp)))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Selected", fontSize = 11.sp)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(16.dp).background(MalawiRed, shape = RoundedCornerShape(3.dp)))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reserved/Sold", fontSize = 11.sp)
                                        }
                                    }

                                    // Render 40 individual seats grid
                                    // 10 rows, 4 seats per row with an aisle in the middle (e.g. Columns: [1, 2] Aisle [3, 4])
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        for (row in 0 until 10) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                // Seat column 1 & 2
                                                val s1 = row * 4 + 1
                                                val s2 = row * 4 + 2
                                                val isReservedS1 = s1 % 5 == 0 || s1 in listOf(2, 12, 22)
                                                val isReservedS2 = s2 % 5 == 0 || s2 in listOf(6, 16, 26)
                                                val isSelectedS1 = selectedSeatsList.contains(s1)
                                                val isSelectedS2 = selectedSeatsList.contains(s2)

                                                // Seat 1
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                            if (isReservedS1) MalawiRed else if (isSelectedS1) MalawiGreen else Color.LightGray.copy(
                                                                0.4f
                                                            )
                                                        )
                                                        .clickable(enabled = !isReservedS1) {
                                                            selectedSeatsList =
                                                                if (isSelectedS1) selectedSeatsList - s1 else selectedSeatsList + s1
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(s1.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isReservedS1 || isSelectedS1) Color.White else Color.Black)
                                                }

                                                // Seat 2
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                            if (isReservedS2) MalawiRed else if (isSelectedS2) MalawiGreen else Color.LightGray.copy(
                                                                0.4f
                                                            )
                                                        )
                                                        .clickable(enabled = !isReservedS2) {
                                                            selectedSeatsList =
                                                                if (isSelectedS2) selectedSeatsList - s2 else selectedSeatsList + s2
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(s2.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isReservedS2 || isSelectedS2) Color.White else Color.Black)
                                                }

                                                // Aisle
                                                Spacer(modifier = Modifier.width(24.dp))

                                                // Seat column 3 & 4
                                                val s3 = row * 4 + 3
                                                val s4 = row * 4 + 4
                                                val isReservedS3 = s3 % 5 == 0 || s3 in listOf(8, 18, 28)
                                                val isReservedS4 = s4 % 5 == 0 || s4 in listOf(10, 20, 30)
                                                val isSelectedS3 = selectedSeatsList.contains(s3)
                                                val isSelectedS4 = selectedSeatsList.contains(s4)

                                                // Seat 3
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                            if (isReservedS3) MalawiRed else if (isSelectedS3) MalawiGreen else Color.LightGray.copy(
                                                                0.4f
                                                            )
                                                        )
                                                        .clickable(enabled = !isReservedS3) {
                                                            selectedSeatsList =
                                                                if (isSelectedS3) selectedSeatsList - s3 else selectedSeatsList + s3
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(s3.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isReservedS3 || isSelectedS3) Color.White else Color.Black)
                                                }

                                                // Seat 4
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(
                                                            if (isReservedS4) MalawiRed else if (isSelectedS4) MalawiGreen else Color.LightGray.copy(
                                                                0.4f
                                                            )
                                                        )
                                                        .clickable(enabled = !isReservedS4) {
                                                            selectedSeatsList =
                                                                if (isSelectedS4) selectedSeatsList - s4 else selectedSeatsList + s4
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(s4.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isReservedS4 || isSelectedS4) Color.White else Color.Black)
                                                }
                                            }
                                        }
                                    }

                                    // Calculations & Remaining Spots
                                    val totalReserved = 14 // fixed preset simulation
                                    val totalSelected = selectedSeatsList.size
                                    val remainingSpots = 40 - totalReserved - totalSelected
                                    val totalTicketPrice = totalSelected * schedule.third

                                    HorizontalDivider(color = Color.Gray.copy(0.2f))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Spots remaining on bus:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("$remainingSpots / 40", fontWeight = FontWeight.ExtraBold, color = MalawiGreen)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Seats Selected:", fontSize = 12.sp)
                                        Text(if (selectedSeatsList.isEmpty()) "None" else selectedSeatsList.joinToString(", "), fontWeight = FontWeight.Bold, color = SunriseOrange)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Total Payout Price:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("MWK " + String.format("%,.0f", totalTicketPrice), fontSize = 18.sp, fontWeight = FontWeight.Black, color = MalawiRed)
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { selectingSeatsForSchedule = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Cancel")
                                        }

                                        Button(
                                            onClick = {
                                                if (selectedSeatsList.isEmpty()) {
                                                    Toast.makeText(context, "Please select at least 1 seat!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    isTravelSearched = false
                                                    isBookingConfirmed = true
                                                    bookedTripTicket = TravelTrip(
                                                        id = "TR-${(100..999).random()}",
                                                        operatorName = schedule.first,
                                                        origin = originCity,
                                                        destination = destinationCity,
                                                        date = departureDate,
                                                        rTime = schedule.second,
                                                        price = "MWK " + String.format("%,.0f", totalTicketPrice),
                                                        seat = "Seats: " + selectedSeatsList.joinToString(", "),
                                                        qrCode = "QR-${(1000..9999).random()}"
                                                    )
                                                    // Save directly into travelBookings viewmodel flow
                                                    val mList = viewModel.travelBookings.value.toMutableList()
                                                    bookedTripTicket?.let { mList.add(0, it) }
                                                    viewModel.travelBookings.value = mList
                                                    Toast.makeText(context, "Tickets booked securely!", Toast.LENGTH_LONG).show()
                                                    selectingSeatsForSchedule = null
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MalawiGreen),
                                            modifier = Modifier.weight(1.5f)
                                        ) {
                                            Text("Pay & Generate Pass", fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }
                        }

                        // Section 3: Digital Boarding Pass Display (FIX 4.3)
                        if (isBookingConfirmed && bookedTripTicket != null) {
                            val ticket = bookedTripTicket!!
                            Text(
                                "Your Digital Boarding Pass 🎫",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.5.dp, MalawiGreen),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Ticket Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(ticket.operatorName, fontWeight = FontWeight.Black, fontSize = 16.sp, color = SunriseOrange)
                                            Text("MALAWI EXPRESS LINE", fontSize = 10.sp, color = MutedTextGray, fontWeight = FontWeight.Bold)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(50))
                                                .background(MalawiGreen.copy(0.12f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("PAID", fontSize = 10.sp, fontWeight = FontWeight.Black, color = MalawiGreen)
                                        }
                                    }

                                    // Boarding Timeline (FIX 4.3 Timeline element)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(horizontalAlignment = Alignment.Start) {
                                            Text(ticket.origin.uppercase().take(3), fontWeight = FontWeight.Black, fontSize = 24.sp)
                                            Text(ticket.origin, fontSize = 11.sp, color = MutedTextGray)
                                            Text("Depart: " + ticket.rTime, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Timeline visual bar
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                                        ) {
                                            Text("On-Board: 15m prior", fontSize = 9.sp, color = MalawiGreen, fontWeight = FontWeight.Bold)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(modifier = Modifier.size(6.dp).background(MalawiRed, CircleShape))
                                                Box(modifier = Modifier.weight(1f).height(2.dp).background(Color.Gray.copy(0.4f))) {
                                                    Box(modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight().background(MalawiGreen))
                                                }
                                                Icon(Icons.Default.DirectionsBus, "Bus Icon", tint = MalawiGreen, modifier = Modifier.size(16.dp))
                                                Box(modifier = Modifier.weight(1f).height(2.dp).background(Color.Gray.copy(0.4f)))
                                                Box(modifier = Modifier.size(6.dp).background(MalawiGreen, CircleShape))
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(ticket.destination.uppercase().take(3), fontWeight = FontWeight.Black, fontSize = 24.sp)
                                            Text(ticket.destination, fontSize = 11.sp, color = MutedTextGray)
                                            Text("Duration: ~4.5h", fontSize = 10.sp, color = MutedTextGray)
                                        }
                                    }

                                    HorizontalDivider(color = Color.Gray.copy(0.2f))

                                    // Ticket details meta rows
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("DATE", fontSize = 9.sp, color = MutedTextGray)
                                            Text(ticket.date, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Column {
                                            Text("SEAT(S)", fontSize = 9.sp, color = MutedTextGray)
                                            Text(ticket.seat.substringAfter("Seats: "), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SunriseOrange)
                                        }
                                        Column {
                                            Text("PRICE", fontSize = 9.sp, color = MutedTextGray)
                                            Text(ticket.price, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MalawiGreen)
                                        }
                                    }

                                    // Custom Abstract Animated/Drawn QR Code via Canvas (FIX 4.3)
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(140.dp)
                                                .background(Color.White)
                                                .border(1.dp, BorderGray)
                                                .padding(12.dp)
                                        ) {
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                // We can draw a high fidelity abstract vector barcode / QR code block
                                                val squareSize = size.width / 14f
                                                val qrPattern = listOf(
                                                    listOf(1,1,1,1,1,1,1,0,1,0,1,1,1,1),
                                                    listOf(1,0,0,0,0,0,1,0,1,1,0,1,0,1),
                                                    listOf(1,0,1,1,1,0,1,0,0,1,1,0,0,1),
                                                    listOf(1,0,1,1,1,0,1,0,1,0,0,1,1,1),
                                                    listOf(1,0,1,1,1,0,1,0,1,1,0,1,0,1),
                                                    listOf(1,0,0,0,0,0,1,0,0,1,1,0,1,1),
                                                    listOf(1,1,1,1,1,1,1,0,1,0,1,0,0,1),
                                                    listOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0),
                                                    listOf(1,1,0,1,0,1,1,0,1,1,1,1,1,1),
                                                    listOf(0,1,0,1,0,0,1,0,1,0,0,0,0,0),
                                                    listOf(1,0,1,1,0,1,0,0,1,0,1,1,1,0),
                                                    listOf(1,0,0,1,1,0,1,0,1,0,1,1,1,0),
                                                    listOf(1,1,1,0,0,1,0,0,1,0,1,1,1,0),
                                                    listOf(1,1,1,1,1,1,1,0,1,1,1,0,0,1)
                                                )
                                                for (r in 0 until 14) {
                                                    for (c in 0 until 14) {
                                                        if (qrPattern[r][c] == 1) {
                                                            drawRect(
                                                                color = Color.Black,
                                                                topLeft = androidx.compose.ui.geometry.Offset(c * squareSize, r * squareSize),
                                                                size = androidx.compose.ui.geometry.Size(squareSize, squareSize)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        Text("BOARDING QR PASS ID: " + ticket.qrCode, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text("Show QR certificate to security loader during check-in", fontSize = 10.sp, color = MutedTextGray)
                                    }

                                    // Add to Google Wallet shortcut button (FIX 4.3 Add details buttons)
                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "Added '${ticket.operatorName} Pass' to Google Wallet successfully! 💳", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("add_to_wallet_btn")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.CreditCard, contentDescription = "Wallet", tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Add to Google Wallet", fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    isBookingConfirmed = false
                                    selectedBusStation = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Go Back to Search")
                            }
                        }

                        // Your past history
                        Text(
                            "Your Travel Booking History",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )

                        travelHistory.forEach { trip ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(trip.operatorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${trip.origin} ➔ ${trip.destination}", fontSize = 11.sp)
                                        Text("${trip.date} • ${trip.rTime}", fontSize = 11.sp, color = MutedTextGray)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(trip.price, fontWeight = FontWeight.Bold, color = SunriseOrange, fontSize = 13.sp)
                                        Text(trip.seat, fontSize = 11.sp, color = MutedTextGray)
                                    }
                                }
                            }
                        }
                    }
                }

                "Map" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = mapSearchQuery,
                            onValueChange = { mapSearchQuery = it },
                            placeholder = { Text("Search location coordinates in Malawi...") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Select District Hub", fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Lilongwe", "Blantyre", "Mzuzu").forEach { city ->
                                val active = selectedMapCity == city
                                Button(
                                    onClick = { selectedMapCity = city },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (active) SunriseOrange else Color.LightGray
                                    )
                                ) {
                                    Text(city, color = Color.White)
                                }
                            }
                        }

                        DobaMapComponent(
                            cityName = selectedMapCity,
                            searchQuery = mapSearchQuery,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, BorderGray)
                        )
                    }
                }

                "Groups" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("My Joined Communities", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        userGroups.forEach { grp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VerifiedUser, "verified", tint = MalawiGreen, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(grp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Reels Screen built-in to support discovering videos
@Composable
fun ReelsTab(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val discoverPosts by viewModel.discoverPosts.collectAsState()
    val playSpeed by viewModel.reelPlaySpeed.collectAsState()
    val activeIndex by viewModel.currentReelIndex.collectAsState()
    val isDuetMode by viewModel.isDuetMode.collectAsState()
    val context = LocalContext.current

    val isJuniorModeActive by viewModel.isJuniorModeActive.collectAsState()
    val adultKeywords = listOf("[18+]", "🔞", "🌶️", "adult", "dating", "casino", "gamble", "nsfw")

    val baseReels = discoverPosts.filter { it.isVideo }
    val reelList = if (isJuniorModeActive) {
        baseReels.filter { post ->
            val capEng = post.captionEnglish.lowercase()
            val capChi = post.captionChichewa.lowercase()
            val user = post.username.lowercase()
            adultKeywords.none { keyword ->
                capEng.contains(keyword.lowercase()) ||
                capChi.contains(keyword.lowercase()) ||
                user.contains(keyword.lowercase())
            }
        }
    } else {
        baseReels
    }

    if (reelList.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No short video reels available.", color = MutedTextGray)
        }
        return
    }

    val activeReel = reelList[activeIndex.coerceIn(0, reelList.lastIndex)]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MalawiBlack)
    ) {
        if (isDuetMode) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(ChambaSlate),
                    contentAlignment = Alignment.Center
                ) {
                    ReelVideoPlaceholder(post = activeReel, speed = playSpeed)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Card(colors = CardDefaults.cardColors(containerColor = SunriseOrange)) {
                            Text("Original", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MalawiGreenAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Videocam, "duet cam", tint = MalawiGreen, modifier = Modifier.size(48.dp))
                        Text("Recording Duet...", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Audio Sync Active", color = MutedTextGray, fontSize = 9.sp)
                    }
                }
            }
        } else {
            ReelVideoPlaceholder(post = activeReel, speed = playSpeed)
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(0.5f, 1.0f, 2.0f).forEach { speed ->
                val isSel = playSpeed == speed
                SuggestionChip(
                    onClick = { viewModel.reelPlaySpeed.value = speed },
                    label = { Text("${speed}x", color = if (isSel) Color.White else MutedTextGray, fontWeight = FontWeight.Bold) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSel) SunriseOrange else MalawiBlack.copy(alpha = 0.6f)
                    ),
                    border = BorderStroke(1.dp, if (isSel) SunriseOrange else Color.Transparent)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReelActionButton(
                icon = Icons.Default.Favorite,
                text = "${activeReel.likesCount + 120}",
                tint = if (activeReel.isLiked) MalawiRed else Color.White,
                onClick = { viewModel.toggleLikePost(activeReel) }
            )

            ReelActionButton(icon = Icons.Default.Comment, text = "${activeReel.commentsCount + 5}", onClick = {})

            ReelActionButton(
                icon = Icons.Default.People,
                text = if (isDuetMode) "Solo" else "Duet",
                tint = if (isDuetMode) SunriseOrange else Color.White,
                onClick = { viewModel.isDuetMode.value = !viewModel.isDuetMode.value }
            )

            var showReelReport by remember { mutableStateOf(false) }
            Box {
                ReelActionButton(
                    icon = Icons.Default.MoreVert,
                    text = "Report",
                    onClick = { showReelReport = true }
                )
                DropdownMenu(
                    expanded = showReelReport,
                    onDismissRequest = { showReelReport = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Report Reel") },
                        onClick = {
                            showReelReport = false
                            Toast.makeText(context, "Mndandanda wadziwika. Reel content reported and queued for 24-hour review.", Toast.LENGTH_LONG).show()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Flag, contentDescription = "Report", tint = MalawiRed)
                        },
                        modifier = Modifier.testTag("report_reel_option_item")
                    )
                }
            }

            RotatingMusicDisc()
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 90.dp, end = 80.dp)
        ) {
            Text(activeReel.username, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            Text(activeReel.captionEnglish, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, maxLines = 2)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    if (activeIndex > 0) {
                        viewModel.currentReelIndex.value = activeIndex - 1
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(0.6f))
            ) {
                Icon(Icons.Default.KeyboardArrowUp, "prev", tint = Color.White)
            }

            IconButton(
                onClick = {
                    if (activeIndex < reelList.lastIndex) {
                        viewModel.currentReelIndex.value = activeIndex + 1
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(0.6f))
            ) {
                Icon(Icons.Default.KeyboardArrowDown, "next", tint = Color.White)
            }
        }
    }
}

@Composable
fun ReelVideoPlaceholder(post: Post, speed: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ChambaSlateLight,
                        MalawiBlack
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.MovieFilter, null, tint = SunriseOrange, modifier = Modifier.size(72.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Streaming Short-Video Reel", color = Color.White, fontWeight = FontWeight.Bold)
            Text("Adaptive 360p • Speed: ${speed}x", color = MalawiGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ReelActionButton(
    icon: ImageVector,
    text: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint)
        }
        Text(text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun RotatingMusicDisc() {
    val infiniteTransition = rememberInfiniteTransition(label = "musicDiscRotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )
    Box(
        modifier = Modifier
            .size(44.dp)
            .graphicsLayer { rotationZ = angle }
            .clip(CircleShape)
            .background(Color.DarkGray)
            .border(3.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(SunriseOrange)
        )
    }
}

@Composable
fun DobaMapComponent(
    cityName: String,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle

    // Safely attempt to initialize MapLibre
    var mapInitSuccess by remember { mutableStateOf(false) }
    var initErrorMsg by remember { mutableStateOf<String?>(null) }
    
    remember(cityName) {
        try {
            MapLibre.getInstance(context)
            mapInitSuccess = true
        } catch (e: Throwable) {
            android.util.Log.e("DobaMap", "MapLibre native initialization failed", e)
            initErrorMsg = e.localizedMessage ?: "Native linkage failed"
            mapInitSuccess = false
        }
    }

    if (!mapInitSuccess) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f), RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, BorderGray.copy(0.3f)), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Map, contentDescription = "Map unavailable", tint = MalawiRed, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Interactive Map Offline", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SunriseOrange)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "MapLibre navigation is deactivated due to device architecture constraints ($initErrorMsg). All social hubs, payments, and location-based filters are fully operational!",
                fontSize = 12.sp,
                color = MutedTextGray,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MalawiGreen)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Using Offline Localization Hub", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    var mapLibreMapState by remember { mutableStateOf<MapLibreMap?>(null) }

    // Coordinates for Malawian Cities
    val coordinates = remember {
        mapOf(
            "Lilongwe" to LatLng(-13.9626, 33.7741),
            "Blantyre" to LatLng(-15.7861, 35.0058),
            "Mzuzu" to LatLng(-11.4584, 34.0150)
        )
    }

    // Effect to update camera center when cityName or searchQuery changes
    LaunchedEffect(cityName, searchQuery, mapLibreMapState) {
        val map = mapLibreMapState ?: return@LaunchedEffect
        val targetCoords = if (searchQuery.isNotBlank()) {
            val normalizedQuery = searchQuery.lowercase().trim()
            if (normalizedQuery.contains("blantyre")) LatLng(-15.7861, 35.0058)
            else if (normalizedQuery.contains("mzuzu")) LatLng(-11.4584, 34.0150)
            else if (normalizedQuery.contains("lilongwe")) LatLng(-13.9626, 33.7741)
            else coordinates[cityName] ?: LatLng(-13.9626, 33.7741)
        } else {
            coordinates[cityName] ?: LatLng(-13.9626, 33.7741)
        }

        try {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(targetCoords, 10.0))
        } catch (e: Throwable) {
            // Safe catch
        }
    }

    // Safely build MapView
    val mapView = remember {
        try {
            MapView(context).apply {
                getMapAsync { map ->
                    mapLibreMapState = map
                    try {
                        // Add pins/markers for Malawi hubs
                        map.addMarker(MarkerOptions().position(LatLng(-13.9626, 33.7741)).title("Lilongwe Hub - Head Office"))
                        map.addMarker(MarkerOptions().position(LatLng(-15.7861, 35.0058)).title("Blantyre Hub - Southern depot"))
                        map.addMarker(MarkerOptions().position(LatLng(-11.4584, 34.0150)).title("Mzuzu Hub - Northern terminal"))

                        // Map styling
                        map.setStyle(Style.Builder().fromUri("https://demotiles.maplibre.org/style.json")) { style ->
                            // Style loaded
                        }

                        // Initial position
                        val initialLoc = coordinates[cityName] ?: LatLng(-13.9626, 33.7741)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLoc, 8.0))
                    } catch (e: Throwable) {
                        android.util.Log.e("DobaMap", "Map configuration failed", e)
                    }
                }
            }
        } catch (e: Throwable) {
            null
        }
    }

    if (mapView == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f), RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, BorderGray.copy(0.3f)), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Map, contentDescription = "Map view creation failed", tint = MalawiRed, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("MapView Creation Failed", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = SunriseOrange)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Failed to construct render viewport. This commonly occurs on emulators without adequate graphical layer resources.",
                fontSize = 12.sp,
                color = MutedTextGray,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    // Observe lifecycle events to forward them to MapView
    DisposableEffect(lifecycle, mapView) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            try {
                when (event) {
                    androidx.lifecycle.Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                    androidx.lifecycle.Lifecycle.Event.ON_START -> mapView.onStart()
                    androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    androidx.lifecycle.Lifecycle.Event.ON_STOP -> mapView.onStop()
                    androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                    else -> {}
                }
            } catch (e: Throwable) {
                // Safe catch framework crashes
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            try {
                mapView.onDestroy()
            } catch (e: Throwable) {
                // Ignore destruction issues
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
