package com.example.ui.feed

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.example.ui.DobadobaViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun FeedTab(
    viewModel: DobadobaViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.allPosts.collectAsState()
    val discoverPosts by viewModel.discoverPosts.collectAsState()
    val stories by viewModel.allStories.collectAsState()
    val userLang by viewModel.currentLanguage.collectAsState()
    val safetyAlert by viewModel.activeSafetyAlert.collectAsState()

    val context = LocalContext.current

    var feedMode by remember { mutableStateOf("Following") } // Following or Discover
    var activeStoryForView by remember { mutableStateOf<Story?>(null) }
    var isPostCreatorOpen by remember { mutableStateOf(false) }
    var isStoryCreatorOpen by remember { mutableStateOf(false) }

    val activeFeedItems = if (feedMode == "Following") posts else discoverPosts

    val adultKeywords = listOf("[18+]", "🔞", "🌶️", "adult", "dating", "casino", "gamble", "nsfw")
    val isJuniorModeActive by viewModel.isJuniorModeActive.collectAsState()

    val filteredFeedItems = if (isJuniorModeActive) {
        activeFeedItems.filter { post ->
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
        activeFeedItems
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Feed Mode Toggle Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(4.dp)
                ) {
                    TabToggleButton(
                        text = "Following",
                        isActive = feedMode == "Following",
                        onClick = { feedMode = "Following" }
                    )
                    TabToggleButton(
                        text = "Discover 🇲🇼",
                        isActive = feedMode == "Discover",
                        onClick = { feedMode = "Discover" }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                safetyAlert?.let { alert ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MalawiRed.copy(0.08f)),
                            border = BorderStroke(1.5.dp, MalawiRed)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = "Verified Urgent Broadcast",
                                        tint = MalawiRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = alert.headerTitle,
                                        color = MalawiRed,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = alert.emergencyBody,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Official National Broadcast • Signed by DoDMA",
                                        fontSize = 9.sp,
                                        color = MutedTextGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Dismiss",
                                        fontSize = 9.sp,
                                        color = MalawiRed,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier
                                            .clickable { viewModel.activeSafetyAlert.value = null }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Stories horizontal row
                item {
                    Text(
                        text = "DobaStories (24 Hours)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedTextGray,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                    )

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            val isDark = isSystemInDarkTheme()
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable {
                                        isStoryCreatorOpen = true
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .border(
                                            width = 2.dp,
                                            color = SunriseOrange,
                                            shape = CircleShape
                                        )
                                        .padding(3.dp)
                                        .background(MaterialTheme.colorScheme.background, shape = CircleShape)
                                        .border(
                                            width = 1.dp,
                                            color = if (!isDark) Color.White else ChambaSlateLight,
                                            shape = CircleShape
                                        )
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(MutedTextGray.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Your Story",
                                        tint = SunriseOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "Your Story",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.width(68.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        items(stories) { story ->
                            StoryCircleItem(story = story) {
                                activeStoryForView = story
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 8.dp)
                }

                if (filteredFeedItems.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Inbox, "empty", modifier = Modifier.size(48.dp), tint = MutedTextGray)
                            Text("No posts available.", color = MutedTextGray, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                } else {
                    items(filteredFeedItems) { post ->
                        PostCardItem(
                            post = post,
                            userLang = userLang,
                            onLikePressed = { viewModel.toggleLikePost(post) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Post
        FloatingActionButton(
            onClick = { isPostCreatorOpen = true },
            containerColor = SunriseOrange,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_post_fab"),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Publish Post")
        }

        if (isPostCreatorOpen) {
            CreatePostDialog(
                onDismiss = { isPostCreatorOpen = false },
                onSubmit = { text, isReel -> viewModel.addPost(text, isReel) }
            )
        }

        if (isStoryCreatorOpen) {
            CreateStoryDialog(
                onDismiss = { isStoryCreatorOpen = false },
                onSubmit = { text, filter -> viewModel.addStory(text, filter) }
            )
        }

        activeStoryForView?.let { story ->
            StoryViewerDialog(
                story = story,
                onDismiss = { activeStoryForView = null },
                onVote = { voteOptionA ->
                    viewModel.voteStoryPoll(story, voteOptionA)
                }
            )
        }
    }
}

@Composable
fun TabToggleButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isActive) SunriseOrange else Color.Transparent,
        animationSpec = tween(250),
        label = "tabBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) Color.White else MutedTextGray,
        animationSpec = tween(250),
        label = "tabText"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = textColor
        )
    }
}

@Composable
fun StoryCircleItem(
    story: Story,
    onClick: () -> Unit
) {
    val ringColor = when {
        story.isCloseFriends -> MalawiGreen
        story.username.contains("Mawu", ignoreCase = true) -> MalawiRed
        story.username.contains("Kulu", ignoreCase = true) -> MalawiGreen
        else -> SunriseOrange
    }
    val isDark = isSystemInDarkTheme()

    val infiniteTransition = rememberInfiniteTransition(label = "storyPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("story_${story.username}")
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .scale(pulseScale)
                .border(
                    width = 2.dp,
                    color = ringColor,
                    shape = CircleShape
                )
                .padding(3.dp)
                .background(MaterialTheme.colorScheme.background, shape = CircleShape)
                .border(
                    width = 1.dp,
                    color = if (!isDark) Color.White else ChambaSlateLight,
                    shape = CircleShape
                )
                .padding(2.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(SunriseOrange.copy(0.12f), ringColor.copy(0.18f)))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                story.username.take(1).uppercase(),
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = ringColor
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = story.username,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(68.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PostCardItem(
    post: Post,
    userLang: String,
    onLikePressed: () -> Unit
) {
    val isLight = !isSystemInDarkTheme()
    val likeScale by animateFloatAsState(
        targetValue = if (post.isLiked) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "likeAnimation"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("post_${post.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLight) 1.dp else 4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isLight) BorderGray.copy(0.4f) else Color(0xFF2E333D)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                            text = post.username.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = post.username, fontWeight = FontWeight.Bold)
                            if (post.isTrending) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.TrendingUp,
                                    "Trending",
                                    tint = SunriseOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(text = "Malawi Hub • Just now", fontSize = 11.sp, color = MutedTextGray)
                    }
                }

                // Three-dot report menu
                var showReportMenu by remember { mutableStateOf(false) }
                val context = LocalContext.current
                Box {
                    IconButton(onClick = { showReportMenu = true }, modifier = Modifier.testTag("report_post_btn_${post.id}")) {
                        Icon(Icons.Default.MoreVert, "More options", tint = MutedTextGray)
                    }
                    DropdownMenu(
                        expanded = showReportMenu,
                        onDismissRequest = { showReportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Report content") },
                            onClick = {
                                showReportMenu = false
                                Toast.makeText(context, "Mndandanda wadziwika. Content reported and queued for 24-hour review.", Toast.LENGTH_LONG).show()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Flag, contentDescription = "Report", tint = MalawiRed)
                            },
                            modifier = Modifier.testTag("report_post_option_item")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val activeCaption = if (userLang == "English") post.captionEnglish else post.captionChichewa
            Text(
                text = activeCaption,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (userLang == "English") "Chichewa translation available 🇲🇼" else "Zili mu Chicheŵa",
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = SunriseOrange,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isLight) LightRedTint else ChambaSlateLight
                    )
                    .border(
                        width = 1.dp,
                        color = if (isLight) BorderGray.copy(0.4f) else Color(0xFF2E333D).copy(0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (post.isVideo) Icons.Default.PlayCircle else Icons.Default.Image,
                        contentDescription = "Visual attachment",
                        tint = if (post.isVideo) SunriseOrange else MutedTextGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = if (post.isVideo) "Tap DobaReels or Discover to stream" else "Optimized WebP Image",
                        color = MutedTextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLikePressed) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) MalawiRed else MutedTextGray,
                            modifier = Modifier.scale(likeScale)
                        )
                    }
                    Text(text = "${post.likesCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Comment, "Comment", tint = MutedTextGray)
                    }
                    Text(text = "${post.commentsCount}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                IconButton(onClick = {}) {
                    Icon(Icons.Default.Share, "Share", tint = MutedTextGray)
                }
            }
        }
    }
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Boolean) -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var isReelVideo by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Tumizani Mavesi (Create Post) 🇲🇼", fontWeight = FontWeight.Black, fontSize = 18.sp, color = SunriseOrange)

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    placeholder = { Text("What is happening in Malawi today? Kodi chikuchitika ndi chiyani m'derali?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("create_post_textbox"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SunriseOrange,
                        focusedLabelColor = SunriseOrange
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isReelVideo,
                        onCheckedChange = { isReelVideo = it },
                        colors = CheckboxDefaults.colors(checkedColor = SunriseOrange)
                    )
                    Text("Attach as short vertical Reel video (Video fupi)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

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
                            onSubmit(rawText, isReelVideo)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                        enabled = rawText.isNotBlank(),
                        modifier = Modifier.testTag("publish_post_submit_btn")
                    ) {
                        Text("Publish", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StoryViewerDialog(
    story: Story,
    onDismiss: () -> Unit,
    onVote: (Boolean) -> Unit
) {
    var progress by remember { mutableStateOf(0.0f) }

    LaunchedEffect(Unit) {
        while (progress < 1.0f) {
            delay(100)
            progress += 0.02f
        }
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(0.95f)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = SunriseOrange,
                        trackColor = Color.White.copy(0.2f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SunriseOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(story.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(story.username, color = Color.White, fontWeight = FontWeight.Bold)
                            if (story.isCloseFriends) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MalawiGreen.copy(0.2f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("CF 💚", color = MalawiGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, "close", tint = Color.White)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp)
                            .background(ChambaSlateLight, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Photo, null, tint = SunriseOrange, modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(story.textOverlay, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                            if (story.locationTag.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                                    Icon(Icons.Default.LocationOn, "or", tint = MalawiGreen, modifier = Modifier.size(16.dp))
                                    Text(story.locationTag, color = MalawiGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (story.isPoll) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ChambaSlate),
                            border = BorderStroke(1.dp, Color.White.copy(0.12f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(story.pollQuestion, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                                val voted = story.userAnswer != 0
                                val optA = story.pollOptionA
                                val optB = story.pollOptionB

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { if (!voted) onVote(true) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (story.userAnswer == 1) SunriseOrange else Color.DarkGray
                                        )
                                    ) {
                                        Text(optA, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { if (!voted) onVote(false) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (story.userAnswer == 2) SunriseOrange else Color.DarkGray
                                        )
                                    ) {
                                        Text(optB, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (voted) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Votes: ${story.pollVotesA}", color = SunriseOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("Votes: ${story.pollVotesB}", color = MutedTextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateStoryDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var textOverlay by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Normal") }
    val filters = listOf("Normal", "Sunset Glow", "Lake Star", "Golden hour")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Lembani DobaStory (New Story) ✨",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = SunriseOrange
                )

                OutlinedTextField(
                    value = textOverlay,
                    onValueChange = { textOverlay = it },
                    placeholder = { Text("Enter text to display on your story status...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("create_story_textbox")
                )

                Text("Select DobaLens Filter / Tag", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val act = selectedFilter == filter
                        SuggestionChip(
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (act) SunriseOrange else Color.Transparent,
                                labelColor = if (act) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(1.dp, if (act) SunriseOrange else BorderGray)
                        )
                    }
                }

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
                            onSubmit(textOverlay, selectedFilter)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SunriseOrange),
                        enabled = textOverlay.isNotBlank(),
                        modifier = Modifier.testTag("publish_story_submit_btn")
                    ) {
                        Text("Share Story", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

