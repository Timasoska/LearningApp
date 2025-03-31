package com.example.learningapp.domain.usecase

import com.example.learningapp.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class AddSearchTermUseCase @Inject constructor(
    private val repository: SearchHistoryRepository // Зависимость от репозитория
) {
    // Этот UseCase suspend, так как repository.addTerm() - suspend
    suspend operator fun invoke(term: String) {
        repository.addTerm(term) // Вызываем метод репозитория
    }
}