package com.example.learningapp.presentation.question

import com.example.learningapp.domain.model.Question

data class QuestionState (
    val questions: List<Question> = emptyList(),
    val currentQuestion: Question? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)