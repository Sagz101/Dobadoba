package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DobadobaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DobadobaDatabase.getDatabase(application)
    val repository = DobadobaRepository(database.dobadobaDao())

    // --- Tab / Navigation State ---
    val currentTab = MutableStateFlow("Home") // Home, Reels, Market, Chat, Hub

    // --- Custom Brand User Profile State ---
    val userDisplayName = MutableStateFlow("Ine (Me) 🇲🇼")
    val userHandle = MutableStateFlow("ine_me")
    val userDistrict = MutableStateFlow("Lilongwe")
    val userDOB = MutableStateFlow("15/05/1998")
    val userGender = MutableStateFlow("Male")
    val userBio = MutableStateFlow("Chitukuko ndi Chikhulupiriro. Building the digital future of Malawi!")
    val userWebsite = MutableStateFlow("https://dobadoba.mw")
    val isBusinessProfile = MutableStateFlow(false)
    val businessCategory = MutableStateFlow("Agriculture & Hub 🌽")
    val businessPhone = MutableStateFlow("+265 888 12 34 56")
    val isDobaVerified = MutableStateFlow(true)
    val userFollowersCount = MutableStateFlow(1240)
    val userFollowingCount = MutableStateFlow(320)
    val userPostsCount = MutableStateFlow(14)
    val userHasActiveStories = MutableStateFlow(true)

    // --- DobaTravel Booking History ---
    val travelBookings = MutableStateFlow(listOf(
        TravelTrip("TX-993", "AXA Bus Services", "Lilongwe", "Blantyre", "2026-05-25", "08:00 AM", "MWK 18,500", "Seat 14A", "QR-AXA-928")
    ))

    // --- DobaGroups list ---
    val userGroupsList = MutableStateFlow(listOf(
        "Farming Solutions MW", "Chitenge Designers", "Zomba Football Club", "Chonde Youth Guild"
    ))

    // --- Onboarding / Phone Sign up ---
    val isOnboarded = MutableStateFlow(false)
    val phoneNumber = MutableStateFlow("")
    val isOtpSent = MutableStateFlow(false)
    val enteredOtp = MutableStateFlow("")
    val interestSelection = MutableStateFlow(setOf<String>())
    val allInterests = listOf("Music 🎵", "Farming 🌽", "Religion ⛪", "Sport ⚽", "Business 💼", "Fashion 👗", "Politics 🗳️")
    val importedContactsCount = MutableStateFlow(0)
    val isImportingContacts = MutableStateFlow(false)

    // --- Language / Chichewa Toggle ---
    val currentLanguage = MutableStateFlow("English") // English or Chichewa

    // --- Reels Video Speed & duets ---
    val reelPlaySpeed = MutableStateFlow(1.0f) // 0.5x, 1x, 2x
    val currentReelIndex = MutableStateFlow(0)
    val isDuetMode = MutableStateFlow(false)

    // --- Messaging Offline State ---
    val isConnected = MutableStateFlow(true) // offline switch to test 2G/3G queued messaging!

    // --- Marketplace state ---
    val searchQuery = MutableStateFlow("")
    val activeListingForPayment = MutableStateFlow<MarketListing?>(null)
    val mobileMoneyPhoneNumber = MutableStateFlow("")
    val mobileMoneyTypeSelected = MutableStateFlow("Airtel Money")
    val isPaymentProcessing = MutableStateFlow(false)
    val isPaymentSuccess = MutableStateFlow(false)

    // --- Create Custom Content State ---
    val newPostText = MutableStateFlow("")
    val isCreatingPost = MutableStateFlow(false)

    // --- New Listing Content State ---
    val newListingTitle = MutableStateFlow("")
    val newListingPrice = MutableStateFlow("")
    val newListingDesc = MutableStateFlow("")
    val newListingLocation = MutableStateFlow("Lilongwe")
    val isCreatingListing = MutableStateFlow(false)

    // --- Live Stream Virtual Gifting ---
    val liveCoinsGiftCount = MutableStateFlow(120) // Simulated coins balance
    val liveActiveGiftSendSuccess = MutableStateFlow<String?>(null)

    // --- Flow Observing Database ---
    val allPosts: StateFlow<List<Post>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val discoverPosts: StateFlow<List<Post>> = repository.discoverPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStories: StateFlow<List<Story>> = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessages: StateFlow<List<Message>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMarketListings: StateFlow<List<MarketListing>> = repository.allMarketListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGroups: StateFlow<List<Group>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<AppEvent>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.prepopulateInitialData()
        }
    }

    // --- Interactions ---

    // Toggle Language
    fun toggleLanguage() {
        currentLanguage.value = if (currentLanguage.value == "English") "Chichewa" else "English"
    }

    // Feed Liked
    fun toggleLikePost(post: Post) {
        viewModelScope.launch {
            val updated = post.copy(
                isLiked = !post.isLiked,
                likesCount = if (post.isLiked) post.likesCount - 1 else post.likesCount + 1
            )
            repository.updatePost(updated)
        }
    }

    // Create New Post
    fun addPost(text: String, isVideo: Boolean = false) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val newPost = Post(
                username = "Ine (Me) 🇲🇼",
                captionEnglish = text,
                captionChichewa = "Mavesi akukhala: $text (Lomasuliridwa)",
                likesCount = 0,
                commentsCount = 0,
                isLiked = false,
                isVideo = isVideo
            )
            repository.insertPost(newPost)
        }
    }

    // Story poll vote
    fun voteStoryPoll(story: Story, isOptionA: Boolean) {
        viewModelScope.launch {
            val updated = story.copy(
                userAnswer = if (isOptionA) 1 else 2,
                pollVotesA = if (isOptionA) story.pollVotesA + 1 else story.pollVotesA,
                pollVotesB = if (isOptionA) story.pollVotesB else story.pollVotesB + 1
            )
            repository.updateStory(updated)
        }
    }

    // Capture filter story function for DobaLens
    fun addStory(text: String, filter: String) {
        viewModelScope.launch {
            val newStory = Story(
                username = "Ine (Me) 🇲🇼",
                imageUrl = "camera_captured",
                textOverlay = text,
                locationTag = "Lilongwe, MW ($filter)"
            )
            repository.insertStory(newStory)
        }
    }

    // Onboarding interest toggle
    fun toggleInterest(interest: String) {
        val currentSet = interestSelection.value.toMutableSet()
        if (currentSet.contains(interest)) {
            currentSet.remove(interest)
        } else {
            currentSet.add(interest)
        }
        interestSelection.value = currentSet
    }

    // Onboarding import contacts
    fun simulateImportContacts() {
        viewModelScope.launch {
            isImportingContacts.value = true
            kotlinx.coroutines.delay(1200) // cool loading sensation
            isImportingContacts.value = false
            importedContactsCount.value = 142 // 142 friends on Dobadoba!
        }
    }

    // Send Message
    fun sendChatMessage(text: String, recipient: String = "Chonde Group") {
        if (text.isBlank()) return
        viewModelScope.launch {
            val messageStatus = if (isConnected.value) "SENT" else "QUEUED"
            val newMsg = Message(
                sender = "Ine (Me)",
                recipient = recipient,
                text = text,
                status = messageStatus
            )
            repository.insertMessage(newMsg)
        }
    }

    // Toggle connectivity mode (2G/3G test)
    fun toggleConnectivity() {
        isConnected.value = !isConnected.value
        if (isConnected.value) {
            // Flush queued messages
            viewModelScope.launch {
                val queued = repository.getQueuedMessages()
                for (msg in queued) {
                    val sentMsg = msg.copy(status = "SENT")
                    repository.updateMessage(sentMsg)
                }
            }
        }
    }

    // Buy/Sell Marketplace Add Item
    fun addMarketListing(title: String, desc: String, price: Double, location: String) {
        if (title.isBlank() || price <= 0) return
        viewModelScope.launch {
            val newListing = MarketListing(
                title = title,
                description = desc,
                priceMwk = price,
                sellerName = "Ine (Me)",
                sellerPhone = "+265 888 777 666",
                location = location,
                mobileMoneyType = "Both"
            )
            repository.insertMarketListing(newListing)
        }
    }

    // Initiate Airtel Money / TNM Mpamba buy
    fun startPaymentFlow(listing: MarketListing) {
        activeListingForPayment.value = listing
        isPaymentSuccess.value = false
        isPaymentProcessing.value = false
    }

    fun executeMobileMoneyPayment(enteredNumber: String, pType: String) {
        if (enteredNumber.length < 8) return
        mobileMoneyPhoneNumber.value = enteredNumber
        mobileMoneyTypeSelected.value = pType
        viewModelScope.launch {
            isPaymentProcessing.value = true
            kotlinx.coroutines.delay(1800) // simulated processing delay
            isPaymentProcessing.value = false
            isPaymentSuccess.value = true
            // Also deduct from DobaPay balance if it is paid via wallet
            dobaPayBalance.value = (dobaPayBalance.value - (activeListingForPayment.value?.priceMwk ?: 0.0)).coerceAtLeast(0.0)
            addDobaTransaction("Market Payment: ${activeListingForPayment.value?.title ?: "Item"}", -(activeListingForPayment.value?.priceMwk ?: 0.0))
        }
    }

    // Live virtual gifting using DobaCoins
    fun sendLiveGift(giftName: String, coinsCost: Int) {
        if (liveCoinsGiftCount.value >= coinsCost) {
            liveCoinsGiftCount.value -= coinsCost
            viewModelScope.launch {
                liveActiveGiftSendSuccess.value = "You gifted a $giftName for $coinsCost DobaCoins! 🎁"
                kotlinx.coroutines.delay(2000)
                liveActiveGiftSendSuccess.value = null
            }
        }
    }

    // Groups Join Toggle
    fun toggleJoinGroup(group: Group) {
        viewModelScope.launch {
            val updated = group.copy(
                isJoined = !group.isJoined,
                membersCount = if (group.isJoined) group.membersCount - 1 else group.membersCount + 1
            )
            repository.updateGroup(updated)
        }
    }

    // Event RSVP Toggle
    fun toggleRSVPEvent(event: AppEvent) {
        viewModelScope.launch {
            val updated = event.copy(
                isRsvped = !event.isRsvped,
                rsvpCount = if (event.isRsvped) event.rsvpCount - 1 else event.rsvpCount + 1
            )
            repository.updateEvent(updated)
        }
    }

    // ==========================================
    // PHASE 2 STATE VARIABLES & METHODS
    // ==========================================

    // PHASE 2 STATE VARIABLES & METHODS
    // ==========================================

    // --- DobaPay ---
    val dobaPayBalance = MutableStateFlow(38500.0) // MWK
    val dobaCoinsBalance = MutableStateFlow(450) // DobaCoins reward currency
    val dobaPayDailyLimit = MutableStateFlow(50000.0) // configurable limit
    val dobaPayPin = MutableStateFlow("1234")
    val dobaPayTransactions = MutableStateFlow(listOf(
        DobaTransaction("Txn-122", "Airtel Money Cash-In", 20000.0, true, "2026-05-20"),
        DobaTransaction("Txn-121", "TNM Mpamba Top-up", 15000.0, true, "2026-05-19"),
        DobaTransaction("Txn-120", "Split: Chonde Group Dinner", -7500.0, false, "2026-05-18"),
        DobaTransaction("Txn-119", "Creator tip: Kondwani Music", -2000.0, false, "2026-05-16")
    ))

    // --- Monetisation State Flows (Room) ---
    val allFeesList = repository.allFees.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val allAirtimeBundlesFlow = repository.allAirtimeBundles.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val allStandingOrdersFlow = repository.allStandingOrders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val allGiftsFlow = repository.allGifts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val allAdCampaignsFlow = repository.allAdCampaigns.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val allChamasFlow = repository.allChamas.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val allStoresFlow = repository.allStores.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val allPoliciesFlow = repository.allPolicies.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Sprint state fields
    val transferAmountInput = MutableStateFlow("")
    val calculatedTransferFeeMwk = MutableStateFlow(0.0)
    val calculatedTransferTotalMwk = MutableStateFlow(0.0)
    val displayTransferSummary = MutableStateFlow("Enter amount to view calculated transfer summary under 1% fee model")

    fun updateTransferAmountInput(text: String) {
        transferAmountInput.value = text
        val parsedAmt = text.toDoubleOrNull() ?: 0.0
        if (parsedAmt <= 0.0) {
            calculatedTransferFeeMwk.value = 0.0
            calculatedTransferTotalMwk.value = 0.0
            displayTransferSummary.value = "Enter amount to view calculated transfer summary under 1% fee model"
        } else {
            val fee = com.example.util.FeeCalculator.calculateFee(parsedAmt)
            calculatedTransferFeeMwk.value = fee
            calculatedTransferTotalMwk.value = parsedAmt + fee
            displayTransferSummary.value = com.example.util.FeeCalculator.formatTransferSummary(parsedAmt)
        }
    }

    // Interactive Airtime / Data Purchases Flow
    val selectedAirtimeBundle = MutableStateFlow<AirtimeBundle?>(null)
    val activeResellerPurchasePin = MutableStateFlow("")
    val lastToppedUpText = MutableStateFlow("No recent airtime purchases found")

    fun executeAirtimeBundlePurchase(bundle: AirtimeBundle, pinEntered: String): Boolean {
        if (pinEntered != dobaPayPin.value) return false
        if (dobaPayBalance.value < bundle.priceMwk) return false
        
        viewModelScope.launch {
            dobaPayBalance.value -= bundle.priceMwk
            val marginAmt = bundle.priceMwk * 0.02
            repository.insertFee(FeeEntity(feeAmountMwk = marginAmt, associatedTransferId = "AIR-${bundle.id}-${System.currentTimeMillis()}", timestamp = System.currentTimeMillis()))
            repository.insertGift(GiftEntity(senderUsername = "Telecom-Reseller", giftName = "Resold: ${bundle.title}", giftCoinsValue = 0, timestamp = System.currentTimeMillis()))
            addDobaTransaction("Airtime/Data Top-up: ${bundle.title}", -bundle.priceMwk)
            lastToppedUpText.value = "Last Buy: ${bundle.telco} - ${bundle.title} (${com.example.util.FeeCalculator.formatMwk(bundle.priceMwk)})"
        }
        return true
    }

    // Recurring Standing Orders & Subscriptions Flow
    fun handleCreateStandingOrder(recipient: String, amount: Double, frequency: String, startDate: String, description: String, isSubscription: Boolean = false) {
        viewModelScope.launch {
            val order = StandingOrderEntity(
                recipientPhone = recipient,
                amountMwk = amount,
                frequency = frequency,
                startDate = startDate,
                description = description,
                isPaused = false,
                isSubscription = isSubscription,
                timestamp = System.currentTimeMillis()
            )
            repository.insertStandingOrder(order)
        }
    }

    fun handleTogglePauseStandingOrder(order: StandingOrderEntity) {
        viewModelScope.launch {
            repository.updateStandingOrder(order.copy(isPaused = !order.isPaused))
        }
    }

    fun handleDeleteStandingOrder(order: StandingOrderEntity) {
        viewModelScope.launch {
            repository.deleteStandingOrder(order)
        }
    }

    // DobaCoins top-up purchases with dynamic confetti
    val showConfettiAnimation = MutableStateFlow(false)

    fun handlePurchaseDobaCoins(priceMwk: Double, coinVolume: Int) {
        if (dobaPayBalance.value < priceMwk) return
        dobaPayBalance.value -= priceMwk
        dobaCoinsBalance.value += coinVolume
        addDobaTransaction("Bought ${coinVolume} DobaCoins Pack", -priceMwk)
        
        viewModelScope.launch {
            showConfettiAnimation.value = true
            kotlinx.coroutines.delay(2500)
            showConfettiAnimation.value = false
        }
    }

    // Live virtual gifts with 70/30 share split
    val activeLiveGiftOverlayMsg = MutableStateFlow<String?>(null)
    val currentBroadcasterSessionEarnings = MutableStateFlow(0.0) // in MWK

    fun handleSendLiveGift(giftName: String, coinValue: Int, broadcasterUsername: String) {
        if (dobaCoinsBalance.value < coinValue) return
        dobaCoinsBalance.value -= coinValue
        
        viewModelScope.launch {
            repository.insertGift(GiftEntity(senderUsername = userHandle.value, giftName = giftName, giftCoinsValue = coinValue, timestamp = System.currentTimeMillis()))
            
            // 1 coin = 10 MWK conversion standard for payouts
            val mwkValue = coinValue * 10.0
            val broadcasterShare = mwkValue * 0.70
            val platformShare = mwkValue * 0.30
            
            // Log 30% fee
            repository.insertFee(FeeEntity(feeAmountMwk = platformShare, associatedTransferId = "LF-GIFT-${giftName}", timestamp = System.currentTimeMillis()))
            currentBroadcasterSessionEarnings.value += broadcasterShare
            
            activeLiveGiftOverlayMsg.value = "🎁 ${userDisplayName.value} gifted $giftName! ($coinValue Coins!)"
            kotlinx.coroutines.delay(3500)
            activeLiveGiftOverlayMsg.value = null
        }
    }

    // DobaChama savings group flow
    fun handleCreateChamaGroup(name: String, description: String, contribAmt: Double) {
        viewModelScope.launch {
            val newChm = ChamaEntity(
                name = name,
                description = description,
                contributionAmountMwk = contribAmt,
                payoutIndex = 0,
                nextPayoutDate = "2026-06-01",
                monthlyFeeStatus = "Not Paid",
                timestamp = System.currentTimeMillis()
            )
            val newlyCreatedId = repository.insertChama(newChm)
            
            // Insert admin creator as member
            repository.insertChamaMember(
                ChamaMemberEntity(
                    chamaId = newlyCreatedId,
                    userId = "me",
                    joinedAt = System.currentTimeMillis(),
                    payoutPosition = 1
                )
            )
        }
    }

    fun handleJoinChamaGroup(chama: ChamaEntity) {
        viewModelScope.launch {
            // Join member
            repository.insertChamaMember(
                ChamaMemberEntity(
                    chamaId = chama.id,
                    userId = "me",
                    joinedAt = System.currentTimeMillis(),
                    payoutPosition = 2
                )
            )
        }
    }

    fun handleContributeToChama(chama: ChamaEntity) {
        val totalDeduction = chama.contributionAmountMwk
        if (dobaPayBalance.value < totalDeduction) return
        dobaPayBalance.value -= totalDeduction
        addDobaTransaction("Chama Contribution: ${chama.name}", -totalDeduction)
    }

    // Seller Spotlight Boost listing flow
    fun handleBoostMarketListing(listing: MarketListing) {
        val expense = 2000.0 // MK 2000 for 7 days boost
        if (dobaPayBalance.value < expense) return
        
        dobaPayBalance.value -= expense
        addDobaTransaction("Boost Spot: ${listing.title}", -expense)
        
        viewModelScope.launch {
            // Record 100% boost fees
            repository.insertFee(FeeEntity(feeAmountMwk = expense, associatedTransferId = "BOOST-${listing.id}", timestamp = System.currentTimeMillis()))
            // Update listing's boosted target to 7 days from now
            val updated = listing.copy(boostedUntil = System.currentTimeMillis() + 7 * 24 * 3600 * 1000)
            repository.insertMarketListing(updated) // updates or inserts with matched ID due to upsert
        }
    }

    // DobaAds Promotion Campaign
    fun handlePromoteAdCampaign(listing: MarketListing, budget: Double, districts: List<String>, days: Int) {
        val expense = budget * days
        if (dobaPayBalance.value < expense) return
        
        dobaPayBalance.value -= expense
        addDobaTransaction("Ad Promoted Campaign: ${listing.title}", -expense)
        
        viewModelScope.launch {
            // Record 3% processing fee
            val processingFee = expense * 0.03
            repository.insertFee(FeeEntity(feeAmountMwk = processingFee, associatedTransferId = "AD-PROCESS-${listing.id}", timestamp = System.currentTimeMillis()))
            
            val camp = AdCampaignEntity(
                postId = listing.id,
                targetDistricts = districts.joinToString(", "),
                dailyBudgetMwk = budget,
                startDate = "2026-05-24",
                endDate = "2026-05-31",
                impressions = 0,
                isPaused = false,
                timestamp = System.currentTimeMillis()
            )
            repository.insertAdCampaign(camp)
        }
    }

    // Gold Seller custom store link setup
    val activeUserStore = MutableStateFlow<StoreEntity?>(null)

    fun handleSetupGoldStore(name: String, bio: String, category: String, banner: String, subdomain: String) {
        viewModelScope.launch {
            val s = StoreEntity(
                userId = "me",
                name = name,
                bannerImage = banner,
                bio = bio,
                category = category,
                subdobaLinkUrl = "https://dobadoba.mw/store/$subdomain",
                timestamp = System.currentTimeMillis()
            )
            repository.insertStore(s)
            activeUserStore.value = s
        }
    }

    // Escrow management
    val deliveryEscrowLogs = MutableStateFlow(listOf(
        "Escrow: Held MK 4,500 for Agricultural delivery DEL-73",
        "Escrow: Released MK 1,500 to rider Mayeso Express"
    ))

    fun releaseEscrowForDelivery(delivery: DobaDelivery) {
        viewModelScope.launch {
            // Take 1.5% marketplace fee
            val marketFee = delivery.deliveryFeeMwk * 0.015
            repository.insertFee(FeeEntity(feeAmountMwk = marketFee, associatedTransferId = "ESCROW-FEE-${delivery.id}", timestamp = System.currentTimeMillis()))
            
            val log = deliveryEscrowLogs.value.toMutableList()
            log.add(0, "Escrow Released: Credited Rider ${delivery.assignedRider} MK ${delivery.deliveryFeeMwk - marketFee} (Deducted 1.5% fee)")
            deliveryEscrowLogs.value = log
        }
    }

    // DobaCover Life & Crop Insurance policies
    fun handlePurchaseInsurancePolicy(name: String, nrc: String, beneficiary: String, type: String, premium: Double): Boolean {
        if (dobaPayBalance.value < premium) return false
        dobaPayBalance.value -= premium
        addDobaTransaction("DobaCover Premium: $type", -premium)
        
        viewModelScope.launch {
            // Record premium transaction
            val p = PolicyEntity(
                fullName = name,
                nrcNumber = nrc,
                beneficiaryPhone = beneficiary,
                productType = type,
                premiumMwk = premium,
                pdfString = "POLICY-REV-NUM-${(1000..9999).random()}",
                timestamp = System.currentTimeMillis()
            )
            repository.insertPolicy(p)
        }
        return true
    }

    fun addDobaTransaction(desc: String, amount: Double) {
        val list = dobaPayTransactions.value.toMutableList()
        val formattedId = "Txn-${(100..999).random()}"
        list.add(0, DobaTransaction(formattedId, desc, amount, amount > 0, "Today"))
        dobaPayTransactions.value = list
    }

    fun executeSendMoney(phone: String, amount: Double, pType: String, pin: String): Boolean {
        if (amount <= 0 || amount > dobaPayDailyLimit.value || pin != dobaPayPin.value) return false
        if (dobaPayBalance.value < amount) return false
        dobaPayBalance.value -= amount
        addDobaTransaction("Send to $phone via $pType", -amount)
        return true
    }

    fun executeGiftCoins(creator: String, coins: Int): Boolean {
        if (dobaCoinsBalance.value < coins) return false
        dobaCoinsBalance.value -= coins
        viewModelScope.launch {
            liveActiveGiftSendSuccess.value = "Tipped $creator $coins DobaCoins! 💎"
            kotlinx.coroutines.delay(2000)
            liveActiveGiftSendSuccess.value = null
        }
        return true
    }

    fun awardDobaCoins(coins: Int) {
        dobaCoinsBalance.value += coins
    }

    // --- DobaDeliver ---
    val deliveryRadiusKm = MutableStateFlow(25) // km radius
    val deliveryFeeMwk = MutableStateFlow(1800.0) // delivery fee in MWK
    val activeDeliveries = MutableStateFlow(listOf(
        DobaDelivery("DEL-73", "Surgical Crop Fertilizer", "Lilongwe Area 43", "Rider Matched", 1800.0, "Limbani Rider", "0888930214", System.currentTimeMillis()),
        DobaDelivery("DEL-72", "Malawi Chitenje Dress", "Blantyre Limbe", "Escrow Held", 1500.0, "Mayeso Express", "0999580421", System.currentTimeMillis() - 40000)
    ))

    fun confirmDeliveryReceived(deliveryId: String) {
        val list = activeDeliveries.value.map {
            if (it.id == deliveryId) {
                it.copy(speedStatus = "Delivered & Escrow Released")
            } else it
        }
        activeDeliveries.value = list
    }

    // --- DobaGames ---
    val currentTriviaIndex = MutableStateFlow(0)
    val triviaScore = MutableStateFlow(0)
    val triviaQuizList = listOf(
        TriviaItem(
            "Which team won the TNM Super League in 2024?",
            listOf("Silver Strikers", "Nyasa Big Bullets", "Mighty Mukuru Wanderers", "Chitipa United"),
            0,
            "Silver Strikers ended Nyasa Big Bullets' dominance with an outstanding campaign in 2024!"
        ),
        TriviaItem(
            "What is the Chichewa name for Maize?",
            listOf("Mbambaira", "Mapira", "Chimanga", "Mtedza"),
            2,
            "Chimanga is the absolute staple crop and backbone of nutrition across Malawi!"
        ),
        TriviaItem(
            "Where is the magnificent Mount Mulanje situated in Malawi?",
            listOf("Northern Region", "Central Region", "Southern Region", "Zanzibar Route"),
            2,
            "Mount Mulanje (Sapitwa Peak) is located in the beautiful Southern Region of Malawi."
        )
    )

    val wordPuzzleUnscrambled = MutableStateFlow("")
    val activePuzzleWord = WordPuzzleItem("MOBZBA", "ZOMBA", "One of Malawi's beautiful historic cities, famous for its grand Plateau!")

    val pictureQuizGuess = MutableStateFlow("")
    val activePictureQuiz = PictureQuizItem("https://images.unsplash.com/photo", "LAKE MALAWI", "The clean internal body of fresh water in Malawi, also known as the Calendar Lake!")

    // --- DobaKaraoke ---
    val karaokeTracks = listOf(
        KaraokeTrack("Kondwani - Bwanji Bwanji", "4:32", "Blues"),
        KaraokeTrack("Eli Njuchi - Gugugu", "3:15", "Afropop"),
        KaraokeTrack("Phyzix - Makofi (Malawi Rap)", "3:50", "Hip Hop"),
        KaraokeTrack("Giddes Chalamanda - Linny Hoo", "5:12", "Heritage Acoustic")
    )
    val selectedKaraokeTrack = MutableStateFlow(karaokeTracks[0])
    val isRecordingKaraoke = MutableStateFlow(false)
    val recordingProgress = MutableStateFlow(0.0f)
    val savedSingingVideos = MutableStateFlow(listOf(
        SingingVideo("Tionge Melody", "Eli Njuchi - Gugugu", 340, "🔥❤️🙌"),
        SingingVideo("Limbani Beats", "Giddes Chalamanda - Linny Hoo", 215, "💎🌟👏")
    ))

    // --- Predict & Win ---
    val currentPredictions = MutableStateFlow(listOf(
        FixturePrediction("FIX-09", "Silver Strikers vs Nyasa Big Bullets", "2026-05-23", "TNM Super League", "Not Predicted"),
        FixturePrediction("FIX-08", "Mighty Wanderers vs Red Lions FC", "2026-05-24", "TNM Super League", "Not Predicted"),
        FixturePrediction("FIX-07", "Malawi Flames vs Senegal", "2026-06-02", "AFCON Qualifier", "Not Predicted")
    ))

    fun submitFixturePrediction(fixtureId: String, prediction: String) {
        val list = currentPredictions.value.map {
            if (it.id == fixtureId) {
                it.copy(prediction = prediction)
            } else it
        }
        currentPredictions.value = list
        awardDobaCoins(15) // earn social rewards for participating!
    }

    // --- DobaRadio ---
    val liveRadioRooms = MutableStateFlow(listOf(
        RadioRoom("ROOM-01", "Farming & Charcoal Solutions Live", "Host: Dr. Phiri 🌽", 380, true),
        RadioRoom("ROOM-02", "Lilongwe Beats & Heritage Music Showcase", "Host: DJ Gogo 🎙️", 195, false),
        RadioRoom("ROOM-03", "Zathu - Family & Health Discussion", "Host: Chiko & Tionge", 124, false)
    ))
    val isRadioHostStage = MutableStateFlow(false)
    val isHandRaised = MutableStateFlow(false)

    // --- DobaNeighbour ---
    val defaultDistrictListText = listOf("Lilongwe Area 18", "Ndirande Blantyre", "Zomba Central", "Mzuzu Masasa", "Salima Bay")
    val userSelectedDistrict = MutableStateFlow("Lilongwe Area 18")
    val neighbourRadiusKm = MutableStateFlow(10.0f)
    val neighbourPosts = MutableStateFlow(listOf(
        NeighbourPost("NEI-92", "Community Notice", "Lilongwe Area 18", "Clean-up campaign this Saturday morning starting from the Area 18 clinic. Please bring local tools!", "DoDMA Verified Alert", System.currentTimeMillis() - 2 * 3600 * 1000),
        NeighbourPost("NEI-91", "Water Interruptions", "Lilongwe Area 18", "Maintenance works by Lilongwe Water Board tomorrow from 8:00 AM to 5:00 PM. Storage suggested.", "LWB Official", System.currentTimeMillis() - 4 * 3600 * 1000),
        NeighbourPost("NEI-90", "Lost & Found", "Lilongwe Area 18", "Found a brown leather wallet with keys at the shops yesterday. Contact Area 18 police desk with matching ID.", "Limbani Tembo", System.currentTimeMillis() - 10 * 3600 * 1000)
    ))

    fun addNeighbourPost(title: String, body: String, district: String) {
        if (title.isBlank() || body.isBlank()) return
        val list = neighbourPosts.value.toMutableList()
        list.add(0, NeighbourPost("NEI-${(100..999).random()}", title, district, body, "Ine (Me) 🇲🇼", System.currentTimeMillis()))
        neighbourPosts.value = list
    }

    // --- Smart Keyboard & Stickers ---
    val sampleChichewaDictionary = listOf(
        SmartPhrase("Muli bwanji?", "How are you?"),
        SmartPhrase("Zikomo kwambiri!", "Thank you very much!"),
        SmartPhrase("Ndili bwino kupita patsogolo.", "I am well moving forward."),
        SmartPhrase("Chonde ndithandizeni.", "Please help me."),
        SmartPhrase("Mwayiwala kalekale?", "Have you forgotten already?"),
        SmartPhrase("Mulungu akudalitseni 🇲🇼", "God bless you.")
    )
    val availableStickers = listOf(
        DobaSticker("STK-01", "Chilungamo chalephera! 🤔", "Justice failed joke"),
        DobaSticker("STK-02", "Doba Spirit! 🔥🇲🇼", "High energy"),
        DobaSticker("STK-03", "Chonde, chonde! 🙏", "Please please"),
        DobaSticker("STK-04", "Pang'ono pang'ono! 🐢", "Slowly slowly"),
        DobaSticker("STK-05", "Nkhuku yoweta satukwana mphungu! 🦅", "Cultural proverb")
    )

    // --- DobaLearn ---
    val educationalTutorials = MutableStateFlow(listOf(
        DobaLearnItem("ED-1", "Hybrid Maize Growth Guide", "Learn how to optimize spacing and fertilization in Lilongwe soils for maximum yield.", "Dr. Chimwemwe (Agriculture Expert)", true),
        DobaLearnItem("ED-2", "Sewing Chic Chitenge Styles", "Step-by-step tutorial on drafting standard traditional patterns.", "Mama Limbikani (Tailoring Mentor)", false),
        DobaLearnItem("ED-3", "Mobile Merchant 101", "Learn how to setup Airtel Money & Mpamba merchant numbers for your shop easily.", "DobaBusiness School", true)
    ))

    fun toggleOfflineDownloadTutorial(id: String) {
        val list = educationalTutorials.value.map {
            if (it.id == id) {
                it.copy(isDownloaded = !it.isDownloaded)
            } else it
        }
        educationalTutorials.value = list
    }

    // --- DobaCause (Crowdfund) ---
    val dobaCauses = MutableStateFlow(listOf(
        DobaCauseItem("CS-01", "Chikwawa District Flood Relief", "Providing instant local food packets and mosquito nets to families displaced by floods.", 1500000.0, 950000.0, "DoDMA Partner Approved"),
        DobaCauseItem("CS-02", "School Fees for Tionge", "Tionge needs to complete her final year in secondary school to become a local teacher.", 250000.0, 180000.0, "Local Ward Counselor verified"),
        DobaCauseItem("CS-03", "Salima Village Solar Borehole", "Drilling a safe solar borehole to supply clean drinking water for over 200 households.", 4500000.0, 850000.0, "Salima NGO Alliance")
    ))
    val dobaCauseLogs = MutableStateFlow(listOf(
        CauseLog("Limbani Bandas tipped K10,000", "CS-01", System.currentTimeMillis() - 600000),
        CauseLog("Anonymous sent K5,000", "CS-02", System.currentTimeMillis() - 1200000),
        CauseLog("Chiko Mphepo tipped K25,000", "CS-03", System.currentTimeMillis() - 2500000)
    ))

    fun donorTipCause(causeId: String, amount: Double, pType: String): Boolean {
        if (amount <= 0 || dobaPayBalance.value < amount) return false
        dobaPayBalance.value -= amount
        addDobaTransaction("Crowdfund Tip for $causeId via $pType", -amount)

        val list = dobaCauses.value.map {
            if (it.id == causeId) {
                it.copy(currentMwk = it.currentMwk + amount)
            } else it
        }
        dobaCauses.value = list

        val logs = dobaCauseLogs.value.toMutableList()
        logs.add(0, CauseLog("Ine (Me) tipped K${String.format("%,.0f", amount)}", causeId, System.currentTimeMillis()))
        dobaCauseLogs.value = logs

        return true
    }

    // --- Global Phase 2 Toggle Controls & Badges (DobaVerify, Ghost Mode, Community Alerts, DobaJunior) ---
    // Coloured verification badge state
    val verificationStatus = MutableStateFlow("Verified Blue") // Unverified, Verified Blue, Verified Gold (Business), Verified Green (NGO)
    val pendingVerificationDoc = MutableStateFlow<String?>(null)

    // Ghost Mode State (Expires in 24 hours simulation)
    val isGhostModeActive = MutableStateFlow(false)
    val ghostModeExpiresCountdown = MutableStateFlow("23:59:52")

    // Community Safety Alerts Banner State
    val activeSafetyAlert = MutableStateFlow<SafetyAlert?>(
        SafetyAlert(
            "DoDMA VERIFIED URGENT ALERT",
            "Tropical low-pressure advisory for Chikwawa and Nsanje. High possibility of flash floods. Residents suggested moving to community camps.",
            System.currentTimeMillis()
        )
    )

    // DobaJunior State (Safe Child Mode behind Parent PIN)
    val isJuniorModeActive = MutableStateFlow(false)
    val juniorModePinEntered = MutableStateFlow("")
    val parentPin = MutableStateFlow("1234")

    // Profile view states for compatibility with ProfileTab
    val userProfileState = MutableStateFlow(UserProfileState(
        name = "Ine (Me) 🇲🇼",
        isVerified = true,
        locationTag = "Lilongwe"
    ))
    val isBiometricsLocked = MutableStateFlow(false)
    val premiumBuySuccess = MutableStateFlow<String?>(null)

    fun toggleBiometricLock(context: android.content.Context) {
        isBiometricsLocked.value = !isBiometricsLocked.value
        val status = if (isBiometricsLocked.value) "Yatsegulidwa (Enabled)" else "Yatsekedwa (Disabled)"
        android.widget.Toast.makeText(context, "Biometrics Lock $status!", android.widget.Toast.LENGTH_SHORT).show()
    }

    fun purchasePremiumAccount(phone: String, operator: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            premiumBuySuccess.value = "K5,000 paid via $operator. Gold verification badge activated!"
        }
    }
}

// ==========================================
// PHASE 2 METADATA ENTITIES
// ==========================================

data class DobaTransaction(
    val id: String,
    val description: String,
    val amountMwk: Double,
    val isCredit: Boolean,
    val dateString: String
)

data class DobaDelivery(
    val id: String,
    val itemName: String,
    val destination: String,
    val speedStatus: String, // Rider Matched, Escrow Held, Dispatched, Delivered & Escrow Released
    val deliveryFeeMwk: Double,
    val assignedRider: String,
    val riderPhone: String,
    val timestamp: Long
)

data class TriviaItem(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val factExplanation: String
)

data class WordPuzzleItem(
    val scrambled: String,
    val actual: String,
    val chichewaClueOnCorrect: String
)

data class PictureQuizItem(
    val placeholderImage: String,
    val correctWord: String,
    val clue: String
)

data class KaraokeTrack(
    val trackTitle: String,
    val duration: String,
    val genre: String
)

data class SingingVideo(
    val singerName: String,
    val backingTrackPlayed: String,
    val friendVoteCount: Int,
    val reactionsText: String
)

data class FixturePrediction(
    val id: String,
    val fixName: String,
    val dateText: String,
    val tourName: String,
    val prediction: String // "Not Predicted", "Home Win", "Draw", "Away Win"
)

data class RadioRoom(
    val id: String,
    val title: String,
    val hostDesc: String,
    val listeners: Int,
    val isLiveNow: Boolean
)

data class NeighbourPost(
    val id: String,
    val title: String,
    val district: String,
    val bodyText: String,
    val publisher: String,
    val timestamp: Long
)

data class SmartPhrase(
    val phraseText: String,
    val translation: String
)

data class DobaSticker(
    val id: String,
    val rawVisualText: String,
    val cleanDesc: String
)

data class DobaLearnItem(
    val id: String,
    val title: String,
    val summary: String,
    val expertCreatorDesc: String,
    val isDownloaded: Boolean
)

data class DobaCauseItem(
    val id: String,
    val title: String,
    val description: String,
    val goalMwk: Double,
    val currentMwk: Double,
    val verifierTeamStub: String
)

data class CauseLog(
    val contributionLine: String,
    val causeId: String,
    val timestamp: Long
)

data class SafetyAlert(
    val headerTitle: String,
    val emergencyBody: String,
    val issuedTs: Long
)

data class TravelTrip(
    val id: String,
    val operatorName: String,
    val origin: String,
    val destination: String,
    val date: String,
    val rTime: String,
    val price: String,
    val seat: String,
    val qrCode: String
)

data class UserProfileState(
    val name: String,
    val isVerified: Boolean,
    val locationTag: String
)


