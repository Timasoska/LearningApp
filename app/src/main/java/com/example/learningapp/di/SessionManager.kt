package com.example.learningapp.di


import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Hilt будет управлять этим как Singleton
class SessionManager @Inject constructor() { // Конструктор для Hilt

    private var currentUserId: Int? = null

    fun saveUserId(userId: Int) {
        currentUserId = userId
    }

    fun getCurrentUserId(): Int? {
        return currentUserId
    }

    fun clearSession() {
        currentUserId = null
    }

    fun isLoggedIn(): Boolean {
        return currentUserId != null
    }
}