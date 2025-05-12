package com.example.learningapp.presentation.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.data.remote.LoginRequestDto
import com.example.learningapp.data.remote.RegisterRequestDto
import com.example.learningapp.data.remote.AuthResponseDto
import com.example.learningapp.di.SessionManager
import com.example.learningapp.domain.usecase.auth.LoginUseCase
import com.example.learningapp.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager // Инжектируем SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun register(email: String, login: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, registrationSuccess = false) }
            val request = RegisterRequestDto(email = email, login = login, password = password)
            val result = registerUseCase(request)

            result.onSuccess { authData ->
                authData.userId?.let { userId -> // Сохраняем userId, если он есть
                    sessionManager.saveUserId(userId)
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        registrationSuccess = true,
                        loggedInUserId = authData.userId
                    )
                }
            }.onFailure { exception ->
                _state.update {
                    it.copy(isLoading = false, error = exception.message ?: "Registration failed")
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, loginSuccess = false) }
            val request = LoginRequestDto(email = email, password = password)
            val result = loginUseCase(request)

            result.onSuccess { authData ->
                authData.userId?.let { userId -> // Сохраняем userId, если он есть
                    sessionManager.saveUserId(userId)
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        loggedInUserId = authData.userId
                    )
                }
            }.onFailure { exception ->
                _state.update {
                    it.copy(isLoading = false, error = exception.message ?: "Login failed")
                }
            }
        }
    }

    // Метод для выхода пользователя (если будешь реализовывать)
    fun logout() {
        sessionManager.clearSession()
        // Здесь также может быть логика для навигации на экран логина,
        // сброса состояния других ViewModel и т.д.
        _state.update { AuthState() } // Сброс состояния AuthViewModel
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetNavigationFlags() {
        _state.update { it.copy(loginSuccess = false, registrationSuccess = false) }
    }
}