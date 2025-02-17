package com.example.learningapp.presentation.subject

import com.example.learningapp.domain.model.Subject


sealed class SubjectIntent {
    object LoadSubject : SubjectIntent()
    data class AddSubject(val name: String) : SubjectIntent()
    data class DeleteSubject(val id: Int) : SubjectIntent()
    data class LoadSubjectById(val id: Int) : SubjectIntent()
    data class UpdateSubject(val subject: Subject) : SubjectIntent() // обновление предмета
}