package com.example.learningapp.presentation.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.data.remote.LoginRequestDto
import com.example.learningapp.data.remote.RegisterRequestDto
import com.example.learningapp.data.remote.AuthResponseDto
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
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun register(email: String, login: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, registrationSuccess = false) }

            val request = RegisterRequestDto(email = email, login = login, password = password)
            // registerUseCase теперь возвращает kotlin.Result<AuthResponseDto>
            // Тип можно вывести автоматически или указать kotlin.Result
            val result: kotlin.Result<AuthResponseDto> = registerUseCase(request) // <-- Используем kotlin.Result

            // Используем стандартные методы kotlin.Result
            result.onSuccess { authData -> // Выполняется если isSuccess
                _state.update {
                    it.copy(
                        isLoading = false,
                        registrationSuccess = true,
                        loggedInUserId = authData.userId // Прямой доступ к данным внутри onSuccess
                    )
                }
            }.onFailure { exception -> // Выполняется если isFailure
                _state.update {
                    it.copy(
                        isLoading = false,
                        // Используем сообщение из исключения
                        error = exception.message ?: "Registration failed"
                    )
                }
            }
            // Альтернативная запись с if/else (если предпочитаешь)
            /*
            if (result.isSuccess) {
                val authData = result.getOrNull() // Безопасно получаем данные
                _state.update {
                    it.copy(
                        isLoading = false,
                        registrationSuccess = true,
                        loggedInUserId = authData?.userId // Доступ к userId
                    )
                }
            } else { // result.isFailure
                val exception = result.exceptionOrNull() // Получаем исключение
                _state.update {
                    it.copy(
                        isLoading = false,
                        // Используем сообщение из исключения
                        error = exception?.message ?: "Registration failed"
                    )
                }
            }
            */
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, loginSuccess = false) }

            val request = LoginRequestDto(email = email, password = password)
            // loginUseCase теперь возвращает kotlin.Result<AuthResponseDto>
            val result: kotlin.Result<AuthResponseDto> = loginUseCase(request) // <-- Используем kotlin.Result

            // Используем стандартные методы kotlin.Result
            result.onSuccess { authData ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        loggedInUserId = authData.userId
                    )
                }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Login failed"
                    )
                }
            }
            // Альтернативная запись с if/else
            /*
            if (result.isSuccess) {
                val authData = result.getOrNull() // Безопасно получаем данные
                _state.update {
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        loggedInUserId = authData?.userId // Доступ к userId
                    )
                }
            } else { // result.isFailure
                val exception = result.exceptionOrNull() // Получаем исключение
                _state.update {
                    it.copy(
                        isLoading = false,
                        // Используем сообщение из исключения
                        error = exception?.message ?: "Login failed"
                    )
                }
            }
            */
        }
    }

    // Остальные функции без изменений
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetNavigationFlags() {
        _state.update { it.copy(loginSuccess = false, registrationSuccess = false) }
    }
}