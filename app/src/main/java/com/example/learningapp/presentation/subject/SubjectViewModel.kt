package com.example.learningapp.presentation.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.usecase.subject.AddSubjectUseCase
import com.example.learningapp.domain.usecase.subject.DeleteSubjectUseCase
import com.example.learningapp.domain.usecase.subject.GetAllSubjectsUseCase
import com.example.learningapp.domain.usecase.subject.GetSubjectByIdUseCase
import com.example.learningapp.domain.usecase.subject.UpdateSubjectUseCase
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
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase // новая зависимость
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectState())
    val state: StateFlow<SubjectState> = _state.asStateFlow()

    fun processIntent(intent: SubjectIntent){
        when(intent) {
            is SubjectIntent.AddSubject -> addSubject(intent.name)
            is SubjectIntent.DeleteSubject -> deleteSubject(intent.id)
            SubjectIntent.LoadSubject -> loadSubjects()
            is SubjectIntent.LoadSubjectById -> loadSubjectById(intent.id)
            is SubjectIntent.UpdateSubject -> updateSubject(intent.subject)
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

    private fun addSubject(name: String){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                addSubjectUseCase(name)
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

    private fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                updateSubjectUseCase(subject)
                loadSubjects() // обновляем список после изменения
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Ошибка при обновлении предмета: ${e.message}"
                    )
                }
            }
        }
    }

}