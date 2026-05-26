package com.example

import com.example.util.DobaPayValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DobaPayValidationTest {

    @Test
    fun testValidPhoneNumbers() {
        // Phones starting with 088, 099, 098 and having exactly 10 digits
        assertTrue(DobaPayValidation.isValidPhoneNumber("0888123456"))
        assertTrue(DobaPayValidation.isValidPhoneNumber("0999580421"))
        assertTrue(DobaPayValidation.isValidPhoneNumber("0987654321"))
    }

    @Test
    fun testInvalidPhoneNumbers() {
        // Wrong length, non-digit or incorrect prefix
        assertFalse(DobaPayValidation.isValidPhoneNumber("088812345"))  // 9 digits
        assertFalse(DobaPayValidation.isValidPhoneNumber("08881234567")) // 11 digits
        assertFalse(DobaPayValidation.isValidPhoneNumber("0777123456"))  // wrong prefix
        assertFalse(DobaPayValidation.isValidPhoneNumber("0888123abc"))  // non-numeric
        assertFalse(DobaPayValidation.isValidPhoneNumber(""))            // empty
    }

    @Test
    fun testMwkAmountFormatting() {
        // Test MWK currency amount formatting with comma separators
        assertEquals("1,250,000.50", DobaPayValidation.formatMwkAmount(1250000.50))
        assertEquals("38,500.00", DobaPayValidation.formatMwkAmount(38500.0))
        assertEquals("150.25", DobaPayValidation.formatMwkAmount(150.25))
        assertEquals("0.00", DobaPayValidation.formatMwkAmount(0.0))
    }
}
