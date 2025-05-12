package com.example.learningapp.domain.repository

import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    // Subjects
    fun getAllSubjects(): Flow<List<Subject>>
    suspend fun addSubject(name: String): Long
    suspend fun getSubjectById(id: Int): Subject?
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(id: Int)

    // Questions
    suspend fun getQuestionsBySubject(subjectId: Int): List<Question> // Возвращает List для разовой загрузки
    suspend fun addQuestion(question: Question): Long
    suspend fun getQuestionById(id: Int): Question?
    fun getAllQuestions(): Flow<List<Question>> // Если все еще нужен
    suspend fun deleteQuestion(id: Int) // Потребует subjectId для обновления списка в VM
    suspend fun updateQuestion(newQuestion: Question)
    suspend fun updateLearnedStatus(questionId: Int, isLearned: Boolean) // Новый/обновленный метод
    // suspend fun learnedQuestion(id: Int) // Старый можно удалить
}