package com.example.learningapp.domain.usecase.subject

import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class DeleteSubjectUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(id: Int){
        return repository.deleteSubject(id)
    }
}