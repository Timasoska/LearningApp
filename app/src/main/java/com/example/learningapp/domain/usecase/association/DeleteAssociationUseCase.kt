package com.example.learningapp.domain.usecase.association

import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class DeleteAssociationUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(id: Int){
        return repository.deleteAssociation(id)
    }
}