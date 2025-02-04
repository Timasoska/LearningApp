package com.example.learningapp.domain.usecase.association

import com.example.learningapp.domain.model.Association
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class AddAssociationUseCase @Inject constructor(
    private val repository: QuestionRepository
){
    suspend operator fun invoke(association: Association) : Long{
        return repository.addAssociation(association)
    }
}