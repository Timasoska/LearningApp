package com.example.learningapp.domain.usecase.subject

import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class UpdateSubjectUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(subject: Subject) {
        repository.updateSubject(subject)
    }
}
