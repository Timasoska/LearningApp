package com.example.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.learningapp.data.local.entities.QuestionEntity
import com.example.learningapp.domain.model.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById (id: Int): QuestionEntity

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity) : Long

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestion(id: Int)

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId")
    suspend fun getQuestionsBySubject(subjectId: Int): List<QuestionEntity>
}