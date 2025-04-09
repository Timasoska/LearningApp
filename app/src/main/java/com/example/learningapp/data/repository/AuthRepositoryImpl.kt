package com.example.learningapp.data.repository

import android.util.Log
import com.example.Constants
import com.example.learningapp.data.remote.AuthResponseDto
import com.example.learningapp.data.remote.ErrorResponseDto
import com.example.learningapp.data.remote.LoginRequestDto
import com.example.learningapp.data.remote.RegisterRequestDto
import com.example.learningapp.domain.repository.AuthRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.serialization.json.Json
import javax.inject.Inject
//import com.example.learningapp.presentation.authorization.Result// <--- Добавь этот импорт
import io.ktor.client.statement.*
import kotlin.coroutines.cancellation.CancellationException

// Определим кастомное исключение для передачи осмысленных ошибок
class AuthApiException(message: String, val statusCode: Int? = null) : Exception(message)

class AuthRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : AuthRepository {

    // Возвращаемый тип теперь kotlin.Result
    override suspend fun register(request: RegisterRequestDto): Result<AuthResponseDto> {
        return try {
            val response = client.post {
                url("${Constants.BASE_URL}/auth/register")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            // Используем kotlin.Result.success
            Result.success(response.body())
        } catch (e: CancellationException) {
            throw e // Важно перебрасывать CancellationException
        } catch (e: ClientRequestException) {
            val errorResponse = parseErrorBody(e.response)
            val message = errorResponse?.message ?: "Registration failed: ${e.response.status.description}"
            Log.e("AuthRepository", "Register failed (Client Error ${e.response.status}): $message", e)
            // Используем kotlin.Result.failure с нашим кастомным исключением
            Result.failure(AuthApiException(message, e.response.status.value))
        } catch (e: ServerResponseException) {
            val message = "Server error during registration (${e.response.status.value})"
            Log.e("AuthRepository", "Register failed (Server Error ${e.response.status}): $message", e)
            // Используем kotlin.Result.failure
            Result.failure(AuthApiException(message, e.response.status.value))
        } catch (e: Exception) { // Ловим другие ошибки (сеть, сериализация и т.д.)
            val message = "An unexpected error occurred: ${e.localizedMessage}"
            Log.e("AuthRepository", "Register failed (General Error): $message", e)
            // Используем kotlin.Result.failure
            Result.failure(AuthApiException(message))
        }
    }

    // Возвращаемый тип теперь kotlin.Result
    override suspend fun login(request: LoginRequestDto): Result<AuthResponseDto> {
        return try {
            val response = client.post {
                url("${Constants.BASE_URL}/auth/login")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            // Используем kotlin.Result.success
            Result.success(response.body())
        } catch (e: CancellationException) {
            throw e
        } catch (e: ClientRequestException) {
            val errorResponse = parseErrorBody(e.response)
            val message = errorResponse?.message ?: "Login failed: ${e.response.status.description}"
            Log.e("AuthRepository", "Login failed (Client Error ${e.response.status}): $message", e)
            // Используем kotlin.Result.failure
            Result.failure(AuthApiException(message, e.response.status.value))
        } catch (e: ServerResponseException) {
            val message = "Server error during login (${e.response.status.value})"
            Log.e("AuthRepository", "Login failed (Server Error ${e.response.status}): $message", e)
            // Используем kotlin.Result.failure
            Result.failure(AuthApiException(message, e.response.status.value))
        } catch (e: Exception) {
            val message = "An unexpected error occurred: ${e.localizedMessage}"
            Log.e("AuthRepository", "Login failed (General Error): $message", e)
            // Используем kotlin.Result.failure
            Result.failure(AuthApiException(message))
        }
    }

    // Вспомогательная функция остается без изменений
    private suspend fun parseErrorBody(response: HttpResponse): ErrorResponseDto? {
        return try {
            val errorBodyText = response.bodyAsText()
            Log.d("AuthRepository", "Error response body: $errorBodyText")
            Json { ignoreUnknownKeys = true }.decodeFromString<ErrorResponseDto>(errorBodyText)
        } catch (e: Exception) {
            Log.w("AuthRepository", "Failed to parse error body: ${e.message}")
            null
        }
    }
}