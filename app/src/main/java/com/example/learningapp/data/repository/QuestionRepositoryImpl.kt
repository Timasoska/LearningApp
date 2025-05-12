package com.example.learningapp.data.repository

import com.example.learningapp.di.SessionManager
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import io.ktor.client.* // Импортируем HttpClient
import kotlinx.coroutines.flow.flowOf
import android.util.Log // Для логирования
import com.example.Constants // Твой файл с BASE_URL
import com.example.learningapp.data.remote.SubjectRequestDto
import com.example.learningapp.data.remote.SubjectResponseDto
import com.example.learningapp.data.remote.toDomainModel
import io.ktor.client.call.* // Для body()
import io.ktor.client.request.* // Для parameter()
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.flow // Для создания Flow


class QuestionRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : QuestionRepository {

    override fun getAllSubjects(): Flow<List<Subject>> = flow {
        // emit(Result.Loading) // В MVI часто используют sealed class Result для состояний
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) {
            Log.w("QuestionRepoImpl", "getAllSubjects: User not logged in.")
            emit(emptyList()) // Или emit(Result.Error("User not logged in"))
            return@flow
        }
        try {
            Log.d("QuestionRepoImpl", "getAllSubjects: Fetching for userId: $userId")
            val dtoList: List<SubjectResponseDto> = client.get("${Constants.BASE_URL}/subjects") {
                parameter("userId", userId)
            }.body()
            // emit(Result.Success(dtoList.map { it.toDomainModel() }))
            emit(dtoList.map { it.toDomainModel() }) // Пока просто список
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "getAllSubjects: Error for user $userId: ${e.message}", e)
            // emit(Result.Error(e.message ?: "Unknown error"))
            emit(emptyList()) // Пока просто пустой список при ошибке
        }
    }
    // ... остальные методы репозитория будут suspend fun, возвращающие Result<T> или просто T/Boolean ...
    // (заглушки пока оставляем)
    override suspend fun addSubject(name: String): Long {
        val userId = sessionManager.getCurrentUserId() ?: return -1L
        try {
            val requestDto = SubjectRequestDto(name = name)
            val responseDto: SubjectResponseDto = client.post("${Constants.BASE_URL}/subjects") {
                parameter("userId", userId)
                contentType(ContentType.Application.Json)
                setBody(requestDto)
            }.body()
            return responseDto.id.toLong()
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "Error adding subject '$name' for user $userId: ${e.message}", e)
            // В MVI здесь бы вернули Result.Error
            return -1L
        }
    }
    // ... (реализация остальных CRUD методов для репозитория по аналогии,
    // они будут вызываться из UseCase'ов и могут возвращать Result<T> или кидать исключения)
    // ЗАПОЛНИМ ОСТАЛЬНЫЕ ЗАГЛУШКИ ПОЗЖЕ, КОГДА БУДУТ НУЖНЫ СООТВЕТСТВУЮЩИЕ INTENT'Ы
    override suspend fun getSubjectById(id: Int): Subject? { /*...*/ return null }
    override suspend fun updateSubject(subject: Subject) { /*...*/ }
    override suspend fun deleteSubject(id: Int) { /*...*/ }
    override suspend fun getQuestionsBySubject(subjectId: Int): List<Question> { /*...*/ return emptyList() }
    override suspend fun addQuestion(question: Question): Long { /*...*/ return -1L }
    override suspend fun getQuestionById(id: Int): Question? { /*...*/ return null }
    override fun getAllQuestions(): Flow<List<Question>> { /*...*/ return flowOf(emptyList()) }
    override suspend fun deleteQuestion(id: Int) { /*...*/ }
    override suspend fun updateQuestion(newQuestion: Question) { /*...*/ }
    override suspend fun learnedQuestion(id: Int) { /*...*/ }
}