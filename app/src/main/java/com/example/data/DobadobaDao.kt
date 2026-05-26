package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DobadobaDao {

    // --- Posts ---
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE isDiscover = 1 ORDER BY likesCount DESC, timestamp DESC")
    fun getDiscoverPosts(): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Update
    suspend fun updatePost(post: Post)

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()

    // --- Stories ---
    @Query("SELECT * FROM stories ORDER BY timestamp DESC")
    fun getAllStories(): Flow<List<Story>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: Story)

    @Update
    suspend fun updateStory(story: Story)

    // --- Messages ---
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE status = 'QUEUED' ORDER BY timestamp ASC")
    suspend fun getQueuedMessages(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessageById(id: Long)

    // --- Market Listings ---
    @Query("SELECT * FROM market_listings ORDER BY timestamp DESC")
    fun getAllMarketListings(): Flow<List<MarketListing>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketListing(listing: MarketListing)

    @Delete
    suspend fun deleteMarketListing(listing: MarketListing)

    // --- Groups ---
    @Query("SELECT * FROM groups ORDER BY membersCount DESC")
    fun getAllGroups(): Flow<List<Group>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Update
    suspend fun updateGroup(group: Group)

    // --- Events ---
    @Query("SELECT * FROM events ORDER BY timestamp ASC")
    fun getAllEvents(): Flow<List<AppEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: AppEvent)

    @Update
    suspend fun updateEvent(event: AppEvent)

    // --- Travel Bookings ---
    @Query("SELECT * FROM travel_bookings ORDER BY timestamp DESC")
    fun getAllTravelBookings(): Flow<List<TravelBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelBooking(booking: TravelBookingEntity)

    @Query("DELETE FROM travel_bookings")
    suspend fun clearTravelBookings()

    // --- Fees ---
    @Query("SELECT * FROM fees ORDER BY timestamp DESC")
    fun getAllFees(): Flow<List<FeeEntity>>

    @Query("SELECT * FROM fees ORDER BY timestamp DESC")
    suspend fun getAllFeesList(): List<FeeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFee(fee: FeeEntity)

    // --- Airtime Bundles ---
    @Query("SELECT * FROM airtime_bundles ORDER BY priceMwk ASC")
    fun getAllAirtimeBundles(): Flow<List<AirtimeBundle>>

    @Query("SELECT * FROM airtime_bundles ORDER BY priceMwk ASC")
    suspend fun getAirtimeBundlesSync(): List<AirtimeBundle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirtimeBundle(bundle: AirtimeBundle)

    // --- Standing Orders ---
    @Query("SELECT * FROM standing_orders ORDER BY timestamp DESC")
    fun getAllStandingOrders(): Flow<List<StandingOrderEntity>>

    @Query("SELECT * FROM standing_orders ORDER BY timestamp DESC")
    suspend fun getAllStandingOrdersList(): List<StandingOrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStandingOrder(order: StandingOrderEntity)

    @Update
    suspend fun updateStandingOrder(order: StandingOrderEntity)

    @Delete
    suspend fun deleteStandingOrder(order: StandingOrderEntity)

    // --- Gifts ---
    @Query("SELECT * FROM gifts ORDER BY timestamp DESC")
    fun getAllGifts(): Flow<List<GiftEntity>>

    @Query("SELECT * FROM gifts ORDER BY timestamp DESC")
    suspend fun getAllGiftsList(): List<GiftEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGift(gift: GiftEntity)

    // --- Ad Campaigns ---
    @Query("SELECT * FROM ad_campaigns ORDER BY timestamp DESC")
    fun getAllAdCampaigns(): Flow<List<AdCampaignEntity>>

    @Query("SELECT * FROM ad_campaigns ORDER BY timestamp DESC")
    suspend fun getAllAdCampaignsList(): List<AdCampaignEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdCampaign(campaign: AdCampaignEntity)

    @Update
    suspend fun updateAdCampaign(campaign: AdCampaignEntity)

    // --- Chamas ---
    @Query("SELECT * FROM chamas ORDER BY timestamp DESC")
    fun getAllChamas(): Flow<List<ChamaEntity>>

    @Query("SELECT * FROM chamas ORDER BY timestamp DESC")
    suspend fun getAllChamasList(): List<ChamaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChama(chama: ChamaEntity): Long

    @Update
    suspend fun updateChama(chama: ChamaEntity)

    // --- Chama Members ---
    @Query("SELECT * FROM chama_members WHERE chamaId = :chamaId")
    fun getMembersForChama(chamaId: Long): Flow<List<ChamaMemberEntity>>

    @Query("SELECT * FROM chama_members WHERE chamaId = :chamaId")
    suspend fun getMembersForChamaList(chamaId: Long): List<ChamaMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChamaMember(member: ChamaMemberEntity)

    // --- Stores ---
    @Query("SELECT * FROM stores WHERE userId = :userId LIMIT 1")
    fun getStoreForUser(userId: String): Flow<StoreEntity?>

    @Query("SELECT * FROM stores WHERE userId = :userId LIMIT 1")
    suspend fun getStoreForUserSync(userId: String): StoreEntity?

    @Query("SELECT * FROM stores ORDER BY timestamp DESC")
    fun getAllStores(): Flow<List<StoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: StoreEntity)

    // --- Policies (Insurance) ---
    @Query("SELECT * FROM policies ORDER BY timestamp DESC")
    fun getAllPolicies(): Flow<List<PolicyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPolicy(policy: PolicyEntity)
}
