package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class SubjectState (
    val error: String? = null,
    val isLoading: Boolean = false, // Для индикатора загрузки/фильтрации
    // --- Поля из исходного состояния ---
    val subjects: Flow<List<Subject>> = emptyFlow(), // Храним сам Flow
    val currentSubject: Subject? = null, // Для детального экрана, не используется в списке
    // --- Поля для поиска ---
    val searchQuery: String = "", // Текущий поисковый запрос
    val filteredSubjects: List<Subject> = emptyList() // Отображаемый (отфильтрованный) список
)