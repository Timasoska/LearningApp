package com.example.learningapp.domain.usecase

import androidx.room.Dao
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class getQuestionByIdUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(id: Int) : Question {
        return repository.getQuestionById(id)
    }
}