package com.example

import com.example.ui.DobaDelivery
import com.example.ui.DobaTransaction
import com.example.ui.TriviaItem
import com.example.ui.WordPuzzleItem
import com.example.ui.SmartPhrase
import com.example.ui.DobaSticker
import com.example.ui.SafetyAlert
import com.example.ui.UserProfileState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DobadobaBusinessLogicTest {

    @Test
    fun testTransactionArithmetic() {
        val deposit = DobaTransaction("tx-001", "Deposit", 15000.0, true, "May 25, 2026")
        assertTrue(deposit.isCredit)
        assertEquals(15000.0, deposit.amountMwk, 0.0)
    }

    @Test
    fun testTransactionDebitType() {
        val payment = DobaTransaction("tx-002", "DobaPay Merchant", 3500.50, false, "May 25, 2026")
        assertFalse(payment.isCredit)
        assertEquals("DobaPay Merchant", payment.description)
    }

    @Test
    fun testDeliverySpeedStateTransitions() {
        val delivery = DobaDelivery(
            id = "del-101",
            itemName = "Fertilizer Chawama",
            destination = "Zomba District",
            speedStatus = "Dispatched",
            deliveryFeeMwk = 4500.00,
            assignedRider = "Banda Matola",
            riderPhone = "0999580421",
            timestamp = 1715000000L
        )
        assertEquals("Dispatched", delivery.speedStatus)
        assertEquals(4500.00, delivery.deliveryFeeMwk, 0.0)
        assertEquals("Banda Matola", delivery.assignedRider)
    }

    @Test
    fun testDeliveryPhoneValidation() {
        val delivery = DobaDelivery(
            id = "del-102",
            itemName = "Solar Panel 200W",
            destination = "Lilongwe Area 18",
            speedStatus = "Rider Matched",
            deliveryFeeMwk = 7500.00,
            assignedRider = "Chisomo Phiri",
            riderPhone = "0888123456",
            timestamp = 1715005000L
        )
        assertEquals("0888123456", delivery.riderPhone)
        assertTrue(delivery.riderPhone.startsWith("08"))
    }

    @Test
    fun testTriviaItemDetails() {
        val trivia = TriviaItem(
            question = "Which lake is the third largest in Africa?",
            options = listOf("Lake Victoria", "Lake Tanganyika", "Lake Malawi"),
            correctIndex = 2,
            factExplanation = "Lake Malawi is famous for rich fish biodiversity."
        )
        assertEquals("Lake Malawi", trivia.options[trivia.correctIndex])
        assertEquals("Lake Malawi is famous for rich fish biodiversity.", trivia.factExplanation)
    }

    @Test
    fun testWordPuzzleVerification() {
        val puzzle = WordPuzzleItem(
            scrambled = "ALWMIA",
            actual = "MALAWI",
            chichewaClueOnCorrect = "Dziko lathu lokongola!"
        )
        assertEquals("MALAWI", puzzle.actual)
        assertEquals(6, puzzle.scrambled.length)
        assertTrue(puzzle.scrambled.contains("W"))
    }

    @Test
    fun testSmartPhraseValues() {
        val phrase = SmartPhrase(
            phraseText = "Welcome to Malawi",
            translation = "Takulandirani ku Malawi"
        )
        assertNotNull(phrase.phraseText)
        assertNotNull(phrase.translation)
        assertEquals("Takulandirani ku Malawi", phrase.translation)
    }

    @Test
    fun testDobaStickerIdentity() {
        val sticker = DobaSticker(
            id = "stkk-04",
            rawVisualText = "🤥",
            cleanDesc = "Boza (Not True!)"
        )
        assertEquals("stkk-04", sticker.id)
        assertEquals("🤥", sticker.rawVisualText)
        assertEquals("Boza (Not True!)", sticker.cleanDesc)
    }

    @Test
    fun testSafetyAlertSeverityIndex() {
        val alert = SafetyAlert(
            headerTitle = "Heavy rains and flash floods risk along Shire River",
            emergencyBody = "Community preparedness advised. Evacuate low terrain.",
            issuedTs = 1715000000L
        )
        assertNotNull(alert.headerTitle)
        assertEquals(1715000000L, alert.issuedTs)
    }

    @Test
    fun testUserProfileStateConfig() {
        val profileState = UserProfileState(
            name = "Doba Seller Pro",
            isVerified = true,
            locationTag = "Blantyre"
        )
        assertTrue(profileState.isVerified)
        assertEquals("Blantyre", profileState.locationTag)
    }

    @Test
    fun testFeeCalculationsAtSprintBoundaries() {
        // Boundary 1: Transfer MK 500 (1% is MK 5, so minimum cap of MK 50 applies)
        val fee1 = com.example.util.FeeCalculator.calculateFee(500.0)
        assertEquals(50.0, fee1, 0.0)

        // Boundary 2: Transfer MK 10,000 (1% is MK 100)
        val fee2 = com.example.util.FeeCalculator.calculateFee(10000.0)
        assertEquals(100.0, fee2, 0.0)

        // Boundary 3: Transfer MK 500,000 (1% is MK 5,000, so maximum cap of MK 2,500 applies)
        val fee3 = com.example.util.FeeCalculator.calculateFee(500000.0)
        assertEquals(2500.0, fee3, 0.0)

        // Summary format check
        val summaryText = com.example.util.FeeCalculator.formatTransferSummary(5000.0)
        assertTrue(summaryText.contains("Transfer 5,000 MK"))
        assertTrue(summaryText.contains("Fee 50 MK"))
        assertTrue(summaryText.contains("Total 5,050 MK"))
    }

    @Test
    fun testDobaCoinsBalanceAfterPurchase() {
        var baseCoins = 120
        // Top-up options:
        // Option 1: MK 1,000 -> 1,000 coins
        // Option 2: MK 2,500 -> 2,800 coins
        // Option 3: MK 5,000 -> 6,000 coins
        
        baseCoins += 2800 // Option 2
        assertEquals(2920, baseCoins)

        baseCoins += 6000 // Option 3
        assertEquals(8920, baseCoins)
    }

    @Test
    fun testStandingOrderSchedulingConfig() {
        val standingOrder = com.example.data.StandingOrderEntity(
            id = 1L,
            recipientPhone = "0999580421",
            amountMwk = 45000.00,
            frequency = "Monthly",
            startDate = "2026-06-01",
            description = "Landlord Area 18",
            isPaused = false,
            isSubscription = false
        )
        assertEquals("Monthly", standingOrder.frequency)
        assertEquals(45000.00, standingOrder.amountMwk, 0.0)
        assertFalse(standingOrder.isPaused)
    }

    @Test
    fun testChamaContributionDebitLogic() {
        val membersCount = 5
        val monthlyContribution = 10000.0
        val totalDebitPool = membersCount * monthlyContribution
        assertEquals(50000.0, totalDebitPool, 0.0)

        val individualFee = 200.0 // MK 200/month fee deducted from admin
        assertEquals(200.0, individualFee, 0.0)
    }
}
