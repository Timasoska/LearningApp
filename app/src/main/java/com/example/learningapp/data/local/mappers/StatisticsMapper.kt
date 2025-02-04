package com.example.learningapp.data.local.mappers

import com.example.learningapp.data.local.entities.StatisticsEntity
import com.example.learningapp.domain.model.Statistics

fun StatisticsEntity.tpDomain() = Statistics(
    id = id,
    questionId = questionId,
    subjectId = subjectId,
    attempts = attempts,
    correctAnswers = correctAnswers
)

fun Statistics.toEntity() = StatisticsEntity(
    id = id,
    questionId = questionId,
    subjectId = subjectId,
    attempts = attempts,
    correctAnswers = correctAnswers
)