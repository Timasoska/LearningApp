package com.example.learningapp.presentation.subject


sealed class SubjectIntent {
    object LoadSubject : SubjectIntent()
    data class AddSubject(val name: String) : SubjectIntent()
    data class DeleteSubject(val id: Int) : SubjectIntent()
    data class LoadSubjectById(val id: Int) : SubjectIntent()
}