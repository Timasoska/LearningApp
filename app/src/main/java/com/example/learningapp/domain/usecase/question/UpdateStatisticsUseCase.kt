package com.example.learningapp.domain.usecase.question

import com.example.learningapp.data.local.entities.StatisticsEntity
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class UpdateStatisticsUseCase @Inject constructor(
    private val repository: QuestionRepository
){
    suspend operator fun invoke(statisticsEntity: StatisticsEntity){
        return repository.updateStatistics(statisticsEntity)
    }
}