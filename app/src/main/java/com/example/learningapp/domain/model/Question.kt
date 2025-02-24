package com.example.learningapp.domain.model
/**
 * Модель вопроса для приложения.
 *
 * @property id уникальный идентификатор вопроса.
 * @property title заголовок вопроса.
 * @property answer ответ на вопрос.
 * @property isLearned флаг, указывающий, изучен ли вопрос.
 */

data class Question (
    val id: Int,
    val title: String,
    val answer: String,
    val isLearned: Boolean
)