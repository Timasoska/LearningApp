package com.example.learningapp.data.repository

import com.example.learningapp.data.local.dao.AssociationDao
import com.example.learningapp.data.local.dao.QuestionDao
import com.example.learningapp.data.local.dao.StatisticsDao
import com.example.learningapp.data.local.dao.SubjectDao
import com.example.learningapp.data.local.mappers.toDomain
import com.example.learningapp.domain.model.Association
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questiondao: QuestionDao,
    private val associationDao: AssociationDao,
    private val statisticsDao: StatisticsDao,
    private val subjectDao: SubjectDao
) : QuestionRepository {
    override suspend fun getQuestionById(id: Int): Question {
        return questiondao.getQuestionById(id = id).toDomain()
    }

    override fun getAllQuestions(): Flow<List<Question>> {
        return questiondao.getAllQuestions()
            .map { entities -> entities.map {it.toDomain()} } //Первый .map — оператор Flow, обрабатывает каждый элемент потока, entities.map — преобразует каждый QuestionEntity в списке в Question
        }

    override suspend fun addQuestion(question: Question): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteQuestion(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateQuestion(newQuestion: Question) {
        TODO("Not yet implemented")
    }

    override suspend fun learnedQuestion(id: Int) {
    }

    override suspend fun getAllSubjects(): Flow<List<Subject>> {
        return
    }

    override suspend fun getSubjectById(id: Int): Subject {
        TODO("Not yet implemented")
    }

    override suspend fun addSubject(subject: Subject): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSubject(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addAssociation(association: Association): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAssociation(id: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateAssociation(association: Association) {
        TODO("Not yet implemented")
    }

    override suspend fun updateStatistics(questionId: Int, isCorrect: Boolean) {
        TODO("Not yet implemented")
    }


}