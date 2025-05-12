package com.example.learningapp.di


import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SessionManager @Inject constructor() {
    private val _currentUserIdFlow = MutableStateFlow<Int?>(null)
    val currentUserIdFlow: StateFlow<Int?> = _currentUserIdFlow.asStateFlow()

    // currentUserId остается для синхронного доступа, если очень нужно, но лучше через Flow
    private var currentUserId: Int? = null

    fun saveUserId(userId: Int) {
        currentUserId = userId
        _currentUserIdFlow.value = userId
    }

    fun getCurrentUserId(): Int? { // Синхронный геттер
        return _currentUserIdFlow.value // Или currentUserId, если используешь обе переменные
    }

    fun clearSession() {
        currentUserId = null
        _currentUserIdFlow.value = null
    }

    fun isLoggedIn(): Boolean {
        return _currentUserIdFlow.value != null
    }
}