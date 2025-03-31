package com.example.learningapp.presentation.subject

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.usecase.AddSearchTermUseCase
import com.example.learningapp.domain.usecase.ClearSearchHistoryUseCase
import com.example.learningapp.domain.usecase.GetSearchHistoryUseCase
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*



@OptIn(FlowPreview::class)
@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val addSubjectUseCase: AddSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    // --- Внедряем UseCases истории ---
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase, // <--- Зависимость есть
    private val addSearchTermUseCase: AddSearchTermUseCase,     // <--- Зависимость есть
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase // <--- Зависимость есть
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectState())
    val state: StateFlow<SubjectState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private var _fullSubjectList = emptyList<Subject>()

    init {
        initializeViewModel()
    }

    private fun initializeViewModel() {
        // 1. Комбинируем поток данных и поток _дебаунсированного_ запроса
        viewModelScope.launch {
            // Устанавливаем начальную загрузку один раз
            _state.update { it.copy(isLoading = true, error = null) }

            combine(
                getAllSubjectsUseCase().catch { e ->
                    Log.e("SubjectViewModel", "Error getting subjects", e)
                    _state.update { it.copy(isLoading = false, error = "Ошибка загрузки: ${e.message}") }
                    emit(emptyList())
                },
                _searchQuery.debounce(300) // Дебаунс на 300 мс
            ) { subjectsList, debouncedQuery ->
                // Эта лямбда выполняется после debounce ИЛИ когда приходит новый список
                Log.d("SubjectViewModel", "Combine triggered. Query: '$debouncedQuery', List size: ${subjectsList.size}")
                _fullSubjectList = subjectsList // Сохраняем полный список
                val filtered = filterSubjects(subjectsList, debouncedQuery)
                // Возвращаем пару: отфильтрованный список и актуальный (дебаунсированный) запрос
                Pair(filtered, debouncedQuery)
            }
                .collectLatest { (filteredList, finalQuery) ->
                    // Обновляем состояние после завершения debounce/фильтрации
                    Log.d("SubjectViewModel", "Updating state. Filtered size: ${filteredList.size}, Query: '$finalQuery'")
                    _state.update {
                        it.copy(
                            isLoading = false, // Загрузка/фильтрация завершена
                            searchQuery = finalQuery, // Обновляем searchQuery на дебаунсированный
                            filteredSubjects = filteredList,
                            error = null // Сбрасываем ошибку при успехе
                        )
                    }
                    updateShowHistoryState() // Пересчитываем видимость истории
                }
        }

        // 2. Сбор истории поиска (без изменений)
        viewModelScope.launch {
            getSearchHistoryUseCase().collect { history: List<String> ->
                _state.update { it.copy(searchHistory = history) }
                updateShowHistoryState()
            }
        }

        // 3. Обработка НЕПОСРЕДСТВЕННОГО ввода в TextField и isLoading ВО ВРЕМЯ debounce
        viewModelScope.launch {
            _searchQuery.collect { query ->
                // Показываем isLoading, если пользователь начал печатать (запрос не пустой)
                // и еще не пришел результат от combine (который поставит isLoading=false)
                val shouldBeLoading = query.isNotEmpty()
                // Обновляем searchQuery для TextField и isLoading для индикатора debounce
                _state.update { currentState ->
                    currentState.copy(
                        searchQuery = query, // Обновляем немедленно для UI
                        // Ставим isLoading=true если начали печатать,
                        // ИЛИ сохраняем true, если уже идет основная загрузка
                        isLoading = shouldBeLoading || (currentState.isLoading && _fullSubjectList.isEmpty())
                    )
                }
                updateShowHistoryState()
            }
        }
    }


    fun processIntent(intent: SubjectIntent) {
        when (intent) {
            is SubjectIntent.SearchQueryChanged -> { _searchQuery.value = intent.query }
            is SubjectIntent.SearchBarFocusChanged -> { handleFocusChange(intent.isFocused) }
            is SubjectIntent.SubmitSearch -> { submitSearch(intent.query) } // <-- Вызываем ИСПРАВЛЕННЫЙ метод
            is SubjectIntent.HistoryItemClicked -> { handleHistoryClick(intent.term) } // <-- Вызываем ИСПРАВЛЕННЫЙ метод
            SubjectIntent.ClearSearchHistory -> { clearHistory() } // <-- Вызываем ИСПРАВЛЕННЫЙ метод
            SubjectIntent.LoadSubjects -> { initializeViewModel() }
            is SubjectIntent.AddSubject -> addSubject(intent.name)
            is SubjectIntent.DeleteSubject -> deleteSubject(intent.id)
            is SubjectIntent.LoadSubjectById -> loadSubjectById(intent.id)
            is SubjectIntent.UpdateSubject -> updateSubject(intent.subject)
        }
    }

    private fun filterSubjects(subjects: List<Subject>, query: String): List<Subject> {
        return if (query.isBlank()) subjects else subjects.filter { it.name.contains(query, ignoreCase = true) }
    }

    // --- Логика истории (ИСПРАВЛЕНИЯ: добавляем вызовы UseCase) ---
    private fun handleFocusChange(isFocused: Boolean) {
        _state.update { it.copy(isSearchBarFocused = isFocused) }
        updateShowHistoryState()
    }

    private fun updateShowHistoryState() {
        _state.update { currentState ->
            val shouldShow = currentState.isSearchBarFocused &&
                    currentState.searchQuery.isBlank() &&
                    currentState.searchHistory.isNotEmpty()
            // Улучшение: не показываем историю, если идет первоначальная загрузка
            val primaryLoading = currentState.isLoading && _fullSubjectList.isEmpty()
            if (primaryLoading) {
                currentState.copy(showHistory = false)
            } else {
                currentState.copy(showHistory = shouldShow)
            }
        }
    }


    private fun submitSearch(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotBlank()) {
            viewModelScope.launch {
                addSearchTermUseCase(trimmedQuery) // <-- ИСПОЛЬЗУЕМ UseCase
            }
            _searchQuery.value = trimmedQuery // Обновляем для debounce/фильтрации
            // Скрываем историю/фокус после подтверждения поиска
            _state.update { it.copy(showHistory = false, isSearchBarFocused = false) }
        } else {
            _state.update { it.copy(showHistory = false, isSearchBarFocused = false) }
        }
    }

    private fun handleHistoryClick(term: String) {
        _searchQuery.value = term // Запускаем поиск
        _state.update { it.copy(isSearchBarFocused = false, showHistory = false, isLoading = true) } // Обновляем UI
        viewModelScope.launch {
            addSearchTermUseCase(term) // <-- ИСПОЛЬЗУЕМ UseCase (для перемещения вверх)
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            clearSearchHistoryUseCase() // <-- ИСПОЛЬЗУЕМ UseCase
            _state.update { it.copy(showHistory = false) } // Обновляем UI
        }
    }

    // --- CRUD операции (РЕАЛИЗУЕМ) ---
    private fun addSubject(name: String) {
        viewModelScope.launch {
            Log.d("SubjectViewModel", "Attempting to add subject: $name")
            try {
                val result = addSubjectUseCase(name) // Вызываем UseCase
                Log.d("SubjectViewModel", "addSubjectUseCase completed. Result: $result")
                // Список обновится автоматически через Flow -> combine
            } catch (e: Exception) {
                Log.e("SubjectViewModel", "Error adding subject: $name", e)
                // Обновляем только ошибку, isLoading управляется combine
                _state.update { it.copy(error = "Ошибка добавления предмета: ${e.message}") }
            }
        }
    }

    private fun deleteSubject(id: Int) {
        viewModelScope.launch {
            Log.d("SubjectViewModel", "Attempting to delete subject ID: $id")
            try {
                deleteSubjectUseCase(id) // Вызываем UseCase
                Log.d("SubjectViewModel", "deleteSubjectUseCase completed for ID: $id")
                // Список обновится автоматически через Flow -> combine
            } catch (e: Exception) {
                Log.e("SubjectViewModel", "Error deleting subject ID: $id", e)
                _state.update { it.copy(isLoading = false, error = "Ошибка удаления предмета: ${e.message}") }
            }
        }
    }

    private fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            Log.d("SubjectViewModel", "Attempting to update subject: ${subject.id} - ${subject.name}")
            try {
                updateSubjectUseCase(subject) // Вызываем UseCase
                Log.d("SubjectViewModel", "updateSubjectUseCase completed for ID: ${subject.id}")
                // Список обновится автоматически через Flow -> combine
            } catch (e: Exception) {
                Log.e("SubjectViewModel", "Error updating subject ID: ${subject.id}", e)
                _state.update { it.copy(isLoading = false, error = "Ошибка обновления предмета: ${e.message}") }
            }
        }
    }

    private fun loadSubjectById(id: Int) {
        viewModelScope.launch {
            Log.d("SubjectViewModel", "Loading subject by ID: $id")
            _state.update { it.copy(isLoading = true) }
            try {
                val subject = getSubjectByIdUseCase(id)
                Log.d("SubjectViewModel", "Subject loaded: $subject")
                _state.update { it.copy(isLoading = false, currentSubject = subject, error = null) }
            } catch (e: Exception) {
                Log.e("SubjectViewModel", "Error loading subject ID: $id", e)
                _state.update { it.copy(isLoading = false, error = "Не удалось загрузить предмет $id: ${e.message}") }
            }
        }
    }
}