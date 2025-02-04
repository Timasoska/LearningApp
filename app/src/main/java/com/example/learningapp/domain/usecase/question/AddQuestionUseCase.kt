package com.example.learningapp.domain.usecase.question

import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class AddQuestionUseCase @Inject constructor(
    private val repository: QuestionRepository
) {

    suspend operator fun invoke(question: Question) : Long{
        return repository.addQuestion(question)
    }

}