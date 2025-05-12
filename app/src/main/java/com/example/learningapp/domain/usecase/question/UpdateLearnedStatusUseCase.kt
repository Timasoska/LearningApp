package com.example.learningapp.domain.usecase.question

import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

// Переименуй класс в соответствии с конвенцией (с большой буквы)
class UpdateLearnedStatusUseCase @Inject constructor( // <<< ПЕРЕИМЕНОВАЛ ИЛИ ИЗМЕНИ СУЩЕСТВУЮЩИЙ
    private val repository: QuestionRepository
) {
    // Теперь UseCase принимает ID и новый boolean статус
    suspend operator fun invoke(questionId: Int, isLearned: Boolean) {
        // Вызываем новый/обновленный метод репозитория
        repository.updateLearnedStatus(questionId, isLearned)
        // Можно добавить обработку исключений здесь или обертку в Result, если нужно
        // Например:
        // try {
        //     repository.updateLearnedStatus(questionId, isLearned)
        //     // return Result.Success(Unit) // если используешь свой Result
        // } catch (e: Exception) {
        //     // return Result.Error(e.message ?: "Failed to update learned status")
        //     throw e // или пробросить дальше
        // }
    }
}