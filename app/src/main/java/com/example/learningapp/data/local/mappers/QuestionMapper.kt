package com.example.learningapp.data.local.mappers

import com.example.learningapp.data.local.entities.QuestionEntity
import com.example.learningapp.domain.model.Question

fun QuestionEntity.toDomain() = Question(
    id = id,
    title = title,
    answer = answer,
    isLearned = isLearned,
    subjectId = subjectId
)

fun Question.toEntity() = QuestionEntity(
    id = id,
    title = title,
    answer = answer,
    isLearned = isLearned,
    subjectId = subjectId
)