package com.example.learningapp.domain.usecase.question

import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class getQuestionByIdUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(id: Int) : Question {
        return repository.getQuestionById(id)
    }
}