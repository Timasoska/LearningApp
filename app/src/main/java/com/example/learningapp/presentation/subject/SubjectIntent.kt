package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject

sealed class SubjectIntent {
    object LoadInitialData : SubjectIntent() // Для первоначальной загрузки предметов и истории
    data class SearchQueryChanged(val query: String) : SubjectIntent()
    data class SearchBarFocusChanged(val isFocused: Boolean) : SubjectIntent()
    data class SubmitSearch(val query: String) : SubjectIntent()
    data class HistoryItemClicked(val term: String) : SubjectIntent()
    object ClearSearchHistory : SubjectIntent()

    data class AddNewSubject(val name: String) : SubjectIntent() // Изменил с AddSubject, чтобы не путать с UseCase
    data class UpdateExistingSubject(val subject: Subject) : SubjectIntent() // <<< НОВЫЙ ВАРИАНТ
    data class DeleteExistingSubject(val id: Int) : SubjectIntent() // Аналогично
    // data class LoadSubjectById(val id: Int) : SubjectIntent() // Если нужен
}