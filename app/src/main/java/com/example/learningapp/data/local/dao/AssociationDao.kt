package com.example.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.learningapp.data.local.entities.AssociationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssociationDao {
    @Query("SELECT * FROM associations")
    fun getAllAssociations(): Flow<List<AssociationEntity>>

    @Query("SELECT * FROM associations WHERE id = :id")
    fun getAssociationsById (id: Int): AssociationEntity

    @Update
    suspend fun updateAssociation(association: AssociationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssociation(association: AssociationEntity): Long

    @Query("DELETE FROM associations WHERE id = :id")
    suspend fun deleteAssociationById(id: Int)

}