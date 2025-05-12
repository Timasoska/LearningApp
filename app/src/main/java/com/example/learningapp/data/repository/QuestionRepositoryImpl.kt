package com.example.learningapp.data.repository
import android.util.Log
import com.example.Constants
import com.example.learningapp.data.remote.LearnedStatusUpdateRequestDto
import com.example.learningapp.data.remote.QuestionResponseDto
import com.example.learningapp.data.remote.SubjectRequestDto
import com.example.learningapp.data.remote.SubjectResponseDto
import com.example.learningapp.data.remote.toDomainModel
import com.example.learningapp.data.remote.toRequestDto
import com.example.learningapp.di.SessionManager
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.repository.QuestionRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject


class QuestionRepositoryImpl @Inject constructor(
    private val client: HttpClient,
    private val sessionManager: SessionManager
) : QuestionRepository {

    // --- Subjects (методы без изменений из предыдущих версий) ---
    // QuestionRepositoryImpl.kt
    override fun getAllSubjects(): Flow<List<Subject>> = flow {
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) {
            Log.w("QuestionRepoImpl", "getAllSubjects: User not logged in.")
            // emit(emptyList()) // Можно не эмитить здесь, если Flow должен просто завершиться или выдать ошибку
            throw IllegalStateException("User not logged in") // Или кастомное исключение
        }
        try {
            Log.d("QuestionRepoImpl", "getAllSubjects: Fetching for userId: $userId")
            val dtoList: List<SubjectResponseDto> = client.get("${Constants.BASE_URL}/subjects") {
                parameter("userId", userId)
            }.body()
            emit(dtoList.map { it.toDomainModel() })
        } catch (e: Exception) { // Ловим все исключения, включая сетевые
            Log.e("QuestionRepoImpl", "getAllSubjects: Error for user $userId: ${e.message}", e)
            throw e // Просто пробрасываем исключение дальше. ViewModel его поймает.
        }
    }
    override suspend fun addSubject(name: String): Long {
        val userId = sessionManager.getCurrentUserId(); if (userId == null) { Log.e("QuestionRepoImpl", "addSubject: userId is null!"); return -1L }
        try {
            val requestDto = SubjectRequestDto(name = name)
            val responseDto: SubjectResponseDto = client.post("${Constants.BASE_URL}/subjects") {
                parameter("userId", userId); contentType(ContentType.Application.Json); setBody(requestDto)
            }.body()
            return responseDto.id.toLong()
        } catch (e: Exception) { Log.e("QuestionRepoImpl", "addSubject: Error for '$name', user $userId: ${e.message}", e); return -1L }
    }
    override suspend fun getSubjectById(id: Int): Subject? { Log.w("QuestionRepoImpl", "getSubjectById($id) - not implemented or server endpoint missing."); return null }
    override suspend fun updateSubject(subject: Subject) {
        val userId = sessionManager.getCurrentUserId(); if (userId == null) { Log.e("QuestionRepoImpl", "updateSubject: userId is null!"); return }
        try {
            client.put("${Constants.BASE_URL}/subjects/${subject.id}") {
                parameter("userId", userId); contentType(ContentType.Application.Json); setBody(subject.toRequestDto())
            }
        } catch (e: Exception) { Log.e("QuestionRepoImpl", "updateSubject: Error for ${subject.id}, user $userId: ${e.message}", e) }
    }
    override suspend fun deleteSubject(id: Int) {
        val userId = sessionManager.getCurrentUserId(); if (userId == null) { Log.e("QuestionRepoImpl", "deleteSubject: userId is null!"); return }
        try {
            client.delete("${Constants.BASE_URL}/subjects/$id") { parameter("userId", userId) }
        } catch (e: Exception) { Log.e("QuestionRepoImpl", "deleteSubject: Error for $id, user $userId: ${e.message}", e) }
    }

    // --- Questions ---
    override suspend fun getQuestionsBySubject(subjectId: Int): List<Question> { // Возвращает List
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) { Log.w("QuestionRepoImpl", "getQuestionsBySubject: User not logged in"); return emptyList() }
        return try {
            val dtoList: List<QuestionResponseDto> = client.get("${Constants.BASE_URL}/questions") {
                parameter("subjectId", subjectId); parameter("userId", userId)
            }.body()
            dtoList.map { it.toDomainModel() }
        } catch (e: Exception) { Log.e("QuestionRepoImpl", "Error fetching questions for subject $subjectId, user $userId: ${e.message}", e); emptyList() }
    }

    override suspend fun addQuestion(question: Question): Long {
        val userId = sessionManager.getCurrentUserId(); if (userId == null) { Log.e("QuestionRepoImpl", "addQuestion: userId is null!"); return -1L }
        try {
            val responseDto: QuestionResponseDto = client.post("${Constants.BASE_URL}/questions") {
                parameter("subjectId", question.subjectId); parameter("userId", userId)
                contentType(ContentType.Application.Json); setBody(question.toRequestDto())
            }.body()
            return responseDto.id.toLong()
        } catch (e: Exception) { Log.e("QuestionRepoImpl", "Error adding question for subject ${question.subjectId}, user $userId: ${e.message}", e); return -1L }
    }

    override suspend fun getQuestionById(id: Int): Question? {
        val userId = sessionManager.getCurrentUserId(); if (userId == null) { Log.w("QuestionRepoImpl", "getQuestionById: User not logged in."); return null }
        Log.d("QuestionRepoImpl", "getQuestionById($id) for userId $userId called.")
        return try {
            val dto: QuestionResponseDto = client.get("${Constants.BASE_URL}/questions/$id") {
                parameter("userId", userId)
            }.body()
            Log.d("QuestionRepoImpl", "getQuestionById: Received DTO: $dto")
            dto.toDomainModel()
        } catch (e: io.ktor.client.plugins.ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) { Log.w("QuestionRepoImpl", "getQuestionById: Question $id not found for user $userId."); return null }
            Log.e("QuestionRepoImpl", "getQuestionById: Client error for $id: ${e.response.status}, ${e.message}", e); null
        } catch (e: Exception) { Log.e("QuestionRepoImpl", "getQuestionById: Generic error for $id: ${e.message}", e); null }
    }

    override fun getAllQuestions(): Flow<List<Question>> { Log.w("QuestionRepoImpl", "getAllQuestions() - not for server use."); return flowOf(emptyList()) }

    override suspend fun deleteQuestion(id: Int) { // Потребует subjectId для обновления списка в VM, но сам метод его не принимает
        val userId = sessionManager.getCurrentUserId(); if (userId == null) { Log.e("QuestionRepoImpl", "deleteQuestion: userId is null!"); return }
        try {
            client.delete("${Constants.BASE_URL}/questions/$id") { parameter("userId", userId) }
        } catch (e: Exception) { Log.e("QuestionRepoImpl", "deleteQuestion: Error for $id: ${e.message}", e) }
    }

    override suspend fun updateQuestion(newQuestion: Question) {
        val userId = sessionManager.getCurrentUserId(); if (userId == null) { Log.e("QuestionRepoImpl", "updateQuestion: userId is null!"); return }
        try {
            client.put("${Constants.BASE_URL}/questions/${newQuestion.id}") {
                parameter("userId", userId); contentType(ContentType.Application.Json); setBody(newQuestion.toRequestDto())
            }
        } catch (e: Exception) { Log.e("QuestionRepoImpl", "updateQuestion: Error for ${newQuestion.id}: ${e.message}", e) }
    }

    override suspend fun updateLearnedStatus(questionId: Int, isLearned: Boolean) {
        val userId = sessionManager.getCurrentUserId()
        Log.d("QuestionRepoImpl", "updateLearnedStatus called for questionId: $questionId, newStatus: $isLearned, UserID: $userId")
        if (userId == null) {
            Log.e("QuestionRepoImpl", "updateLearnedStatus: userId is null!")
            // В реальном приложении лучше бросить исключение или вернуть Result.Error
            throw IllegalStateException("User not logged in, cannot update learned status.")
        }
        try {
            val requestDto = LearnedStatusUpdateRequestDto(isLearned = isLearned)
            Log.d("QuestionRepoImpl", "updateLearnedStatus: Sending PATCH to ${Constants.BASE_URL}/questions/$questionId/learned?userId=$userId")
            client.patch("${Constants.BASE_URL}/questions/$questionId/learned") {
                parameter("userId", userId)
                contentType(ContentType.Application.Json)
                setBody(requestDto)
            }
            Log.d("QuestionRepoImpl", "updateLearnedStatus: Status for $questionId updated successfully on server.")
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "updateLearnedStatus: Error for question $questionId: ${e.message}", e)
            throw e // Пробрасываем исключение, чтобы UseCase и ViewModel могли его обработать
        }
    }
}