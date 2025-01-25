package com.example.learningapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun geQuestionById (id: Int): QuestionEntity

    @Update
    suspend fun updateQuestion(question: QuestionEntity)
}