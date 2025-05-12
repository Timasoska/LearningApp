package com.example.learningapp.domain.usecase.subject

import com.example.learningapp.domain.repository.QuestionRepository
// ВАЖНО: Импортируй свой Result класс!
import com.example.learningapp.presentation.authorization.Result // Или com.example.learningapp.domain.util.Result, если создашь его там
import javax.inject.Inject

class AddSubjectUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    suspend operator fun invoke(name: String): Result<Long> { // Явно указываем Result<Long>
        if (name.isBlank()) {
            // Для твоего Result:
            return Result.Error("Subject name cannot be blank.")
        }
        val resultId = repository.addSubject(name) // Предполагаем, что репозиторий возвращает Long или кидает Exception

        // Если repository.addSubject может вернуть -1L при ошибке на сервере:
        return if (resultId != -1L) {
            Result.Success(resultId) // Для твоего Result
        } else {
            Result.Error("Failed to add subject to the server.") // Для твоего Result
        }
        // Если repository.addSubject кидает Exception при ошибке:
        /*
        try {
            val resultId = repository.addSubject(name)
            return Result.Success(resultId)
        } catch (e: Exception) {
            return Result.Error(e.message ?: "Failed to add subject")
        }
        */
    }
}