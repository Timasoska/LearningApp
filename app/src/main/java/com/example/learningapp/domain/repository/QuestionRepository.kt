package com.example.learningapp.domain.repository

import com.example.learningapp.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun getQuestionById(id: Int) : Question
    fun getAllQuestions() : Flow<List<Question>>
    suspend fun learnedQuestion(id: Int)
}