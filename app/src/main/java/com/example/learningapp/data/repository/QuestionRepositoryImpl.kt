package com.example.learningapp.data.repository

import com.example.learningapp.data.local.QuestionDao
import com.example.learningapp.data.local.mappers.toDomain
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val dao: QuestionDao
) : QuestionRepository {
    override suspend fun getQuestionById(id: Int): Question {
        return dao.getQuestionById(id = id).toDomain()
    }

    override fun getAllQuestions(): Flow<List<Question>> {
        return dao.getAllQuestions()
            .map { entities -> entities.map {it.toDomain()} } //Первый .map — оператор Flow, обрабатывает каждый элемент потока, entities.map — преобразует каждый QuestionEntity в списке в Question
        }

    override suspend fun learnedQuestion(id: Int) {
        val question = dao.getQuestionById(id)
        dao.updateQuestion(question.copy(isLearned = !question.isLearned))
    }

}