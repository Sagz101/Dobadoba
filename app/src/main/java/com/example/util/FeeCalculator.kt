package com.example.util

import java.util.Locale

object FeeCalculator {
    fun calculateFee(amount: Double): Double {
        val calculated = amount * 0.01
        return when {
            calculated < 50.0 -> 50.0
            calculated > 2500.0 -> 2500.0
            else -> calculated
        }
    }

    fun formatMwk(amount: Double): String {
        // Formats as e.g. "5,000 MK" or "500,000 MK"
        return String.format(Locale.US, "%,.0f MK", amount)
    }

    fun formatTransferSummary(amount: Double): String {
        val fee = calculateFee(amount)
        val total = amount + fee
        return "Transfer ${formatMwk(amount)} — Fee ${formatMwk(fee)} — Total ${formatMwk(total)}"
    }
}
