package com.example.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.security.SecureRandom
import android.util.Log

object DobadobaSecurity {
    private const val KEY_ALIAS = "DobaDbKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val PREFS_NAME = "dobadoba_sec_prefs"
    private const val ENCRYPTED_PASSPHRASE_KEY = "encrypted_db_passphrase"
    private const val GCM_IV_KEY = "db_passphrase_iv"

    @Synchronized
    fun getDatabasePassphrase(context: Context): ByteArray {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encryptedPassStr = prefs.getString(ENCRYPTED_PASSPHRASE_KEY, null)
        val ivStr = prefs.getString(GCM_IV_KEY, null)

        if (encryptedPassStr != null && ivStr != null) {
            try {
                val secretKey = getOrCreateSecretKey()
                val iv = Base64.decode(ivStr, Base64.DEFAULT)
                val encryptedPass = Base64.decode(encryptedPassStr, Base64.DEFAULT)

                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
                return cipher.doFinal(encryptedPass)
            } catch (e: Exception) {
                Log.e("DobadobaSecurity", "Decryption of DB passphrase failed. Regenerating fallback...", e)
            }
        }

        // Generate new 256-bit passphrase (32 bytes)
        val secureRandom = SecureRandom()
        val passphrase = ByteArray(32)
        secureRandom.nextBytes(passphrase)

        try {
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryptedPass = cipher.doFinal(passphrase)

            prefs.edit()
                .putString(ENCRYPTED_PASSPHRASE_KEY, Base64.encodeToString(encryptedPass, Base64.DEFAULT))
                .putString(GCM_IV_KEY, Base64.encodeToString(iv, Base64.DEFAULT))
                .apply()
        } catch (e: Exception) {
            Log.e("DobadobaSecurity", "Encryption of new DB passphrase failed.", e)
        }

        return passphrase
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (entry != null) {
            return entry.secretKey
        }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun performPlaintextToEncryptedMigration(context: Context) {
        val dbFile = context.getDatabasePath("dobadoba_database")
        val migrationPrefs = context.getSharedPreferences("db_migration_prefs", Context.MODE_PRIVATE)
        val isMigrated = migrationPrefs.getBoolean("is_secure_migrated", false)

        if (dbFile.exists() && !isMigrated) {
            val tempFile = File(context.cacheDir, "temp_plaintext_db")
            if (tempFile.exists()) tempFile.delete()

            try {
                dbFile.copyTo(tempFile, overwrite = true)
                dbFile.delete()

                File(dbFile.path + "-journal").delete()
                File(dbFile.path + "-shm").delete()
                File(dbFile.path + "-wal").delete()

                val encryptedDb = DobadobaDatabase.getDatabase(context)
                val dao = encryptedDb.dobadobaDao()

                val plaintextDb = android.database.sqlite.SQLiteDatabase.openDatabase(
                    tempFile.absolutePath,
                    null,
                    android.database.sqlite.SQLiteDatabase.OPEN_READONLY
                )

                // Migrate 'posts' table
                try {
                    val cursor = plaintextDb.rawQuery("SELECT * FROM posts", null)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                        val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                        val userAvatarUrl = cursor.getString(cursor.getColumnIndexOrThrow("userAvatarUrl"))
                        val captionEnglish = cursor.getString(cursor.getColumnIndexOrThrow("captionEnglish"))
                        val captionChichewa = cursor.getString(cursor.getColumnIndexOrThrow("captionChichewa"))
                        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                        val videoUrl = cursor.getString(cursor.getColumnIndexOrThrow("videoUrl"))
                        val isVideo = cursor.getInt(cursor.getColumnIndexOrThrow("isVideo")) == 1
                        val likesCount = cursor.getInt(cursor.getColumnIndexOrThrow("likesCount"))
                        val commentsCount = cursor.getInt(cursor.getColumnIndexOrThrow("commentsCount"))
                        val isLiked = cursor.getInt(cursor.getColumnIndexOrThrow("isLiked")) == 1
                        val isTrending = cursor.getInt(cursor.getColumnIndexOrThrow("isTrending")) == 1
                        val isDiscover = cursor.getInt(cursor.getColumnIndexOrThrow("isDiscover")) == 1
                        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))

                        kotlinx.coroutines.runBlocking {
                            dao.insertPost(Post(id, username, userAvatarUrl, captionEnglish, captionChichewa, imageUrl, videoUrl, isVideo, likesCount, commentsCount, isLiked, isTrending, isDiscover, timestamp))
                        }
                    }
                    cursor.close()
                } catch (e: Exception) { Log.e("DobadobaSecurity", "Migrating posts table failed", e) }

                // Migrate 'stories' table
                try {
                    val cursor = plaintextDb.rawQuery("SELECT * FROM stories", null)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                        val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                        val textOverlay = cursor.getString(cursor.getColumnIndexOrThrow("textOverlay"))
                        val isVideo = cursor.getInt(cursor.getColumnIndexOrThrow("isVideo")) == 1
                        val locationTag = cursor.getString(cursor.getColumnIndexOrThrow("locationTag"))
                        val isCloseFriends = cursor.getInt(cursor.getColumnIndexOrThrow("isCloseFriends")) == 1
                        val isPoll = cursor.getInt(cursor.getColumnIndexOrThrow("isPoll")) == 1
                        val pollQuestion = cursor.getString(cursor.getColumnIndexOrThrow("pollQuestion"))
                        val pollOptionA = cursor.getString(cursor.getColumnIndexOrThrow("pollOptionA"))
                        val pollOptionB = cursor.getString(cursor.getColumnIndexOrThrow("pollOptionB"))
                        val pollVotesA = cursor.getInt(cursor.getColumnIndexOrThrow("pollVotesA"))
                        val pollVotesB = cursor.getInt(cursor.getColumnIndexOrThrow("pollVotesB"))
                        val userAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("userAnswer"))
                        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))

                        kotlinx.coroutines.runBlocking {
                            dao.insertStory(Story(id, username, imageUrl, textOverlay, isVideo, locationTag, isCloseFriends, isPoll, pollQuestion, pollOptionA, pollOptionB, pollVotesA, pollVotesB, userAnswer, timestamp))
                        }
                    }
                    cursor.close()
                } catch (e: Exception) { Log.e("DobadobaSecurity", "Migrating stories failed", e) }

                // Migrate 'messages' table
                try {
                    val cursor = plaintextDb.rawQuery("SELECT * FROM messages", null)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                        val sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"))
                        val recipient = cursor.getString(cursor.getColumnIndexOrThrow("recipient"))
                        val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
                        val isVoiceNote = cursor.getInt(cursor.getColumnIndexOrThrow("isVoiceNote")) == 1
                        val voiceDurationSeconds = cursor.getInt(cursor.getColumnIndexOrThrow("voiceDurationSeconds"))
                        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                        val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))

                        kotlinx.coroutines.runBlocking {
                            dao.insertMessage(Message(id, sender, recipient, text, isVoiceNote, voiceDurationSeconds, imageUrl, timestamp, status))
                        }
                    }
                    cursor.close()
                } catch (e: Exception) { Log.e("DobadobaSecurity", "Migrating messages failed", e) }

                // Migrate 'market_listings' table
                try {
                    val cursor = plaintextDb.rawQuery("SELECT * FROM market_listings", null)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                        val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                        val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                        val priceMwk = cursor.getDouble(cursor.getColumnIndexOrThrow("priceMwk"))
                        val sellerName = cursor.getString(cursor.getColumnIndexOrThrow("sellerName"))
                        val sellerPhone = cursor.getString(cursor.getColumnIndexOrThrow("sellerPhone"))
                        val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                        val mobileMoneyType = cursor.getString(cursor.getColumnIndexOrThrow("mobileMoneyType"))
                        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))

                        kotlinx.coroutines.runBlocking {
                            dao.insertMarketListing(MarketListing(id, title, description, priceMwk, sellerName, sellerPhone, location, imageUrl, mobileMoneyType, timestamp))
                        }
                    }
                    cursor.close()
                } catch (e: Exception) { Log.e("DobadobaSecurity", "Migrating market_listings failed", e) }

                // Migrate 'groups' table
                try {
                    val cursor = plaintextDb.rawQuery("SELECT * FROM groups", null)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                        val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                        val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                        val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                        val membersCount = cursor.getInt(cursor.getColumnIndexOrThrow("membersCount"))
                        val isJoined = cursor.getInt(cursor.getColumnIndexOrThrow("isJoined")) == 1
                        val pinnedPost = cursor.getString(cursor.getColumnIndexOrThrow("pinnedPost"))
                        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))

                        kotlinx.coroutines.runBlocking {
                            dao.insertGroup(Group(id, name, description, category, membersCount, isJoined, pinnedPost, timestamp))
                        }
                    }
                    cursor.close()
                } catch (e: Exception) { Log.e("DobadobaSecurity", "Migrating groups failed", e) }

                // Migrate 'events' table
                try {
                    val cursor = plaintextDb.rawQuery("SELECT * FROM events", null)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                        val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                        val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                        val dateString = cursor.getString(cursor.getColumnIndexOrThrow("dateString"))
                        val location = cursor.getString(cursor.getColumnIndexOrThrow("location"))
                        val organizer = cursor.getString(cursor.getColumnIndexOrThrow("organizer"))
                        val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                        val rsvpCount = cursor.getInt(cursor.getColumnIndexOrThrow("rsvpCount"))
                        val isRsvped = cursor.getInt(cursor.getColumnIndexOrThrow("isRsvped")) == 1
                        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))

                        kotlinx.coroutines.runBlocking {
                            dao.insertEvent(AppEvent(id, title, description, dateString, location, organizer, imageUrl, rsvpCount, isRsvped, timestamp))
                        }
                    }
                    cursor.close()
                } catch (e: Exception) { Log.e("DobadobaSecurity", "Migrating events failed", e) }

                plaintextDb.close()
                tempFile.delete()
                migrationPrefs.edit().putBoolean("is_secure_migrated", true).apply()
                Log.d("DobadobaSecurity", "SQLCipher database migration completed!")
            } catch (e: Exception) {
                Log.e("DobadobaSecurity", "Error during plaintext to encrypted migration", e)
                if (tempFile.exists()) tempFile.delete()
            }
        } else {
            migrationPrefs.edit().putBoolean("is_secure_migrated", true).apply()
        }
    }

    fun clearPassphrase(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            Log.d("DobadobaSecurity", "Dobadoba database passphrase wiped out from SharedPreferences.")
        } catch (e: Exception) {
            Log.e("DobadobaSecurity", "Error wiping secure passphrase.", e)
        }
    }
}
