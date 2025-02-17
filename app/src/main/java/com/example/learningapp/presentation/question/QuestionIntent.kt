package com.example.learningapp.presentation.question

import com.example.learningapp.domain.model.Question

sealed class QuestionIntent {
    data class LearnedStatus(val id: Int) : QuestionIntent() // статус изученности вопроса
    data object LoadQuestions : QuestionIntent()
    data class LoadQuestionBySubject(val subjectId: Int) : QuestionIntent()
    data class LoadQuestionById(val id: Int) : QuestionIntent()
    data class AddQuestion(val question: Question) : QuestionIntent()
    data class DeleteQuestion(val id: Int) : QuestionIntent()
    data class AddAssociation(val association: Association) : QuestionIntent()
    data class DeleteAssociation(val id: Int) : QuestionIntent()
    data class UpdateAssociation(val association: Association) : QuestionIntent()
    data class UpdateStatistics(val statisticsEntity: StatisticsEntity) : QuestionIntent()
    data class UpdateQuestion(val newQuestion: Question) : QuestionIntent()
}