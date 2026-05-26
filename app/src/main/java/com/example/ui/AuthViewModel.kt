package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.data.DobadobaBiometricHelper
import com.example.data.DobadobaSecurity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState {
    object Idle : AuthState()
    object OtpSent : AuthState()
    object Verifying : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun submitPhoneNumber(phone: String) {
        if (phone.isBlank()) {
            _error.value = "Phone number cannot be empty"
            _state.value = AuthState.Error("Empty phone number")
            return
        }
        _error.value = null
        _state.value = AuthState.OtpSent
    }

    fun submitOtp(otp: String) {
        if (otp == "2026") {
            _state.value = AuthState.Verifying
        } else {
            _state.value = AuthState.Error("Invalid OTP")
        }
    }

    fun signOut(context: Context, onSignOutComplete: () -> Unit) {
        try {
            // 1. Clear session token from EncryptedSharedPreferences
            val masterKey = androidx.security.crypto.MasterKey.Builder(context)
                .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                .build()
            val sharedPreferences = androidx.security.crypto.EncryptedSharedPreferences.create(
                context,
                "secure_user_session",
                masterKey,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            sharedPreferences.edit().clear().apply()

            // 2. Clear Biometric Helper Keys
            DobadobaBiometricHelper.clearKeys()

            // 3. Clear In-memory or shared prepassphrase in DobadobaSecurity
            DobadobaSecurity.clearPassphrase(context)
        } catch (e: Exception) {
            android.util.Log.e("AuthViewModel", "Error occurred during secure sign out cleanup", e)
        } finally {
            onSignOutComplete()
        }
    }
}
