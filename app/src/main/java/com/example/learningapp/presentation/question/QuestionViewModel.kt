package com.example.learningapp.presentation.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.domain.usecase.getAllQuestionsUseCase
import com.example.learningapp.domain.usecase.getQuestionByIdUseCase
import com.example.learningapp.domain.usecase.learnedQuestionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuestionViewModel @Inject constructor(
    private val learnedQuestionUseCase: learnedQuestionUseCase,
    private val getQuestionByIdUseCase: getQuestionByIdUseCase,
    private val getAllQuestionsUseCase: getAllQuestionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionState())
    val state: StateFlow<QuestionState> = _state

    fun processIntent(intent: QuestionIntent){
        when(intent) {
            QuestionIntent.LoadQuestions -> loadQuestions()
            is QuestionIntent.LearnedStatus -> learnedStatus(id = intent.id)
            is QuestionIntent.LoadQuestionById -> loadQuestionById(id = intent.id)
        }
    }

    fun loadQuestions(){
        getAllQuestionsUseCase
    }

    fun learnedStatus(id: Int){
        viewModelScope.launch {
            learnedQuestionUseCase(id)
        }
    }

    fun loadQuestionById(id: Int){
        viewModelScope.launch {
            getQuestionByIdUseCase(id)
        }
    }

}