package com.example.learningapp.domain.usecase.question

import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class getAllQuestionsUseCase @Inject constructor(
    private val repository: QuestionRepository
){

    operator fun invoke() : Flow<List<Question>>{
        return repository.getAllQuestions()
    }

}