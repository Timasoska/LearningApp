package com.example.learningapp.data.repository

import com.example.learningapp.data.local.dao.AssociationDao
import com.example.learningapp.data.local.dao.QuestionDao
import com.example.learningapp.data.local.dao.StatisticsDao
import com.example.learningapp.data.local.dao.SubjectDao
import com.example.learningapp.data.local.entities.StatisticsEntity
import com.example.learningapp.data.local.entities.SubjectEntity
import com.example.learningapp.data.local.mappers.toDomain
import com.example.learningapp.data.local.mappers.toEntity
import com.example.learningapp.domain.model.Association
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val associationDao: AssociationDao,
    private val statisticsDao: StatisticsDao,
    private val subjectDao: SubjectDao
) : QuestionRepository {
    override suspend fun getQuestionById(id: Int): Question {
        return questionDao.getQuestionById(id = id).toDomain()
    }

    override fun getAllQuestions(): Flow<List<Question>> {
        return questionDao.getAllQuestions()
            .map { entities -> entities.map {it.toDomain()} } //Первый .map — оператор Flow, обрабатывает каждый элемент потока, entities.map — преобразует каждый QuestionEntity в списке в Question
        }

    override suspend fun addQuestion(question: Question): Long {
        return questionDao.insertQuestion(question.toEntity())
    }

    override suspend fun deleteQuestion(id: Int) {
        return questionDao.deleteQuestion(id)
    }

    override suspend fun updateQuestion(newQuestion: Question) {
        return questionDao.updateQuestion(newQuestion.toEntity())
    }

    override suspend fun learnedQuestion(id: Int) {
        val question = questionDao.getQuestionById(id)
        questionDao.updateQuestion(question.copy(isLearned = !question.isLearned))
    }

    override suspend fun getQuestionsBySubject(subjectId: Int): List<Question> {
        return questionDao.getQuestionsBySubject(subjectId).map { it.toDomain() }
    }

    override suspend fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects().map { it.map { it.toDomain() } }
    }

    override suspend fun getSubjectById(id: Int): Subject {
        return subjectDao.getSubjectById(id).toDomain()
    }

    override suspend fun addSubject(name: String): Long {
        val subjectModel = Subject(
            id = 0, //ID автоматически сгенерируется БД
             name = name
        )
        return subjectDao.insertSubject(subjectModel.toEntity())
    }

    override suspend fun deleteSubject(id: Int) {
        return subjectDao.deleteSubject(id)
    }

    override suspend fun updateSubject(subject: Subject) {
        subjectDao.insertSubject(subject.toEntity())
    }

    override suspend fun addAssociation(association: Association): Long {
        return associationDao.insertAssociation(association.toEntity())
    }

    override suspend fun deleteAssociation(id: Int) {
        return associationDao.deleteAssociationById(id)
    }

    override suspend fun updateAssociation(association: Association) {
        return associationDao.updateAssociation(association.toEntity())
    }

    override suspend fun updateStatistics(statistics: StatisticsEntity) {
        return statisticsDao.updateStatistics(statistics)
    }


}