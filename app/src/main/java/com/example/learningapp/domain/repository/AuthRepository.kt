package com.example.learningapp.domain.repository

import com.example.learningapp.data.remote.AuthResponseDto
import com.example.learningapp.data.remote.LoginRequestDto
import com.example.learningapp.data.remote.RegisterRequestDto

interface AuthRepository {
    suspend fun register(request: RegisterRequestDto): Result<AuthResponseDto>
    suspend fun login(request: LoginRequestDto): Result<AuthResponseDto>
}