package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DobadobaDatabase
import com.example.data.Message
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ChatQueueIdempotencyTest {

    private lateinit var database: DobadobaDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            DobadobaDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testFlushingQueuedMessagesIsIdempotent() = runBlocking {
        val dao = database.dobadobaDao()

        // 1. Create a pending queued message with a specific ID
        val pendingMessageId = 8899L
        val queuedMsg = Message(
            id = pendingMessageId,
            sender = "Ine (Me)",
            recipient = "Chonde Group",
            text = "Kodi muli kuti?",
            status = "QUEUED"
        )
        dao.insertMessage(queuedMsg)

        // Verify it was correctly inserted as QUEUED
        val queuedList1 = dao.getQueuedMessages()
        assertEquals(1, queuedList1.size)
        assertEquals(pendingMessageId, queuedList1[0].id)
        assertEquals("QUEUED", queuedList1[0].status)

        // 2. Simulate flushing function — first call
        // The flush logic updates the status of the message to 'SENT' using OnConflictStrategy.REPLACE
        val flushAction = suspend {
            val queued = dao.getQueuedMessages()
            for (msg in queued) {
                val sentMsg = msg.copy(status = "SENT")
                dao.insertMessage(sentMsg)
            }
        }

        flushAction()

        // Verify the message is now 'SENT'
        val allMsgAfterFirstFlush = dao.getAllMessages().first()
        assertEquals(1, allMsgAfterFirstFlush.size)
        assertEquals("SENT", allMsgAfterFirstFlush[0].status)
        assertEquals(pendingMessageId, allMsgAfterFirstFlush[0].id)

        // 3. Simulate flushing function — second call
        // Call flush again with the same message or data to test idempotency
        flushAction()

        // Verify that calling the message flush twice with the same pending message ID
        // does not insert duplicate rows into the messages table
        val allMsgAfterSecondFlush = dao.getAllMessages().first()
        assertEquals(1, allMsgAfterSecondFlush.size) // still exactly 1 message
        assertEquals("SENT", allMsgAfterSecondFlush[0].status)
        assertEquals(pendingMessageId, allMsgAfterSecondFlush[0].id)
    }
}
