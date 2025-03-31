package com.example.learningapp.domain.usecase



import com.example.learningapp.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class ClearSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository // Зависимость от репозитория
) {
    // Этот UseCase suspend, так как repository.clearHistory() - suspend
    suspend operator fun invoke() {
        repository.clearHistory() // Вызываем метод репозитория
    }
}