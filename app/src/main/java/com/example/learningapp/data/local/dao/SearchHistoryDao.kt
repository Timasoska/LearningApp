package com.example.learningapp.data.local.dao

// Файл: app/src/main/java/com/example/learningapp/data/local/dao/SearchHistoryDao.kt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.learningapp.data.local.entities.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    /**
     * Возвращает Flow со списком последних 10 поисковых запросов,
     * отсортированных по времени (самые новые - первые).
     */
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentHistory(): Flow<List<SearchHistoryEntity>>

    /**
     * Вставляет новый термин или обновляет timestamp существующего.
     * Использует REPLACE: если термин уже есть (из-за unique index),
     * старая запись будет заменена новой (с новым timestamp).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTerm(searchHistoryEntity: SearchHistoryEntity)

    /**
     * Удаляет самые старые записи, если их больше 10.
     * Вызывается после вставки нового элемента.
     */
    @Query("DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY timestamp DESC LIMIT 10)")
    suspend fun trimHistory()

    /**
     * Полностью очищает таблицу истории поиска.
     */
    @Query("DELETE FROM search_history")
    suspend fun clearHistory()

    /**
     * (Опционально) Находит запись по термину для обновления timestamp.
     * Не используется с OnConflictStrategy.REPLACE, но может быть полезно
     * для других стратегий.
     */
    // @Query("SELECT * FROM search_history WHERE term = :term LIMIT 1")
    // suspend fun findByTerm(term: String): SearchHistoryEntity?
}