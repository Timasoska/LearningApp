package com.example.learningapp.domain.usecase.statistics

import com.example.learningapp.domain.model.Statistics
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class UpdateStatisticsUseCase @Inject constructor(
    private val repository: QuestionRepository
){
    suspend operator fun invoke(questionId: Int, isCorrect: Boolean){
        return repository.updateStatistics(questionId,isCorrect)
    }
}