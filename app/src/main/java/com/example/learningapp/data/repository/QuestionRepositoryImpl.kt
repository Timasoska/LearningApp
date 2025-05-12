package com.example.learningapp.data.repository
import android.util.Log
import com.example.Constants
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

    // --- Subjects ---
    override fun getAllSubjects(): Flow<List<Subject>> = flow {
        // ... (уже реализовано) ...
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) {
            Log.w("QuestionRepoImpl", "getAllSubjects: User not logged in. Emitting empty list.")
            emit(emptyList())
            return@flow
        }
        try {
            Log.d("QuestionRepoImpl", "getAllSubjects: Fetching subjects for userId: $userId")
            val dtoList: List<SubjectResponseDto> = client.get("${Constants.BASE_URL}/subjects") {
                parameter("userId", userId)
            }.body()
            Log.d("QuestionRepoImpl", "getAllSubjects: Received ${dtoList.size} subjects from server.")
            emit(dtoList.map { it.toDomainModel() })
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "getAllSubjects: Error fetching subjects for user $userId: ${e.message}", e)
            emit(emptyList())
        }
    }

    override suspend fun addSubject(name: String): Long {
        // ... (уже реализовано) ...
        val userId = sessionManager.getCurrentUserId()
        Log.d("QuestionRepoImpl", "addSubject called. Name: '$name', UserID: $userId")
        if (userId == null) {
            Log.e("QuestionRepoImpl", "addSubject: userId is null!")
            return -1L
        }
        try {
            val requestDto = SubjectRequestDto(name = name) // Используем клиентский DTO
            Log.d("QuestionRepoImpl", "addSubject: Sending POST to ${Constants.BASE_URL}/subjects?userId=$userId")
            val responseDto: SubjectResponseDto = client.post("${Constants.BASE_URL}/subjects") { // Ожидаем SubjectResponseDto
                parameter("userId", userId)
                contentType(ContentType.Application.Json)
                setBody(requestDto)
            }.body()
            Log.d("QuestionRepoImpl", "addSubject: Server response: $responseDto")
            return responseDto.id.toLong()
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "addSubject: Error adding subject '$name' for user $userId: ${e.message}", e)
            return -1L
        }
    }

    override suspend fun getSubjectById(id: Int): Subject? {
        // Этот метод может быть не нужен, если UI не требует загрузки одного предмета отдельно.
        // Если нужен, на сервере должен быть эндпоинт GET /subjects/{id}?userId=X
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) {
            Log.w("QuestionRepoImpl", "getSubjectById: User not logged in.")
            return null
        }
        Log.w("QuestionRepoImpl", "getSubjectById($id) for userId $userId - not fully implemented or server endpoint might be missing/different.")
        // Примерная реализация, если бы эндпоинт существовал:
        /*
        try {
            val dto: SubjectResponseDto = client.get("${Constants.BASE_URL}/subjects/$id") {
                parameter("userId", userId)
            }.body()
            return dto.toDomainModel()
        } catch (e: io.ktor.client.plugins.ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) return null
            Log.e("QuestionRepoImpl", "Error fetching subject $id: ${e.message}", e)
            return null
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "Error fetching subject $id: ${e.message}", e)
            return null
        }
        */
        return null // Заглушка, пока не ясно, нужен ли и какой эндпоинт
    }

    override suspend fun updateSubject(subject: Subject) {
        val userId = sessionManager.getCurrentUserId()
        Log.d("QuestionRepoImpl", "updateSubject called for subjectId: ${subject.id}, UserID: $userId")
        if (userId == null) {
            Log.e("QuestionRepoImpl", "updateSubject: userId is null!")
            return // или бросить исключение / вернуть Result.Error
        }
        try {
            val requestDto = subject.toRequestDto() // Маппер из Domain Subject в SubjectRequestDto
            Log.d("QuestionRepoImpl", "updateSubject: Sending PUT to ${Constants.BASE_URL}/subjects/${subject.id}?userId=$userId")
            client.put("${Constants.BASE_URL}/subjects/${subject.id}") {
                parameter("userId", userId)
                contentType(ContentType.Application.Json)
                setBody(requestDto)
            }
            Log.d("QuestionRepoImpl", "updateSubject: Subject ${subject.id} updated successfully on server.")
            // Ktor HttpClient по умолчанию бросит исключение при неуспешном статусе (если expectSuccess = true)
            // Если expectSuccess = false, нужно проверять response.status
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "updateSubject: Error updating subject ${subject.id} for user $userId: ${e.message}", e)
            // бросить исключение или вернуть Result.Error
        }
    }

    override suspend fun deleteSubject(id: Int) {
        val userId = sessionManager.getCurrentUserId()
        Log.d("QuestionRepoImpl", "deleteSubject called for subjectId: $id, UserID: $userId")
        if (userId == null) {
            Log.e("QuestionRepoImpl", "deleteSubject: userId is null!")
            return // или бросить исключение / вернуть Result.Error
        }
        try {
            Log.d("QuestionRepoImpl", "deleteSubject: Sending DELETE to ${Constants.BASE_URL}/subjects/$id?userId=$userId")
            client.delete("${Constants.BASE_URL}/subjects/$id") {
                parameter("userId", userId)
            }
            Log.d("QuestionRepoImpl", "deleteSubject: Subject $id deleted successfully on server.")
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "deleteSubject: Error deleting subject $id for user $userId: ${e.message}", e)
            // бросить исключение или вернуть Result.Error
        }
    }

    // --- Questions ---
    // Пока оставим заглушками, реализуем их следующим шагом

    override suspend fun getQuestionsBySubject(subjectId: Int): List<Question> {
        val userId = sessionManager.getCurrentUserId()
        Log.d("QuestionRepoImpl", "getQuestionsBySubject called for subjectId: $subjectId, UserID: $userId")
        if (userId == null) {
            Log.w("QuestionRepoImpl", "getQuestionsBySubject: User not logged in")
            return emptyList()
        }
        try {
            Log.d("QuestionRepoImpl", "getQuestionsBySubject: Fetching for subject $subjectId, user $userId")
            val dtoList: List<QuestionResponseDto> = client.get("${Constants.BASE_URL}/questions") {
                parameter("subjectId", subjectId)
                parameter("userId", userId)
            }.body()
            Log.d("QuestionRepoImpl", "getQuestionsBySubject: Received ${dtoList.size} questions.")
            return dtoList.map { it.toDomainModel() }
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "getQuestionsBySubject: Error: ${e.message}", e)
            return emptyList()
        }
    }

    override suspend fun addQuestion(question: Question): Long {
        val userId = sessionManager.getCurrentUserId()
        Log.d("QuestionRepoImpl", "addQuestion called for subjectId: ${question.subjectId}, UserID: $userId")
        if (userId == null) {
            Log.e("QuestionRepoImpl", "addQuestion: userId is null!")
            return -1L
        }
        try {
            val requestDto = question.toRequestDto() // Domain Question -> QuestionRequestDto
            Log.d("QuestionRepoImpl", "addQuestion: Sending POST to ${Constants.BASE_URL}/questions?subjectId=${question.subjectId}&userId=$userId")
            val responseDto: QuestionResponseDto = client.post("${Constants.BASE_URL}/questions") {
                parameter("subjectId", question.subjectId)
                parameter("userId", userId)
                contentType(ContentType.Application.Json)
                setBody(requestDto)
            }.body()
            Log.d("QuestionRepoImpl", "addQuestion: Server response: $responseDto")
            return responseDto.id.toLong()
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "addQuestion: Error: ${e.message}", e)
            return -1L
        }
    }

    override suspend fun getQuestionById(id: Int): Question? {
        val userId = sessionManager.getCurrentUserId()
        if (userId == null) {
            Log.w("QuestionRepoImpl", "getQuestionById: User not logged in.")
            return null
        }
        Log.d("QuestionRepoImpl", "getQuestionById($id) for userId $userId called.")
        try {
            // Используем новый эндпоинт, который мы только что определили для сервера
            val dto: QuestionResponseDto = client.get("${Constants.BASE_URL}/questions/$id") {
                parameter("userId", userId)
            }.body()
            Log.d("QuestionRepoImpl", "getQuestionById: Received DTO: $dto")
            return dto.toDomainModel()
        } catch (e: io.ktor.client.plugins.ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                Log.w("QuestionRepoImpl", "getQuestionById: Question $id not found for user $userId.")
                return null
            }
            Log.e("QuestionRepoImpl", "getQuestionById: Client error fetching question $id: ${e.response.status}, ${e.message}", e)
            return null
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "getQuestionById: Generic error fetching question $id: ${e.message}", e)
            return null
        }
    }

    override fun getAllQuestions(): Flow<List<Question>> {
        // Этот метод, скорее всего, не будет использоваться с сервером в таком виде.
        Log.w("QuestionRepoImpl", "getAllQuestions() - likely not for server use without context.")
        return flowOf(emptyList())
    }

    override suspend fun deleteQuestion(id: Int) {
        val userId = sessionManager.getCurrentUserId()
        Log.d("QuestionRepoImpl", "deleteQuestion called for questionId: $id, UserID: $userId")
        if (userId == null) {
            Log.e("QuestionRepoImpl", "deleteQuestion: userId is null!")
            return
        }
        try {
            Log.d("QuestionRepoImpl", "deleteQuestion: Sending DELETE to ${Constants.BASE_URL}/questions/$id?userId=$userId")
            client.delete("${Constants.BASE_URL}/questions/$id") {
                parameter("userId", userId) // Сервер должен проверить, что этот вопрос принадлежит пользователю
            }
            Log.d("QuestionRepoImpl", "deleteQuestion: Question $id deleted.")
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "deleteQuestion: Error: ${e.message}", e)
        }
    }

    override suspend fun updateQuestion(newQuestion: Question) {
        val userId = sessionManager.getCurrentUserId()
        Log.d("QuestionRepoImpl", "updateQuestion called for questionId: ${newQuestion.id}, UserID: $userId")
        if (userId == null) {
            Log.e("QuestionRepoImpl", "updateQuestion: userId is null!")
            return
        }
        try {
            val requestDto = newQuestion.toRequestDto()
            Log.d("QuestionRepoImpl", "updateQuestion: Sending PUT to ${Constants.BASE_URL}/questions/${newQuestion.id}?userId=$userId")
            client.put("${Constants.BASE_URL}/questions/${newQuestion.id}") {
                parameter("userId", userId) // Сервер должен проверить права
                contentType(ContentType.Application.Json)
                setBody(requestDto)
            }
            Log.d("QuestionRepoImpl", "updateQuestion: Question ${newQuestion.id} updated.")
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "updateQuestion: Error: ${e.message}", e)
        }
    }

    override suspend fun learnedQuestion(id: Int) {
        // Для этого метода нам нужно знать, какой статус learned установить.
        // Интерфейс не передает это. Предположим, мы хотим его "переключить".
        // Это потребует сначала GET-запроса, чтобы узнать текущий статус, а потом PATCH.
        // Либо сервер должен иметь эндпоинт для "toggle".
        // Пока оставим как TODO, так как это требует больше логики или изменения интерфейса/сервера.
        // Если у тебя learnedQuestionUseCase передает новый статус, то реализация будет проще.

        // Для примера, если бы мы всегда устанавливали в true (что неверно для toggle):
        val userId = sessionManager.getCurrentUserId()
        Log.w("QuestionRepoImpl", "learnedQuestion($id) for userId $userId - current impl is placeholder/incomplete.")
        if (userId == null) return
        try {
            // Это пример, как отправить PATCH с новым статусом. Откуда взять newStatus?
            // val newStatus = true // Нужно определить логику получения нового статуса
            // val requestDto = LearnedStatusUpdateRequestDto(isLearned = newStatus)
            // client.patch("${Constants.BASE_URL}/questions/$id/learned") {
            // parameter("userId", userId)
            // contentType(ContentType.Application.Json)
            // setBody(requestDto)
            // }
            Log.d("QuestionRepoImpl", "learnedQuestion: Status for $id updated (placeholder).")
        } catch (e: Exception) {
            Log.e("QuestionRepoImpl", "learnedQuestion: Error for $id: ${e.message}", e)
        }
    }
}