package com.example.learningapp.presentation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.domain.model.Association
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
    private val getAllQuestionsUseCase: getAllQuestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionState())
    val state: StateFlow<QuestionState> = _state.asStateFlow()

    fun processIntent(intent: QuestionIntent){
        when(intent) {
            QuestionIntent.LoadQuestions -> loadQuestions()
            is QuestionIntent.LearnedStatus -> learnedStatus(id = intent.id)
            is QuestionIntent.LoadQuestionById -> loadQuestionById(id = intent.id)
            is QuestionIntent.AddAssociation -> addAssociation(intent.association)
            is QuestionIntent.AddQuestion -> TODO()
            is QuestionIntent.DeleteQuestion -> TODO()
            is QuestionIntent.UpdateQuestion -> TODO()
            is QuestionIntent.UpdateStatistics -> TODO()
        }
    }

    private fun loadQuestions(){
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val questions = getAllQuestionsUseCase()
                _state.value = state.value.copy(
                    questions = questions,
                    isLoading = false,
                    error = null)
            }
            catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun learnedStatus(id: Int){
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

    private fun loadQuestionById(id: Int){
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

    private fun addAssociation(association: Association){

    }

}