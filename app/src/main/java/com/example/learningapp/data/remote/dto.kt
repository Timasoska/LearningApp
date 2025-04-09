package com.example.learningapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val email: String, // Field names MUST match server exactly
    val password: String,
    // Add 'login' field if your server RegisterRequest still has it
    val login: String // Assuming server uses this field name based on its RegisterRequest
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

// Matches server's AuthResponse
@Serializable
data class AuthResponseDto(
    val success: Boolean,
    val message: String,
    val userId: Int? = null
)

// Matches server's ErrorResponse
@Serializable
data class ErrorResponseDto(
    val message: String
)