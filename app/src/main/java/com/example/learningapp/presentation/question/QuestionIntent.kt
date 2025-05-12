package com.example.learningapp.presentation.question

import com.example.learningapp.domain.model.Question


sealed class QuestionIntent {
    data class LoadQuestionBySubject(val subjectId: Int) : QuestionIntent()
    data class LoadQuestionById(val id: Int) : QuestionIntent() // Для экрана деталей/редактирования
    data class AddQuestion(val question: Question) : QuestionIntent() // question содержит subjectId
    data class UpdateQuestion(val newQuestion: Question) : QuestionIntent()
    // Для DeleteQuestion лучше передавать subjectId, чтобы знать, какой список обновлять
    data class DeleteQuestion(val questionId: Int, val subjectId: Int) : QuestionIntent()
    data class ToggleLearnedStatus(val questionId: Int, val newStatus: Boolean, val subjectIdForReload: Int) : QuestionIntent()
    object LoadQuestions : QuestionIntent() // Если нужен общий список всех вопросов (менее вероятно для этого приложения)
}