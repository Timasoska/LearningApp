package com.example.learningapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "questions")
data class QuestionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val answer: String,
    val isLearned: Boolean = false
)