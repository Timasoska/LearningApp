package com.example.learningapp.presentation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningapp.domain.usecase.getAllQuestionsUseCase
import com.example.learningapp.domain.usecase.getQuestionByIdUseCase
import com.example.learningapp.domain.usecase.learnedQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val learnedQuestionUseCase: learnedQuestionUseCase,
    private val getQuestionByIdUseCase: getQuestionByIdUseCase,
    private val getAllQuestionsUseCase: getAllQuestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionState())
    val state: StateFlow<QuestionState> = _state.asStateFlow()

    fun processIntent(intent: QuestionIntent){
        when(intent) {
            QuestionIntent.LoadQuestions -> loadQuestions()
            is QuestionIntent.LearnedStatus -> learnedStatus(id = intent.id)
            is QuestionIntent.LoadQuestionById -> loadQuestionById(id = intent.id)
        }
    }

    fun loadQuestions(){
        viewModelScope.launch {
            getAllQuestionsUseCase()
                .catch { e ->
                    _state.update {
                        it.copy(error = "Ошибка загрузки: ${e.message}")
                    }
                }
                .collect { questions ->
                    _state.update {
                        it.copy(
                            questions = questions,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun learnedStatus(id: Int){
        viewModelScope.launch {
            try {
                learnedQuestionUseCase(id)
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Ошибка загрузки: ${e.message}")
                }
            }
        }
    }

    fun loadQuestionById(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val question = getQuestionByIdUseCase(id)
                _state.update {
                    it.copy(
                        currentQuestion = question,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception){
                _state.update {
                    it.copy(
                        error = "Не найден вопрос ID: $id",
                        isLoading = false
                    )
                }
            }
        }
    }

}