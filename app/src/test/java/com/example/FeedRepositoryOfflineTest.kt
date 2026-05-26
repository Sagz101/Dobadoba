package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DobadobaDatabase
import com.example.data.DobadobaRepository
import com.example.data.Post
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FeedRepositoryOfflineTest {

    private lateinit var database: DobadobaDatabase
    private lateinit var repository: DobadobaRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DobadobaDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = DobadobaRepository(database.dobadobaDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCachedFeedItemsReturnedImmediatelyOffline() = runBlocking {
        // Given: inserting some feed items into the local in-memory database
        val cachedPost = Post(
            id = 505,
            username = "Offline Alimi coop 🌽",
            captionEnglish = "Local fertilizer distribution guide",
            captionChichewa = "Zofotokozera za manyowa adera lathu",
            likesCount = 12,
            commentsCount = 2,
            isLiked = false
        )

        repository.insertPost(cachedPost)

        // When: retrieving the feed items
        // Since we are offline (no network queries are even made), cached feed items should be returned immediately
        val postsList = repository.allPosts.first()

        // Then: verify the cached items are returned correctly
        assertEquals(1, postsList.size)
        assertEquals("Offline Alimi coop 🌽", postsList[0].username)
        assertEquals("Local fertilizer distribution guide", postsList[0].captionEnglish)
        assertEquals(505, postsList[0].id)
    }
}
