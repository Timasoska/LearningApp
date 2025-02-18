package com.example.learningapp.presentation.question


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.data.local.entities.StatisticsEntity
import com.example.learningapp.domain.model.Association
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.usecase.association.AddAssociationUseCase
import com.example.learningapp.domain.usecase.association.DeleteAssociationUseCase
import com.example.learningapp.domain.usecase.association.UpdateAssociationUseCase
import com.example.learningapp.domain.usecase.question.AddQuestionUseCase
import com.example.learningapp.domain.usecase.question.DeleteQuestionUseCase
import com.example.learningapp.domain.usecase.question.UpdateQuestionUseCase
import com.example.learningapp.domain.usecase.question.getAllQuestionsUseCase
import com.example.learningapp.domain.usecase.question.getQuestionByIdUseCase
import com.example.learningapp.domain.usecase.question.learnedQuestionUseCase
import com.example.learningapp.domain.usecase.question.UpdateStatisticsUseCase
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
    private val addAssociationUseCase: AddAssociationUseCase,
    private val deleteQuestionUseCase: DeleteQuestionUseCase,
    private val updateQuestionUseCase: UpdateQuestionUseCase,
    private val updateStatisticsUseCase: UpdateStatisticsUseCase,
    private val deleteAssociationUseCase: DeleteAssociationUseCase,
    private val updateAssociationUseCase: UpdateAssociationUseCase,
    private val addQuestionUseCase: AddQuestionUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionState())
    val state: StateFlow<QuestionState> = _state.asStateFlow()

    fun processIntent(intent: QuestionIntent){
        when(intent) {
            is QuestionIntent.LoadQuestions -> loadQuestions()
            is QuestionIntent.LearnedStatus -> learnedStatus(id = intent.id)
            is QuestionIntent.LoadQuestionById -> loadQuestionById(id = intent.id)
            is QuestionIntent.AddAssociation -> addAssociation(intent.association)
            is QuestionIntent.UpdateAssociation -> updateAssociation(intent.association)
            is QuestionIntent.AddQuestion -> addQuestion(intent.question)
            is QuestionIntent.DeleteQuestion -> deleteQuestion(intent.id)
            is QuestionIntent.UpdateQuestion -> updateQuestion(intent.newQuestion)
            is QuestionIntent.UpdateStatistics -> updateStatistics(intent.statisticsEntity)
            is QuestionIntent.DeleteAssociation -> deleteAssociation(intent.id)

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

    private fun addAssociation(association: Association){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try{
                addAssociationUseCase(association)
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception){
                _state.update {it.copy(isLoading = false, error = "Ошибка при добавлении ассоциации ${e.message}")}
            }
        }
    }

    private fun addQuestion(question: Question){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                addQuestionUseCase(question)
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

    private fun updateStatistics(statisticsEntity: StatisticsEntity){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try{
                updateStatisticsUseCase(statisticsEntity)
                loadQuestions()
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception){
                _state.update { it.copy(isLoading = false, error = "Ошибка при обновлении статистики ${e.message}") }
            }
        }
    }

    private fun deleteAssociation(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                deleteAssociationUseCase(id)
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка при удалении ассоциации: ${e.message}", isLoading = false) }
            }
        }
    }

    private fun updateAssociation(association: Association) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                updateAssociationUseCase(association)
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Ошибка при обновлении ассоциации: ${e.message}", isLoading = false) }
            }
        }
    }

}