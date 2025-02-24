package com.example.learningapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность вопроса, используемая для хранения информации о вопросах в базе данных.
 *
 * @property id Уникальный идентификатор вопроса.
 * @property title Заголовок вопроса.
 * @property answer Ответ на вопрос.
 * @property isLearned Флаг, указывающий, был ли вопрос изучен.
 */
@Entity (tableName = "questions")
data class QuestionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val answer: String,
    val isLearned: Boolean = false
)