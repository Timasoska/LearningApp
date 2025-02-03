package com.example.learningapp.domain.model

data class Statistics (
    val id: Int,
    val questionId: Int,
    val subjectId: Int,
    val attempts: Int,
    val correctAnswers: Int
)