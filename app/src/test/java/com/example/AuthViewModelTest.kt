package com.example

import com.example.ui.AuthState
import com.example.ui.AuthViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthViewModelTest {

    @Test
    fun testEmptyPhoneNumberShowsErrorState() {
        val viewModel = AuthViewModel()
        viewModel.submitPhoneNumber("")
        
        // Assert error flow value is set and state is Error
        assertEquals("Phone number cannot be empty", viewModel.error.value)
        assertTrue(viewModel.state.value is AuthState.Error)
    }

    @Test
    fun testValidOtpTransitionsStateToVerifying() {
        val viewModel = AuthViewModel()
        
        // 1. Submit valid phone
        viewModel.submitPhoneNumber("0888123456")
        assertEquals(AuthState.OtpSent, viewModel.state.value)
        
        // 2. Submit valid OTP (2026 is the configured code)
        viewModel.submitOtp("2026")
        assertEquals(AuthState.Verifying, viewModel.state.value)
    }
}
