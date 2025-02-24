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
/**
 * DAO для доступа к данным вопросов в базе данных.
 *
 * Предоставляет методы для получения, добавления, обновления и удаления вопросов.
 */
@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    /**
     * Возвращает вопрос по его идентификатору.
     *
     * @param id Идентификатор вопроса.
     * @return Экземпляр [QuestionEntity] для данного идентификатора.
     */

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById (id: Int): QuestionEntity

    /**
     * Обновляет данные вопроса в базе данных.
     *
     * @param question Объект [QuestionEntity] с обновлёнными данными.
     */

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    /**
     * Вставляет новый вопрос в базу данных.
     *
     * @param question Объект [QuestionEntity] для вставки.
     * @return Идентификатор вставленного вопроса.
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity) : Long

    /**
     * Удаляет вопрос по его идентификатору.
     *
     * @param id Идентификатор вопроса, который необходимо удалить.
     */

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestion(id: Int)
}