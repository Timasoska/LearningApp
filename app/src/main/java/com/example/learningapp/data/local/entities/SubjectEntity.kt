package com.example.learningapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)