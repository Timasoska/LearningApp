package com.example.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.learningapp.data.local.entities.SubjectEntity
import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity) : Long

    @Query("SELECT * FROM subjects")
    suspend fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Int): SubjectEntity

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubject(id: Int)

}