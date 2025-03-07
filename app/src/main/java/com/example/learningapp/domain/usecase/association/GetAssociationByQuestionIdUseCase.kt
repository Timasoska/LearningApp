package com.example.learningapp.domain.usecase.association

import com.example.learningapp.domain.model.Association
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAssociationsByQuestionIdUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    operator fun invoke(questionId: Int): Flow<List<Association>> {
        return repository.getAssociationsByQuestionId(questionId)
    }
}