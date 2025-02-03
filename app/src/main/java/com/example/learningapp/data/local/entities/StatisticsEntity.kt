package com.example.learningapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "statistics")
data class StatisticsEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val questionId: Int,
    val subjectId: Int,
    val attempts: Int = 0,
    val correctAnswers: Int = 0
)