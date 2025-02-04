package com.example.learningapp.domain.usecase.question

import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class DeleteQuestionUseCase @Inject constructor(
    private val repository: QuestionRepository
){

    suspend operator fun invoke(id: Int){
        return repository.deleteQuestion(id)
    }

}