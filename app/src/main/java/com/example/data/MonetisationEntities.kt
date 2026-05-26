package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fees")
data class FeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val feeAmountMwk: Double,
    val associatedTransferId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "airtime_bundles")
data class AirtimeBundle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val telco: String, // Airtel or TNM
    val title: String, // e.g., "500MB Social Bundle"
    val priceMwk: Double,
    val isData: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "standing_orders")
data class StandingOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientPhone: String,
    val amountMwk: Double,
    val frequency: String, // "Weekly" or "Monthly"
    val startDate: String,
    val description: String = "",
    val isPaused: Boolean = false,
    val isSubscription: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "gifts")
data class GiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderUsername: String,
    val giftName: String,
    val giftCoinsValue: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ad_campaigns")
data class AdCampaignEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val targetDistricts: String, // comma-separated list
    val dailyBudgetMwk: Double,
    val startDate: String,
    val endDate: String,
    val impressions: Int = 0,
    val isPaused: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chamas")
data class ChamaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val contributionAmountMwk: Double,
    val payoutIndex: Int = 0,
    val nextPayoutDate: String = "",
    val monthlyFeeStatus: String = "UNPAID", // UNPAID or PAID
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chama_members")
data class ChamaMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chamaId: Long,
    val userId: String,
    val joinedAt: Long = System.currentTimeMillis(),
    val payoutPosition: Int
)

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val name: String,
    val bannerImage: String,
    val bio: String,
    val category: String,
    val subdobaLinkUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "policies")
data class PolicyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val nrcNumber: String,
    val beneficiaryPhone: String,
    val productType: String, // Phone, Crop, Accident
    val premiumMwk: Double,
    val pdfString: String,
    val timestamp: Long = System.currentTimeMillis()
)
