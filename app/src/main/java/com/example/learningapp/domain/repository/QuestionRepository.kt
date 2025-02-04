package com.example.learningapp.domain.repository

import com.example.learningapp.domain.model.Association
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {

    //Вопросы
    suspend fun getQuestionById(id: Int): Question
    fun getAllQuestions(): Flow<List<Question>>
    suspend fun addQuestion(question: Question): Int
    suspend fun deleteQuestion(id: Int)
    suspend fun updateQuestion(newQuestion: Question)
    suspend fun learnedQuestion(id: Int) //Статус изученности

    //Предметы
    suspend fun getAllSubjects(): Flow<List<Subject>>
    suspend fun getSubjectById(id: Int): Subject
    suspend fun addSubject(subject: Subject): Int
    suspend fun deleteSubject(id: Int)


    //Ассоциации
    suspend fun addAssociation(association: Association): Int
    suspend fun deleteAssociation(id: Int)
    suspend fun updateAssociation(association: Association)

    //Статистика
    suspend fun updateStatistics(questionId: Int, isCorrect: Boolean)


}