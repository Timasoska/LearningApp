package com.example.learningapp.domain.usecase.association

import com.example.learningapp.domain.model.Association
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class UpdateAssociationUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(newAssociation: Association){
        return repository.updateAssociation(newAssociation)
    }
}