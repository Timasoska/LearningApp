package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow


data class SubjectState (
    val error: String? = null,
    val isLoading: Boolean = false,
    val currentSubject: Subject? = null, // Для деталей
    val searchQuery: String = "",
    val filteredSubjects: List<Subject> = emptyList(), // Результат фильтрации

    // --- Поля для истории (ВОЗВРАЩАЕМ) ---
    val searchHistory: List<String> = emptyList(),
    val isSearchBarFocused: Boolean = false,
    val showHistory: Boolean = false // Показывать ли список истории
)