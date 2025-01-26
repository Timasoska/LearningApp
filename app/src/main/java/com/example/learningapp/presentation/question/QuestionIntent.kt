package com.example.learningapp.presentation.question

sealed class QuestionIntent {
    data class LearnedStatus(val id: Int) : QuestionIntent() // статус изученности вопроса
    data object LoadQuestions : QuestionIntent()
    data class LoadQuestionById(val id: Int) : QuestionIntent()
}