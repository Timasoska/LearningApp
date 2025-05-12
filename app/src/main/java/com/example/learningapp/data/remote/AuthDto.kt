package com.example.learningapp.data.remote

import kotlinx.serialization.Serializable

// Эти DTO должны соответствовать com.example.model на сервере

@Serializable
data class RegisterRequestDto( // Имя может быть таким же или с суффиксом Dto
    val login: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponseDto( // Это то, что приходит от сервера
    val success: Boolean,
    val message: String,
    val userId: Int? = null // userId может быть null при ошибке
)

@Serializable
data class ErrorResponseDto( // Для ошибок от сервера
    val message: String
)