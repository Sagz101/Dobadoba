package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Post::class,
        Story::class,
        Message::class,
        MarketListing::class,
        Group::class,
        AppEvent::class,
        TravelBookingEntity::class,
        FeeEntity::class,
        AirtimeBundle::class,
        StandingOrderEntity::class,
        GiftEntity::class,
        AdCampaignEntity::class,
        ChamaEntity::class,
        ChamaMemberEntity::class,
        StoreEntity::class,
        PolicyEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class DobadobaDatabase : RoomDatabase() {
    abstract fun dobadobaDao(): DobadobaDao

    companion object {
        @Volatile
        private var INSTANCE: DobadobaDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `travel_bookings` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `busName` TEXT NOT NULL, 
                        `departureTime` TEXT NOT NULL, 
                        `priceMwk` REAL NOT NULL, 
                        `qrCodeBase64` TEXT NOT NULL, 
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `market_listings` ADD COLUMN `boostedUntil` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `fees` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `feeAmountMwk` REAL NOT NULL,
                        `associatedTransferId` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `airtime_bundles` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `telco` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `priceMwk` REAL NOT NULL,
                        `isData` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `standing_orders` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `recipientPhone` TEXT NOT NULL,
                        `amountMwk` REAL NOT NULL,
                        `frequency` TEXT NOT NULL,
                        `startDate` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `isPaused` INTEGER NOT NULL,
                        `isSubscription` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `gifts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `senderUsername` TEXT NOT NULL,
                        `giftName` TEXT NOT NULL,
                        `giftCoinsValue` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `ad_campaigns` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `postId` INTEGER NOT NULL,
                        `targetDistricts` TEXT NOT NULL,
                        `dailyBudgetMwk` REAL NOT NULL,
                        `startDate` TEXT NOT NULL,
                        `endDate` TEXT NOT NULL,
                        `impressions` INTEGER NOT NULL,
                        `isPaused` INTEGER NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `chamas` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `contributionAmountMwk` REAL NOT NULL,
                        `payoutIndex` INTEGER NOT NULL,
                        `nextPayoutDate` TEXT NOT NULL,
                        `monthlyFeeStatus` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `chama_members` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `chamaId` INTEGER NOT NULL,
                        `userId` TEXT NOT NULL,
                        `joinedAt` INTEGER NOT NULL,
                        `payoutPosition` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `stores` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `userId` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `bannerImage` TEXT NOT NULL,
                        `bio` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `subdobaLinkUrl` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `policies` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `fullName` TEXT NOT NULL,
                        `nrcNumber` TEXT NOT NULL,
                        `beneficiaryPhone` TEXT NOT NULL,
                        `productType` TEXT NOT NULL,
                        `premiumMwk` REAL NOT NULL,
                        `pdfString` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): DobadobaDatabase {
            return INSTANCE ?: synchronized(this) {
                var instance: DobadobaDatabase? = null
                try {
                    val passphrase = DobadobaSecurity.getDatabasePassphrase(context)
                    
                    try {
                        System.loadLibrary("sqlcipher")
                    } catch (e: Throwable) {
                        try {
                            val sqlClass = Class.forName("net.sqlcipher.database.SQLiteDatabase")
                            val loadMethod = sqlClass.getMethod("loadLibs", Context::class.java)
                            loadMethod.invoke(null, context.applicationContext)
                        } catch (e2: Throwable) {
                            // Ignore or log if loaded by framework
                        }
                    }

                    val factory = try {
                        val factoryClass = Class.forName("net.zetetic.database.sqlcipher.SupportFactory")
                        val constructor = factoryClass.getConstructor(ByteArray::class.java)
                        constructor.newInstance(passphrase) as androidx.sqlite.db.SupportSQLiteOpenHelper.Factory
                    } catch (e: Throwable) {
                        throw RuntimeException("SQLCipher SupportFactory reflection failed", e)
                    }
                    
                    val db = Room.databaseBuilder(
                        context.applicationContext,
                        DobadobaDatabase::class.java,
                        "dobadoba_database"
                    )
                        .openHelperFactory(factory)
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                        .fallbackToDestructiveMigration(dropAllTables = true)
                        .build()
                    
                    // Verify the database is readable and functional
                    db.openHelper.writableDatabase
                    instance = db
                } catch (dbError: Throwable) {
                    android.util.Log.w("DobadobaDatabase", "WARNING: SQLCipher secure database initialization failed. Fallback triggered. Reason: ${dbError.localizedMessage}", dbError)
                    try {
                        val fallbackDb = Room.databaseBuilder(
                            context.applicationContext,
                            DobadobaDatabase::class.java,
                            "dobadoba_database_unencrypted"
                        )
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .fallbackToDestructiveMigration(dropAllTables = true)
                            .build()
                        fallbackDb.openHelper.writableDatabase
                        android.util.Log.w("DobadobaDatabase", "WARNING: Standard unencrypted database fallback successfully opened.")
                        instance = fallbackDb
                    } catch (fallbackError: Throwable) {
                        android.util.Log.e("DobadobaDatabase", "CRITICAL WARNING: Unencrypted fallback failed. Falling back further to pure in-memory SQLite state. Root cause: ${fallbackError.localizedMessage}", fallbackError)
                        instance = Room.inMemoryDatabaseBuilder(
                            context.applicationContext,
                            DobadobaDatabase::class.java
                        )
                            .fallbackToDestructiveMigration(dropAllTables = true)
                            .build()
                    }
                }
                
                val finalInstance = instance!!
                INSTANCE = finalInstance
                finalInstance
            }
        }
    }
}
