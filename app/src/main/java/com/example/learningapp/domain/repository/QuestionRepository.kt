package com.example.learningapp.domain.repository

import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {

    //Вопросы
    suspend fun getQuestionById(id: Int): Question
    fun getAllQuestions(): Flow<List<Question>>
    suspend fun addQuestion(question: Question): Long
    suspend fun deleteQuestion(id: Int)
    suspend fun updateQuestion(newQuestion: Question)
    suspend fun learnedQuestion(id: Int) //Статус изученности
    suspend fun getQuestionsBySubject(subjectId: Int): List<Question> // Добавляем метод


    //Предметы
    suspend fun getAllSubjects(): Flow<List<Subject>>
    suspend fun getSubjectById(id: Int): Subject
    suspend fun addSubject(name: String): Long
    suspend fun deleteSubject(id: Int)
    suspend fun updateSubject(subject: Subject)



}