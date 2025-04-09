package com.example.learningapp.domain.usecase.search

// Файл: app/src/main/java/com/example/learningapp/domain/usecase/search/GetSearchHistoryUseCase.kt
import com.example.learningapp.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository // Зависимость от репозитория
) {
    // Оператор invoke позволяет вызывать UseCase как функцию
    operator fun invoke(): Flow<List<String>> {
        // Просто вызываем метод репозитория
        // Этот UseCase не suspend, так как repository.getHistory() возвращает Flow
        return repository.getHistory()
    }
}