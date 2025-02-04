package com.example.learningapp.presentation.question

import com.example.learningapp.domain.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class QuestionState (
    val questions: Flow<List<Question>> = emptyFlow(),
    val currentQuestion: Question? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)