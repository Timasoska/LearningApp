package com.example.learningapp.presentation.authorization


data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false,
    val loginSuccess: Boolean = false,
    val loggedInUserId: Int? = null // Store user ID after login/register
)