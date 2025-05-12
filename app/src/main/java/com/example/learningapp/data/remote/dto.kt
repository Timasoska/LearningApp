package com.example.learningapp.data.remote

import kotlinx.serialization.Serializable

// --- Subjects DTOs ---
@Serializable
data class SubjectResponseDto( // Для получения данных от сервера
    val id: Int,
    val name: String,
    val userId: Int // Сервер возвращает userId, хоть он и не нужен напрямую в доменной модели клиента
)

@Serializable
data class SubjectRequestDto( // Для отправки данных на сервер (создание/обновление)
    val name: String
)

// --- Questions DTOs ---
@Serializable
data class QuestionResponseDto( // Для получения данных от сервера
    val id: Int,
    val title: String,
    val answer: String,
    val isLearned: Boolean,
    val subjectId: Int
)

@Serializable
data class QuestionRequestDto( // Для отправки данных на сервер (создание/обновление)
    val title: String,
    val answer: String,
    val isLearned: Boolean? = null // Опционально при создании, если на сервере есть default
)

@Serializable
data class LearnedStatusUpdateRequestDto( // Для PATCH запроса на обновление статуса изученности
    val isLearned: Boolean
)

// Может понадобиться для ответа сервера на простые операции типа PUT/DELETE/PATCH,
// если сервер возвращает что-то вроде {"message": "Success"}
@Serializable
data class SimpleMessageResponseDto(
    val message: String
)