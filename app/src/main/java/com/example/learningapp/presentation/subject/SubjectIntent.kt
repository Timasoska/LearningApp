package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject

sealed class SubjectIntent {
    data object LoadSubject : SubjectIntent()
    data class AddSubject(val subject: Subject) : SubjectIntent()
    data class DeleteSubject(val id: Int) : SubjectIntent()
    data class LoadSubjectById(val id: Int) : SubjectIntent()
}