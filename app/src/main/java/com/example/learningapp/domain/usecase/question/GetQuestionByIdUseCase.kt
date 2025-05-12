package com.example.learningapp.domain.usecase.question

import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.repository.QuestionRepository
import javax.inject.Inject

class getQuestionByIdUseCase @Inject constructor( // Имя класса с маленькой буквы - не стандарт, лучше GetQuestionByIdUseCase
    private val repository: QuestionRepository
) {
    // Вариант 1: UseCase возвращает Question? (совпадает с репозиторием)
    suspend operator fun invoke(id: Int): Question? {
        return repository.getQuestionById(id) // Репозиторий уже возвращает Question?
    }
}