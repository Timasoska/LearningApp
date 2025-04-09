package com.example.learningapp.domain.usecase.auth

import com.example.learningapp.data.remote.AuthResponseDto
import com.example.learningapp.data.remote.LoginRequestDto
import com.example.learningapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequestDto): Result<AuthResponseDto> {
        // Add any specific business logic for login if needed
        return repository.login(request)
    }
}