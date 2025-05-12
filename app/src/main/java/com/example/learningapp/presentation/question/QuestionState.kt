package com.example.learningapp.presentation.question

import com.example.learningapp.domain.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class QuestionState(
    // Теперь questions это List, а не Flow, так как мы будем загружать его по запросу
    val questions: List<Question> = emptyList(),
    val currentQuestion: Question? = null, // Для экрана деталей/редактирования
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentSubjectIdForList: Int? = null // Храним ID предмета, для которого загружен список
)