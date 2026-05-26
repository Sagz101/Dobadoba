package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val userAvatarUrl: String = "", // Placeholders or empty
    val captionEnglish: String,
    val captionChichewa: String,
    val imageUrl: String = "",
    val videoUrl: String = "", // Used if video
    val isVideo: Boolean = false,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLiked: Boolean = false,
    val isTrending: Boolean = false,
    val isDiscover: Boolean = false, // algorithmic feed toggle
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val imageUrl: String,
    val textOverlay: String = "",
    val isVideo: Boolean = false,
    val locationTag: String = "", // e.g. "Lilongwe", "Blantyre"
    val isCloseFriends: Boolean = false,
    val isPoll: Boolean = false,
    val pollQuestion: String = "",
    val pollOptionA: String = "Eya! (Yes)",
    val pollOptionB: String = "Ayi (No)",
    val pollVotesA: Int = 0,
    val pollVotesB: Int = 0,
    val userAnswer: Int = 0, // 0: none, 1: A, 2: B
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val recipient: String = "Chonde Group", // Group or direct
    val text: String = "",
    val isVoiceNote: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "SENT" // SENT, QUEUED, DELIVERED
)

@Entity(tableName = "market_listings")
data class MarketListing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val priceMwk: Double,
    val sellerName: String,
    val sellerPhone: String,
    val location: String, // e.g. "Zomba", "Blantyre Central"
    val imageUrl: String = "",
    val mobileMoneyType: String = "Both", // "Airtel Money", "Mpamba", "Both"
    val timestamp: Long = System.currentTimeMillis(),
    val boostedUntil: Long = 0
)

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val category: String, // "Farming", "Music", "Religion", "Sports" etc.
    val membersCount: Int = 1,
    val isJoined: Boolean = false,
    val pinnedPost: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "events")
data class AppEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val dateString: String,
    val location: String, // e.g., "Silver Stadium, Lilongwe"
    val organizer: String,
    val imageUrl: String = "",
    val rsvpCount: Int = 0,
    val isRsvped: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "travel_bookings")
data class TravelBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val busName: String,
    val departureTime: String,
    val priceMwk: Double,
    val qrCodeBase64: String, // Full Base64-encoded QR string (not URL) for offline rendering
    val timestamp: Long = System.currentTimeMillis()
)
