package com.example.learningapp.domain.usecase

import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class learnedQuestionUseCase @Inject constructor(
    private val repository: QuestionRepository
) {

    suspend operator fun invoke(id: Int){
        return repository.learnedQuestion(id)
    }

}