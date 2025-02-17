package com.example.learningapp.presentation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.usecase.question.DeleteQuestionUseCase
import com.example.learningapp.domain.usecase.question.GetQuestionsBySubjectUseCase
import com.example.learningapp.domain.usecase.question.UpdateQuestionUseCase
import com.example.learningapp.domain.usecase.question.getAllQuestionsUseCase
import com.example.learningapp.domain.usecase.question.getQuestionByIdUseCase
import com.example.learningapp.domain.usecase.question.learnedQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val learnedQuestionUseCase: learnedQuestionUseCase,
    private val getQuestionByIdUseCase: getQuestionByIdUseCase,
    private val getAllQuestionsUseCase: getAllQuestionsUseCase,
    private val deleteQuestionUseCase: DeleteQuestionUseCase,
    private val updateQuestionUseCase: UpdateQuestionUseCase,
    private val getQuestionsBySubjectUseCase: GetQuestionsBySubjectUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionState())
    val state: StateFlow<QuestionState> = _state.asStateFlow()

    fun processIntent(intent: QuestionIntent){
        when(intent) {
            is QuestionIntent.LoadQuestions -> loadQuestions()
            is QuestionIntent.LearnedStatus -> learnedStatus(id = intent.id)
            is QuestionIntent.LoadQuestionById -> loadQuestionById(id = intent.id)
            is QuestionIntent.AddQuestion -> addQuestion(intent.question)
            is QuestionIntent.DeleteQuestion -> deleteQuestion(intent.id)
            is QuestionIntent.UpdateQuestion -> updateQuestion(intent.newQuestion)
            is QuestionIntent.LoadQuestionBySubject -> loadQuestionsBySubject(intent.subjectId)
        }
    }

    private fun loadQuestions(){
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val questions = getAllQuestionsUseCase()
                _state.value = state.value.copy(questions = questions, isLoading = false, error = null)
            }
            catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка загрузки вопросов ${e.message}", isLoading = false)
                }
            }
        }
    }

    private fun learnedStatus(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                learnedQuestionUseCase(id)
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Ошибка при изменении статуса: ${e.message}", isLoading = false)
                }
            }
        }
    }

    private fun loadQuestionById(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val question = getQuestionByIdUseCase(id)
                _state.update {
                    it.copy(currentQuestion = question, isLoading = false, error = null
                    )
                }
            } catch (e: Exception){
                _state.update {
                    it.copy(error = "Не найден вопрос ID: $id", isLoading = false
                    )
                }
            }
        }
    }


    private fun addQuestion(question: Question){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                addQuestion(question)
                loadQuestions()
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception){
                _state.update { it.copy(isLoading = false, error = "Ошибка при добавлении вопроса ${e.message}")
                }
            }
        }
    }

    private fun deleteQuestion(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try{
                deleteQuestionUseCase(id)
                loadQuestions()
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception){
                _state.update { it.copy(isLoading = false, error = "Ошибка при удалении вопроса ${e.message}") }
            }
        }
    }

    private fun updateQuestion(newQuestion: Question){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try{
                updateQuestionUseCase(newQuestion)
                loadQuestions()
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception){
                _state.update { it.copy(isLoading = false, error = "Ошибка при обновлении вопроса ${e.message}") }
            }
        }
    }


    private fun loadQuestionsBySubject(subjectId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val questions = getQuestionsBySubjectUseCase(subjectId) // <-- Use Case для получения вопросов по предмету
                _state.update { it.copy(questions = MutableStateFlow(questions), isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Ошибка: ${e.message}") }
            }
        }
    }


}