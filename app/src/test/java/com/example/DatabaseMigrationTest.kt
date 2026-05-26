package com.example

import android.content.ContentValues
import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.DobadobaDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DatabaseMigrationTest {

    @Test
    fun testMigration1To2PreservesMessages() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "test_migration_db"
        context.deleteDatabase(dbName)

        // 1. Create a version 1 database using standard FrameworkSQLiteOpenHelper
        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL("""
                        CREATE TABLE IF NOT EXISTS `messages` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                            `sender` TEXT NOT NULL, 
                            `recipient` TEXT NOT NULL, 
                            `text` TEXT NOT NULL, 
                            `isVoiceNote` INTEGER NOT NULL, 
                            `voiceDurationSeconds` INTEGER NOT NULL, 
                            `imageUrl` TEXT NOT NULL, 
                            `timestamp` INTEGER NOT NULL, 
                            `status` TEXT NOT NULL
                        )
                    """.trimIndent())
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()

        val helper = FrameworkSQLiteOpenHelperFactory().create(config)
        val v1Db = helper.writableDatabase

        // Insert a test message into version 1 Database
        val values = ContentValues().apply {
            put("id", 192)
            put("sender", "Kondwani (Zomba)")
            put("recipient", "Chonde Group")
            put("text", "Moni Alimi!")
            put("isVoiceNote", 0)
            put("voiceDurationSeconds", 0)
            put("imageUrl", "")
            put("timestamp", System.currentTimeMillis())
            put("status", "QUEUED")
        }
        v1Db.insert("messages", android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE, values)
        assertEquals(1, v1Db.version)
        v1Db.close()

        // 2. Open it as version 2 and apply migration manually
        val configV2 = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(2) {
                override fun onCreate(db: SupportSQLiteDatabase) {}
                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    if (oldVersion == 1 && newVersion == 2) {
                        InMIGRATION_1_2(db)
                    }
                }
            })
            .build()

        // Apply our actual migration we are validating
        val helperV2 = FrameworkSQLiteOpenHelperFactory().create(configV2)
        val v2Db = helperV2.writableDatabase
        
        // Ensure migration took place on upgrade
        DobadobaDatabase.MIGRATION_1_2.migrate(v2Db)

        // 3. Verify messages table rows are pristine after migration
        val cursor = v2Db.query("SELECT * FROM messages WHERE id = 192")
        assertTrue(cursor.moveToFirst())
        val senderIndex = cursor.getColumnIndexOrThrow("sender")
        val textIndex = cursor.getColumnIndexOrThrow("text")
        val statusIndex = cursor.getColumnIndexOrThrow("status")

        assertEquals("Kondwani (Zomba)", cursor.getString(senderIndex))
        assertEquals("Moni Alimi!", cursor.getString(textIndex))
        assertEquals("QUEUED", cursor.getString(statusIndex))
        cursor.close()

        // Also check that the travel_bookings table was created successfully by the migration
        val checkTableCursor = v2Db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='travel_bookings'")
        assertTrue(checkTableCursor.moveToFirst())
        checkTableCursor.close()

        v2Db.close()
        context.deleteDatabase(dbName)
    }

    private fun InMIGRATION_1_2(db: SupportSQLiteDatabase) {
        db.version = 2
    }
}
