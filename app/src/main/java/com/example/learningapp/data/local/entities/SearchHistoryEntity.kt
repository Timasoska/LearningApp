package com.example.learningapp.data.local.entities

// Файл: app/src/main/java/com/example/learningapp/data/local/entities/SearchHistoryEntity.kt

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_history",
    // Добавляем индекс на термин для быстрого поиска и уникальности
    indices = [Index(value = ["term"], unique = true)]
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Первичный ключ
    val term: String, // Поисковый запрос
    val timestamp: Long // Временная метка для сортировки
)