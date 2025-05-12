package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SubjectState(
    val subjects: List<Subject> = emptyList(), // Основной список для отображения (загруженный)
    val isLoading: Boolean = false,
    val error: String? = null, // Общая ошибка для экрана
    // val currentSubject: Subject? = null, // Если есть экран деталей предмета

    val searchQuery: String = "",
    // filteredSubjects теперь не нужен в State, если фильтрация происходит при обновлении subjects
    // или если мы хотим показывать _fullSubjectList и фильтровать в Composable (менее MVI-шно)
    // Лучше, если ViewModel готовит уже отфильтрованный список для отображения в `subjects`

    val searchHistory: List<String> = emptyList(),
    val isSearchBarFocused: Boolean = false,
    val showHistory: Boolean = false
)