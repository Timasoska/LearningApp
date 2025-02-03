package com.example.learningapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "associations")
data class AssociationEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val questionId: Int,
    val association: String
)