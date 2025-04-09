package com.example.learningapp.domain.usecase.auth

import com.example.learningapp.data.remote.AuthResponseDto
import com.example.learningapp.data.remote.RegisterRequestDto
import com.example.learningapp.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    // Возвращаемый тип kotlin.Result
    suspend operator fun invoke(request: RegisterRequestDto): Result<AuthResponseDto> {
        return repository.register(request) // Просто передаем результат репозитория
    }
}