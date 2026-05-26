package com.example.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Onboarding screen destination.
 */
@Serializable
object OnboardingDestination

/**
 * Login screen with SMS/Biometric verification.
 */
@Serializable
object LoginDestination

/**
 * Home feed destination.
 */
@Serializable
object HomeDestination

/**
 * Discover algorithmic feed destination.
 */
@Serializable
object DiscoverDestination

/**
 * Post creation / DobaLens tab destination.
 */
@Serializable
object PostDestination

/**
 * Chat tab with threads.
 */
@Serializable
object ChatDestination

/**
 * Dedicated Chat thread conversation screen.
 */
@Serializable
data class ChatThreadDestination(val threadId: String)

/**
 * Profile page destination.
 */
@Serializable
object ProfileDestination

/**
 * Profile page of a specific user.
 */
@Serializable
data class UserProfileDestination(val userId: String)

/**
 * DobaMarket page destination.
 */
@Serializable
object MarketDestination
