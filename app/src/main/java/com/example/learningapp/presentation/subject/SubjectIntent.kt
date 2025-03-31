package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject


sealed class SubjectIntent {
    // Существующие
    object LoadSubjects : SubjectIntent()
    data class AddSubject(val name: String) : SubjectIntent()
    data class DeleteSubject(val id: Int) : SubjectIntent()
    data class LoadSubjectById(val id: Int) : SubjectIntent()
    data class UpdateSubject(val subject: Subject) : SubjectIntent()
    data class SearchQueryChanged(val query: String) : SubjectIntent()

    // --- Интенты истории (ВОЗВРАЩАЕМ) ---
    data class SearchBarFocusChanged(val isFocused: Boolean) : SubjectIntent()
    data class SubmitSearch(val query: String) : SubjectIntent() // Для добавления в историю
    data class HistoryItemClicked(val term: String) : SubjectIntent()
    object ClearSearchHistory : SubjectIntent()
}