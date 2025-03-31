package com.example.learningapp.data.repository


import com.example.learningapp.data.local.dao.SearchHistoryDao
import com.example.learningapp.data.local.entities.SearchHistoryEntity
import com.example.learningapp.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImplRoom @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao // <-- Зависимость от DAO
) : SearchHistoryRepository { // <-- Реализуем тот же интерфейс

    /**
     * Получаем Flow<List<SearchHistoryEntity>> из DAO и преобразуем его
     * в Flow<List<String>>.
     */
    override fun getHistory(): Flow<List<String>> {
        return searchHistoryDao.getRecentHistory().map { entityList ->
            entityList.map { it.term } // Преобразуем каждую Entity в String (термин)
        }
    }

    /**
     * Добавляем термин. Используем insertOrUpdateTerm и trimHistory из DAO.
     */
    override suspend fun addTerm(term: String) {
        val trimmedTerm = term.trim()
        if (trimmedTerm.isBlank()) return

        // Выполняем операции с БД в IO потоке
        withContext(Dispatchers.IO) {
            // Создаем Entity с текущим временем
            val entity = SearchHistoryEntity(
                term = trimmedTerm,
                timestamp = System.currentTimeMillis()
            )
            // Вставляем или обновляем (DAO сам обработает конфликт)
            searchHistoryDao.insertOrUpdateTerm(entity)
            // Удаляем старые записи, если их стало больше 10
            searchHistoryDao.trimHistory()
        }
    }

    /**
     * Очищаем историю, вызывая метод DAO.
     */
    override suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            searchHistoryDao.clearHistory()
        }
    }
}