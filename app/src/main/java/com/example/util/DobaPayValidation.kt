package com.example.util

object DobaPayValidation {
    /**
     * Validates Malawian phone numbers matching ^(088|099|098)[0-9]{7}$
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^(088|099|098)[0-9]{7}$"))
    }

    /**
     * Formats MWK amounts with correct thousand/comma separators, e.g. 1000000.00 -> 1,000,000.00
     */
    fun formatMwkAmount(amount: Double): String {
        return String.format("%,.2f", amount)
    }
}
