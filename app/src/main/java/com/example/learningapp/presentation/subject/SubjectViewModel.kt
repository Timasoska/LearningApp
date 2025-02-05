package com.example.learningapp.presentation.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.data.local.entities.SubjectEntity
import com.example.learningapp.data.local.mappers.toDomain
import com.example.learningapp.data.local.mappers.toEntity
import com.example.learningapp.domain.usecase.subject.AddSubjectUseCase
import com.example.learningapp.domain.usecase.subject.DeleteSubjectUseCase
import com.example.learningapp.domain.usecase.subject.GetAllSubjectsUseCase
import com.example.learningapp.domain.usecase.subject.GetSubjectByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val addSubjectUseCase: AddSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectState())
    val state: StateFlow<SubjectState> = _state.asStateFlow()

    fun processIntent(intent: SubjectIntent){
        when(intent) {
            is SubjectIntent.AddSubject -> addSubject(intent.subject.toEntity())
            is SubjectIntent.DeleteSubject -> deleteSubject(intent.id)
            SubjectIntent.LoadSubject -> loadSubjects()
            is SubjectIntent.LoadSubjectById -> loadSubjectById(intent.id)
        }
    }

    private fun loadSubjects(){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val subject = getAllSubjectsUseCase()
                _state.update { it.copy(subjects = subject, isLoading = false, error = null) }
            } catch (e: Exception){
                _state.update { it.copy(isLoading = false, error = "Ошибка при загрузке предметов${e.message}") }
            }
        }
    }

    private fun addSubject(subject: SubjectEntity){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                addSubjectUseCase(subject.toDomain())
                loadSubjects()
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Ошибка при добавлении предмета ${e.message}") }
            }
        }
    }

    private fun deleteSubject(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                deleteSubjectUseCase(id)
                loadSubjects()
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception){
                _state.update { it.copy(isLoading = false, error = "Ошибка при удалении объекта${e.message}") }
            }
        }
    }

    private fun loadSubjectById(id: Int){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                getSubjectByIdUseCase(id)
                loadSubjects()
                _state.update { it.copy(isLoading = false, error = null) }
            }catch (e: Exception){
                _state.update { it.copy(isLoading = false, error = "Ошибка при загрузке предмета по id ${e.message}") }
            }
        }
    }

}