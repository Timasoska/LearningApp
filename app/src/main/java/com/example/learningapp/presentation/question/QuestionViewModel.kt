package com.example.learningapp.presentation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.usecase.question.AddQuestionUseCase
import com.example.learningapp.domain.usecase.question.DeleteQuestionUseCase
import com.example.learningapp.domain.usecase.question.GetQuestionsBySubjectUseCase
import com.example.learningapp.domain.usecase.question.UpdateQuestionUseCase
import com.example.learningapp.domain.usecase.question.getAllQuestionsUseCase
import com.example.learningapp.domain.usecase.question.getQuestionByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import com.example.learningapp.domain.usecase.question.* // Убедись, что все UseCase'ы импортированы

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val getQuestionsBySubjectUseCase: GetQuestionsBySubjectUseCase,
    private val getQuestionByIdUseCase: getQuestionByIdUseCase, // Должен быть GetQuestionByIdUseCase
    private val addQuestionUseCase: AddQuestionUseCase,
    private val updateQuestionUseCase: UpdateQuestionUseCase,
    private val deleteQuestionUseCase: DeleteQuestionUseCase, // Должен быть DeleteQuestionUseCase
    private val updateLearnedStatusUseCase: UpdateLearnedStatusUseCase // Наш новый UseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionState())
    val state: StateFlow<QuestionState> = _state.asStateFlow()

    fun processIntent(intent: QuestionIntent) {
        Log.d("QuestionVM", "Processing Intent: $intent")
        when (intent) {
            is QuestionIntent.LoadQuestionBySubject -> loadQuestionsBySubject(intent.subjectId)
            is QuestionIntent.LoadQuestionById -> loadQuestionById(intent.id)
            is QuestionIntent.AddQuestion -> addNewQuestion(intent.question)
            is QuestionIntent.UpdateQuestion -> updateUserQuestion(intent.newQuestion)
            is QuestionIntent.DeleteQuestion -> deleteUserQuestion(intent.questionId, intent.subjectId)
            is QuestionIntent.ToggleLearnedStatus -> toggleLearnedStatus(intent.questionId, intent.newStatus, intent.subjectIdForReload)
            QuestionIntent.LoadQuestions -> { /* Не используется пока */ }
        }
    }

    private fun loadQuestionsBySubject(subjectId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, currentSubjectIdForList = subjectId) }
            try {
                val questionsFromRepo = getQuestionsBySubjectUseCase(subjectId)
                _state.update {
                    it.copy(
                        questions = questionsFromRepo,
                        isLoading = false
                    )
                }
                Log.d("QuestionVM", "Loaded ${questionsFromRepo.size} questions for subject $subjectId")
            } catch (e: Exception) {
                Log.e("QuestionVM", "Error loading questions for subject $subjectId: ${e.message}", e)
                _state.update {
                    it.copy(isLoading = false, error = "Ошибка загрузки вопросов: ${e.message}")
                }
            }
        }
    }

    private fun loadQuestionById(questionId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, currentQuestion = null) }
            try {
                // Предполагаем, GetQuestionByIdUseCase возвращает Question?
                val question = getQuestionByIdUseCase(questionId)
                _state.update { it.copy(isLoading = false, currentQuestion = question) }
                if (question == null) {
                    Log.w("QuestionVM", "Question with ID $questionId not found.")
                    _state.update { it.copy(error = "Вопрос не найден") }
                }
            } catch (e: Exception) {
                Log.e("QuestionVM", "Error loading question $questionId: ${e.message}", e)
                _state.update { it.copy(isLoading = false, error = "Ошибка загрузки вопроса: ${e.message}") }
            }
        }
    }


    private fun addNewQuestion(question: Question) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val newId = addQuestionUseCase(question)
                if (newId != -1L) {
                    Log.d("QuestionVM", "Question added with ID $newId. Reloading questions for subject ${question.subjectId}")
                    loadQuestionsBySubject(question.subjectId) // Перезагружаем список
                } else {
                    _state.update { it.copy(isLoading = false, error = "Не удалось добавить вопрос") }
                }
            } catch (e: Exception) {
                Log.e("QuestionVM", "Error adding question: ${e.message}", e)
                _state.update { it.copy(isLoading = false, error = "Ошибка добавления: ${e.message}") }
            }
        }
    }

    private fun updateUserQuestion(question: Question) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                updateQuestionUseCase(question)
                Log.d("QuestionVM", "Question ${question.id} updated. Reloading questions for subject ${question.subjectId}")
                loadQuestionsBySubject(question.subjectId) // Перезагружаем
            } catch (e: Exception) {
                Log.e("QuestionVM", "Error updating question: ${e.message}", e)
                _state.update { it.copy(isLoading = false, error = "Ошибка обновления: ${e.message}") }
            }
        }
    }

    private fun deleteUserQuestion(questionId: Int, subjectId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                deleteQuestionUseCase(questionId) // UseCase теперь не принимает subjectId
                Log.d("QuestionVM", "Question $questionId deleted. Reloading questions for subject $subjectId")
                loadQuestionsBySubject(subjectId) // Перезагружаем
            } catch (e: Exception) {
                Log.e("QuestionVM", "Error deleting question: ${e.message}", e)
                _state.update { it.copy(isLoading = false, error = "Ошибка удаления: ${e.message}") }
            }
        }
    }

    private fun toggleLearnedStatus(questionId: Int, newStatus: Boolean, subjectIdForReload: Int) {
        viewModelScope.launch {
            // Оптимистичное обновление (можно убрать, если не нужно)
            val originalQuestions = _state.value.questions
            _state.update { currentState ->
                currentState.copy(
                    questions = currentState.questions.map {
                        if (it.id == questionId) it.copy(isLearned = newStatus) else it
                    }
                )
            }
            try {
                updateLearnedStatusUseCase(questionId, newStatus)
                Log.d("QuestionVM", "Toggled learned status for $questionId to $newStatus. Reloading questions for subject $subjectIdForReload")
                // Перезагружаем список, чтобы убедиться в консистентности с сервером
                loadQuestionsBySubject(subjectIdForReload)
            } catch (e: Exception) {
                Log.e("QuestionVM", "Error toggling learned status for $questionId: ${e.message}", e)
                _state.update { it.copy(questions = originalQuestions, error = "Ошибка изменения статуса: ${e.message}") } // Откат
            }
        }
    }
}