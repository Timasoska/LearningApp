package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SubjectState(
    val subjects: List<Subject> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null, // Ключевое поле для ошибки

    val searchQuery: String = "",

    val searchHistory: List<String> = emptyList(),
    val isSearchBarFocused: Boolean = false,
    val showHistory: Boolean = false
)