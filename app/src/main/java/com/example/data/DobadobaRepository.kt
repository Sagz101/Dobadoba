package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DobadobaRepository(private val dao: DobadobaDao) {

    val allPosts: Flow<List<Post>> = dao.getAllPosts()
    val discoverPosts: Flow<List<Post>> = dao.getDiscoverPosts()
    val allStories: Flow<List<Story>> = dao.getAllStories()
    val allMessages: Flow<List<Message>> = dao.getAllMessages()
    val allMarketListings: Flow<List<MarketListing>> = dao.getAllMarketListings()
    val allGroups: Flow<List<Group>> = dao.getAllGroups()
    val allEvents: Flow<List<AppEvent>> = dao.getAllEvents()

    val allFees: Flow<List<FeeEntity>> = dao.getAllFees()
    val allAirtimeBundles: Flow<List<AirtimeBundle>> = dao.getAllAirtimeBundles()
    val allStandingOrders: Flow<List<StandingOrderEntity>> = dao.getAllStandingOrders()
    val allGifts: Flow<List<GiftEntity>> = dao.getAllGifts()
    val allAdCampaigns: Flow<List<AdCampaignEntity>> = dao.getAllAdCampaigns()
    val allChamas: Flow<List<ChamaEntity>> = dao.getAllChamas()
    val allStores: Flow<List<StoreEntity>> = dao.getAllStores()
    val allPolicies: Flow<List<PolicyEntity>> = dao.getAllPolicies()

    suspend fun insertPost(post: Post) = withContext(Dispatchers.IO) {
        dao.insertPost(post)
    }

    suspend fun updatePost(post: Post) = withContext(Dispatchers.IO) {
        dao.updatePost(post)
    }

    suspend fun insertStory(story: Story) = withContext(Dispatchers.IO) {
        dao.insertStory(story)
    }

    suspend fun updateStory(story: Story) = withContext(Dispatchers.IO) {
        dao.updateStory(story)
    }

    suspend fun insertMessage(message: Message) = withContext(Dispatchers.IO) {
        dao.insertMessage(message)
    }

    suspend fun getQueuedMessages(): List<Message> = withContext(Dispatchers.IO) {
        dao.getQueuedMessages()
    }

    suspend fun updateMessage(message: Message) = withContext(Dispatchers.IO) {
        dao.updateMessage(message)
    }

    suspend fun deleteMessageById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteMessageById(id)
    }

    suspend fun insertMarketListing(listing: MarketListing) = withContext(Dispatchers.IO) {
        dao.insertMarketListing(listing)
    }

    suspend fun deleteMarketListing(listing: MarketListing) = withContext(Dispatchers.IO) {
        dao.deleteMarketListing(listing)
    }

    suspend fun insertGroup(group: Group) = withContext(Dispatchers.IO) {
        dao.insertGroup(group)
    }

    suspend fun updateGroup(group: Group) = withContext(Dispatchers.IO) {
        dao.updateGroup(group)
    }

    suspend fun insertEvent(event: AppEvent) = withContext(Dispatchers.IO) {
        dao.insertEvent(event)
    }

    suspend fun updateEvent(event: AppEvent) = withContext(Dispatchers.IO) {
        dao.updateEvent(event)
    }

    suspend fun insertFee(fee: FeeEntity) = withContext(Dispatchers.IO) {
        dao.insertFee(fee)
    }

    suspend fun insertAirtimeBundle(bundle: AirtimeBundle) = withContext(Dispatchers.IO) {
        dao.insertAirtimeBundle(bundle)
    }

    suspend fun insertStandingOrder(order: StandingOrderEntity) = withContext(Dispatchers.IO) {
        dao.insertStandingOrder(order)
    }

    suspend fun updateStandingOrder(order: StandingOrderEntity) = withContext(Dispatchers.IO) {
        dao.updateStandingOrder(order)
    }

    suspend fun deleteStandingOrder(order: StandingOrderEntity) = withContext(Dispatchers.IO) {
        dao.deleteStandingOrder(order)
    }

    suspend fun insertGift(gift: GiftEntity) = withContext(Dispatchers.IO) {
        dao.insertGift(gift)
    }

    suspend fun insertAdCampaign(campaign: AdCampaignEntity) = withContext(Dispatchers.IO) {
        dao.insertAdCampaign(campaign)
    }

    suspend fun updateAdCampaign(campaign: AdCampaignEntity) = withContext(Dispatchers.IO) {
        dao.updateAdCampaign(campaign)
    }

    suspend fun insertChama(chama: ChamaEntity): Long = withContext(Dispatchers.IO) {
        dao.insertChama(chama)
    }

    suspend fun updateChama(chama: ChamaEntity) = withContext(Dispatchers.IO) {
        dao.updateChama(chama)
    }

    fun getMembersForChama(chamaId: Long) = dao.getMembersForChama(chamaId)

    suspend fun insertChamaMember(member: ChamaMemberEntity) = withContext(Dispatchers.IO) {
        dao.insertChamaMember(member)
    }

    fun getStoreForUser(userId: String) = dao.getStoreForUser(userId)

    suspend fun insertStore(store: StoreEntity) = withContext(Dispatchers.IO) {
        dao.insertStore(store)
    }

    suspend fun insertPolicy(policy: PolicyEntity) = withContext(Dispatchers.IO) {
        dao.insertPolicy(policy)
    }

    suspend fun prepopulateInitialData() = withContext(Dispatchers.IO) {
        val posts = dao.getAllPosts().first()
        if (posts.isEmpty()) {
            // Seed Posts (some photos, some reels/videos)
            val initialPosts = listOf(
                Post(
                    username = "Eli Njuchi 🇲🇼",
                    captionEnglish = "Chitseko chatsegula! Lilongwe, the door is open. New album is ready for your ears loop. Let us make history!",
                    captionChichewa = "Chitseko chatsegula! Lilongwe khonde muli ndithu chitseko chili gwaa. Album yatsopano yatha kale. Tiyeni tilembe mbiri!",
                    likesCount = 2840,
                    commentsCount = 412,
                    isLiked = false,
                    isTrending = true,
                    isDiscover = false
                ),
                Post(
                    username = "LakeOfStars 🌅",
                    captionEnglish = "Magical sunset glow over Cape Maclear, Mangochi. Malawi is truly the Warm Heart of Africa. Tag your festival squad!",
                    captionChichewa = "Kuundana kwa dzuwa kokucheza bwinobwino pa Cape Maclear m\'boma la Mangochi. Dziko la Malawi ndiye chamtendere kwambiri. Atagireni anzanu amene mukubwera nawo ku chikondwerero chathu!",
                    likesCount = 1920,
                    commentsCount = 230,
                    isLiked = false,
                    isTrending = true,
                    isDiscover = true
                ),
                Post(
                    username = "BulletsFC_Fans ⚽",
                    captionEnglish = "The atmosphere at Kamuzu Stadium is electric for the Blantyre Derby! Drop your match predictions!",
                    captionChichewa = "Chisangalalo ku Kamuzu Stadium ndiye chirombo pa masewera a lero a Blantyre Derby! itsirani mayeso a lero m’munsichi!",
                    likesCount = 890,
                    commentsCount = 180,
                    isLiked = false,
                    isTrending = false,
                    isDiscover = false
                ),
                Post(
                    username = "Afarimi_Cooperative 🌽",
                    captionEnglish = "Bountiful harvest of Dedza white maize. Supporting irrigation practices and modern farming across Central Region.",
                    captionChichewa = "Chimanga chadzaza dondolo m\'minda yathu ku Dedza sabata ino. Tikukulitsa ulimi wothirira komanso wa makono m\'boma lathuli.",
                    likesCount = 340,
                    commentsCount = 45,
                    isLiked = false,
                    isTrending = false,
                    isDiscover = true
                ),
                // Add two short vertical videos / Reels
                Post(
                    username = "DobaFashionista_MW 👗",
                    captionEnglish = "Styling custom Chitenge fabric outfits for Lilongwe Fashion Week. Rate this look 1 to 10!",
                    captionChichewa = "Kusoka ndi kukonza zovala zamakono za Chitenge pa cikondwerero cha Lilongwe Fashion Week. Tiuzeni, chikuyenda bwanji? Kuchokera pa 1 mpaka 10!",
                    likesCount = 1530,
                    commentsCount = 98,
                    isVideo = true,
                    videoUrl = "https://example.com/videos/shorthack.mp4",
                    isLiked = false,
                    isTrending = true,
                    isDiscover = true
                ),
                Post(
                    username = "MalawianHipHopHub 🎙️",
                    captionEnglish = "Phyzix spitting fire bars back-to-back in the studio. Direct from Blantyre city! Tap sound for trending local beats.",
                    captionChichewa = "Phyzix akugwetsa mavesi otentha kwambiri mu situdiyo yathu ku Blantyre! Dinani batani laphokoso kuti mumve nyimbo zam’derali.",
                    likesCount = 2210,
                    commentsCount = 304,
                    isVideo = true,
                    videoUrl = "https://example.com/videos/bars.mp4",
                    isLiked = false,
                    isTrending = true,
                    isDiscover = true
                ),
                Post(
                    username = "LateNight_Club18 🔞",
                    captionEnglish = "[18+] Exclusive Lilongwe club drinks and late-night dating vibes. Adult party updates!",
                    captionChichewa = "[18+] Zosangalatsa za usiku ku Lilongwe, zakumwa ndi kucheza kwachikulire. Party za anthu akulu okha!",
                    likesCount = 42,
                    commentsCount = 2,
                    isLiked = false,
                    isTrending = false,
                    isDiscover = true
                ),
                Post(
                    username = "SpicyDating_MW 🌶️",
                    captionEnglish = "[18+] Late night speed dating and matchmaking show in Blantyre. Adult commentary.",
                    captionChichewa = "[18+] Masewera azachikondi ndi kufunafuna anzanu usiku boma la Blantyre. Ndewu zamphamvu.",
                    likesCount = 110,
                    commentsCount = 12,
                    isVideo = true,
                    videoUrl = "https://example.com/videos/adult.mp4",
                    isLiked = false,
                    isTrending = false,
                    isDiscover = true
                )
            )
            initialPosts.forEach { dao.insertPost(it) }

            // Seed Stories
            val initialStories = listOf(
                Story(
                    username = "Zanga_B",
                    imageUrl = "zanga_market",
                    textOverlay = "Lizulu Irish Potatoes!",
                    locationTag = "Dedza Boarder",
                    isCloseFriends = false,
                    isPoll = true,
                    pollQuestion = "Kodi mwayamba kudya mbatata ya Dedza?",
                    pollVotesA = 48,
                    pollVotesB = 12
                ),
                Story(
                    username = "Mawu_Arts",
                    imageUrl = "mawu_sunrise",
                    textOverlay = "Blantyre Central",
                    locationTag = "Comesa Hall, Blantyre",
                    isCloseFriends = true,
                    isPoll = false
                ),
                Story(
                    username = "KuluFood",
                    imageUrl = "kulu_chambo",
                    textOverlay = "Chambo chotentha & Sima!",
                    locationTag = "Mangochi Waterfront",
                    isCloseFriends = false,
                    isPoll = true,
                    pollQuestion = "Best fish is Chambo. Agree?",
                    pollOptionA = "Eya, ndithu (Yes)",
                    pollOptionB = "Ayi, Kampango (No)",
                    pollVotesA = 120,
                    pollVotesB = 35
                )
            )
            initialStories.forEach { dao.insertStory(it) }

            // Seed Market Listings
            val initialListings = listOf(
                MarketListing(
                    title = "Fresh Lake Malawi Chambo (Pack of 5)",
                    description = "Freshly caught Chambo direct from Mangochi shores, salted and kept under ice. Super sweet, ideal for direct eating with sima.",
                    priceMwk = 15000.0,
                    sellerName = "Bambo Phiri",
                    sellerPhone = "+265 888 123 456",
                    location = "Mangochi Waterfront",
                    mobileMoneyType = "Both"
                ),
                MarketListing(
                    title = "Dedza Irish Potatoes (50kg Bag)",
                    description = "Fresh harvest of premium Irish potatoes directly from Dedza farms. Large, clean tubers, perfect for chips or home consumption.",
                    priceMwk = 24000.0,
                    sellerName = "Mai Chisale",
                    sellerPhone = "+265 999 765 432",
                    location = "Dedza Central Market",
                    mobileMoneyType = "TNM Mpamba"
                ),
                MarketListing(
                    title = "Handmade Premium Chitenge Handbags",
                    description = "Vibrant, durable, double-stitched cotton Chitenge handbags locally crafted by young women tailoring group in Lilongwe.",
                    priceMwk = 8500.0,
                    sellerName = "Chesa Crafts",
                    sellerPhone = "+265 881 999 888",
                    location = "Lilongwe Craft Market",
                    mobileMoneyType = "Airtel Money"
                ),
                MarketListing(
                    title = "Original Eli Njuchi Branded Black Hoodie",
                    description = "Limited edition official Eli Njuchi concert hoodie. Soft cotton material, bold 'Galu Wamkota' graphic print.",
                    priceMwk = 18000.0,
                    sellerName = "Doba Merch Store",
                    sellerPhone = "+265 994 333 111",
                    location = "Limbe, Blantyre",
                    mobileMoneyType = "Both"
                )
            )
            initialListings.forEach { dao.insertMarketListing(it) }

            // Seed Groups
            val initialGroups = listOf(
                Group(
                    name = "Zitukuko za Alimi MW",
                    description = "A community for modern farmers in Malawi. We share ideas on fertilizer usage, seed varieties, irrigation, and finding the best markets.",
                    category = "Farming",
                    membersCount = 4850,
                    isJoined = true,
                    pinnedPost = "⚠️ CHIZIDZO: Mbewu zatsopano zofika ku Lilongwe Agri-Hub zili ndi mafotokozedwe atsopano pa kadyetsedwe. Onetsetsani kuti mukuwerenga."
                ),
                Group(
                    name = "Nyimbo Zachimalawi",
                    description = "Connecting Malawian artists, producers, and passionate music fans. Stay tuned for album drops, live concerts, and exclusive local artist interviews.",
                    category = "Music",
                    membersCount = 12400,
                    isJoined = false,
                    pinnedPost = "🎵 ELI NJUCHI drops album tomorrow! Let's streaming and sharing local Malawian talent world wide!"
                ),
                Group(
                    name = "Blantyre Esports & Soccer Circle",
                    description = "Passionate discussions regarding football, esports tournaments, and international matches in Southern Region.",
                    category = "Sports",
                    membersCount = 3200,
                    isJoined = false,
                    pinnedPost = "⚽ M’munda lero: Big Bullets vs Mighty Wanderers! Match starts at 2:30 PM. Spot the live thread!"
                )
            )
            initialGroups.forEach { dao.insertGroup(it) }

            // Seed Events
            val initialEvents = listOf(
                AppEvent(
                    title = "Lake of Stars Arts Festival",
                    description = "The world-famous Lake of Stars returns to the white sands of Lake Malawi. Enjoy three days of incredible music, dance, visual arts, and theatre under the warm stars.",
                    dateString = "September 25-27, 2026",
                    location = "Cape Maclear Sandy Shores, Mangochi",
                    organizer = "Lake of Stars Project Team",
                    rsvpCount = 1420,
                    isRsvped = true
                ),
                AppEvent(
                    title = "Blantyre Hip Hop Extravaganza",
                    description = "Join Malawi's top lyrical generals live in concert. Expect back-to-back premium performances, fresh sound production, and local street art exhibition.",
                    dateString = "June 12, 2026",
                    location = "Comesa Hall, Blantyre",
                    organizer = "Mawu Promotion",
                    rsvpCount = 850,
                    isRsvped = false
                ),
                AppEvent(
                    title = "Lilongwe Agri-Tech Fair",
                    description = "The ultimate networking hub for Malawian agriculture. Discover modern solar irrigations, digital soil monitors, organic pesticides, and find buyers from Lilongwe and beyond.",
                    dateString = "July 04, 2026",
                    location = "Civo Stadium Outer Grounds, Lilongwe",
                    organizer = "Ministry of Agriculture & TechHub",
                    rsvpCount = 380,
                    isRsvped = false
                )
            )
            initialEvents.forEach { dao.insertEvent(it) }

            // Seed Messages
            val initialMessages = listOf(
                Message(
                    sender = "Chikondi (Lilongwe)",
                    recipient = "Chonde Group",
                    text = "Moni nonse! Kodi titakumana kuti kukambirana za phwando la mbewu mdziko lathu?"
                ),
                Message(
                    sender = "Limbani (Blantyre)",
                    recipient = "Chonde Group",
                    text = "Bwinobwino thupi ndikuti tikumane pa Gateway Mall, m’malo a khofi m’munsichi sabata ino pa 2:00 PM."
                ),
                Message(
                    sender = "Thoko (Zomba)",
                    recipient = "Chonde Group",
                    text = "Mwamva ndithu, ine ndipezekapo ndithu ndi mapulani anga olimbikitsa alimi anyamata!"
                ),
                Message(
                    sender = "Chimwemwe (Friend)",
                    recipient = "Ine (Me)",
                    text = "Zikomo for the Chitenge design ideas! Let me know if you are coming to Lilongwe."
                ),
                Message(
                    sender = "Strange_Sales_MW (Non-Friend)",
                    recipient = "Ine (Me)",
                    text = "[Promotional / Unverified] Get cash loans in Blantyre instantly! 50% interest rate per week. Send phone copy to activate."
                ),
                Message(
                    sender = "DatingPlus_Agent (Non-Friend)",
                    recipient = "Ine (Me)",
                    text = "[Matchmaker] Lonely singles in Lilongwe are waiting! Chat with them now."
                )
            )
            initialMessages.forEach { dao.insertMessage(it) }

            // Pre-seed typical Telco Airtime and Data Bundles for Malawi (Airtel + TNM Mpamba)
            val initialBundles = listOf(
                AirtimeBundle(telco = "Airtel", title = "500MB Social Bundle", priceMwk = 500.0, isData = true),
                AirtimeBundle(telco = "Airtel", title = "2GB Daily Max", priceMwk = 1200.0, isData = true),
                AirtimeBundle(telco = "Airtel", title = "10GB Weekly Ultimate", priceMwk = 5000.0, isData = true),
                AirtimeBundle(telco = "Airtel", title = "MK 1,000 Airtime Promo", priceMwk = 1000.0, isData = false),
                AirtimeBundle(telco = "TNM", title = "Monthly Facebook Pass", priceMwk = 800.0, isData = true),
                AirtimeBundle(telco = "TNM", title = "4GB Tik Tok Pass", priceMwk = 2000.0, isData = true),
                AirtimeBundle(telco = "TNM", title = "25GB Monthly Ultimate", priceMwk = 15000.0, isData = true),
                AirtimeBundle(telco = "TNM", title = "MK 500 Airtime Promo", priceMwk = 500.0, isData = false)
            )
            initialBundles.forEach { dao.insertAirtimeBundle(it) }
        }
    }
}
