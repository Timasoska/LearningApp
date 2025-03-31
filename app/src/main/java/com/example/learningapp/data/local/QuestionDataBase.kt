package com.example.learningapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.learningapp.data.local.dao.QuestionDao
import com.example.learningapp.data.local.dao.SubjectDao
import com.example.learningapp.data.local.entities.QuestionEntity
import com.example.learningapp.data.local.entities.SubjectEntity
import com.example.learningapp.data.local.dao.SearchHistoryDao // <-- Импорт нового DAO
import com.example.learningapp.data.local.entities.SearchHistoryEntity // <-- Импорт новой Entity
@Database(
    version = 4, // <-- УВЕЛИЧИВАЕМ ВЕРСИЮ!
    entities = [
        QuestionEntity::class,
        SubjectEntity::class,
        SearchHistoryEntity::class // <-- Добавляем новую Entity
    ]
    // Если вы не используете fallbackToDestructiveMigration,
    // вам нужно будет определить миграцию с версии 3 на 4.
    // exportSchema = true // Рекомендуется включить для миграций
)
abstract class QuestionDataBase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun subjectDao(): SubjectDao
    abstract fun searchHistoryDao(): SearchHistoryDao // <-- Добавляем метод для DAO
}