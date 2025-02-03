package com.example.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.learningapp.data.local.entities.StatisticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistics(statistics: StatisticsEntity): Int

    @Query("SELECT * FROM statistics WHERE questionId = :questionId")
    suspend fun getStatisticsByQuestionId(questionId: Int): Flow<List<StatisticsEntity>>

    @Query("SELECT * FROM statistics WHERE subjectId = :subjectId")
    suspend fun getStatisticsBySubjectId(subjectId: Int): Flow<List<StatisticsEntity>>

    @Update
    suspend fun updateStatistics(statistics: StatisticsEntity)

}