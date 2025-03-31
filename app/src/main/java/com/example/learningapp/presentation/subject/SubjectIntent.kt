package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject


sealed class SubjectIntent {
    object LoadSubjects : SubjectIntent() // Может быть для явного рефреша
    data class AddSubject(val name: String) : SubjectIntent()
    data class DeleteSubject(val id: Int) : SubjectIntent()
    data class LoadSubjectById(val id: Int) : SubjectIntent()
    data class UpdateSubject(val subject: Subject) : SubjectIntent()
    data class SearchQueryChanged(val query: String) : SubjectIntent()
}