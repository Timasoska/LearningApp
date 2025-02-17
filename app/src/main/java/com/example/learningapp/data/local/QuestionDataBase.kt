package com.example.learningapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.learningapp.data.local.dao.QuestionDao
import com.example.learningapp.data.local.dao.SubjectDao
import com.example.learningapp.data.local.entities.QuestionEntity
import com.example.learningapp.data.local.entities.SubjectEntity

@Database(
    version = 3,
    entities = [QuestionEntity::class, SubjectEntity::class]
)
abstract class QuestionDataBase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun subjectDao(): SubjectDao
}