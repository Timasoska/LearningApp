package com.example.learningapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [QuestionEntity::class])
abstract class QuestionDataBase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

}