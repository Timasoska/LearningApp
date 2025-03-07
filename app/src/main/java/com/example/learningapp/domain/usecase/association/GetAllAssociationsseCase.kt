package com.example.learningapp.domain.usecase.association

import com.example.learningapp.domain.model.Association
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAssociationsUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    fun getAllAssociations(): Flow<Map<Int, List<Association>>> {
        return repository.getAllAssociations()
    }
}
