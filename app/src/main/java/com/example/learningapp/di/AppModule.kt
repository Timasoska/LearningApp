package com.example.learningapp.di

import android.content.Context
import androidx.room.Room
import com.example.learningapp.data.local.QuestionDao
import com.example.learningapp.data.local.QuestionDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule  {
    @Provides
    @Singleton
    fun providesQuestionDao(database: QuestionDataBase) : QuestionDao{
        return database.questionDao()
    }

    @Provides
    @Singleton
    fun providesQuestionDataBase(context: Context): QuestionDataBase{
        return Room.databaseBuilder(
            context,
            QuestionDataBase::class.java,
            "questions_db"
        ).build()
    }

}