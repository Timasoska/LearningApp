package com.example.learningapp.domain.repository


import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для управления историей поиска.
 * Определяет операции, не зависящие от способа хранения (SharedPreferences или Room).
 */
interface SearchHistoryRepository {

    /**
     * Возвращает Flow, который эмитит текущий список истории поиска (до 10 элементов).
     * Новые элементы находятся в начале списка.
     */
    fun getHistory(): Flow<List<String>>

    /**
     * Добавляет новый поисковый термин в историю.
     * Если термин уже существует, он перемещается в начало (или его время обновляется).
     * Список ограничивается по максимальному размеру (например, 10).
     * @param term Поисковый термин для добавления.
     */
    suspend fun addTerm(term: String)

    /**
     * Полностью очищает историю поиска.
     */
    suspend fun clearHistory()
}