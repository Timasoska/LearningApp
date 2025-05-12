package com.example.learningapp.presentation.subject

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningapp.di.SessionManager
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.domain.usecase.search.AddSearchTermUseCase
import com.example.learningapp.domain.usecase.search.ClearSearchHistoryUseCase
import com.example.learningapp.domain.usecase.search.GetSearchHistoryUseCase
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
import com.example.learningapp.presentation.authorization.Result



@OptIn(FlowPreview::class)
@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val addSubjectUseCase: AddSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addSearchTermUseCase: AddSearchTermUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectState())
    val state: StateFlow<SubjectState> = _state.asStateFlow()

    private var _fullSubjectList = emptyList<Subject>()
    private val _searchQueryFlow = MutableStateFlow("")

    init {
        // ... (код инициализации для searchHistory и _searchQueryFlow) ...
        viewModelScope.launch {
            getSearchHistoryUseCase().collect { history ->
                _state.update { it.copy(searchHistory = history) }
                updateShowHistoryState()
            }
        }
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(300)
                .collectLatest { query ->
                    filterSubjectsAndUpdateState(query)
                }
        }
        viewModelScope.launch {
            _searchQueryFlow.collect { query ->
                _state.update { it.copy(searchQuery = query) }
            }
        }

        viewModelScope.launch {
            sessionManager.currentUserIdFlow.collectLatest { userIdValue ->
                Log.d("SubjectViewModel", "SessionManager emitted userId: $userIdValue")
                if (userIdValue != null) {
                    Log.d("SubjectViewModel", "UserId available: $userIdValue. Triggering LoadInitialData.")
                    processIntent(SubjectIntent.LoadInitialData)
                } else {
                    Log.d("SubjectViewModel", "UserId became null. Clearing subjects.")
                    _fullSubjectList = emptyList()
                    _state.update { it.copy(subjects = emptyList(), isLoading = false, error = null) }
                }
            }
        }
    }

    fun processIntent(intent: SubjectIntent) {
        Log.d("SubjectViewModel", "Processing Intent: $intent")
        when (intent) {
            is SubjectIntent.LoadInitialData -> { // Интент для загрузки/повторной загрузки
                if (sessionManager.isLoggedIn()) {
                    loadInitialSubjects()
                } else {
                    Log.w("SubjectViewModel", "LoadInitialData: User not logged in.")
                    _state.update { it.copy(isLoading = false, error = "Пользователь не авторизован", subjects = emptyList())}
                }
            }
            // ... (остальные обработчики интентов) ...
            is SubjectIntent.SearchQueryChanged -> {
                _searchQueryFlow.value = intent.query
                updateShowHistoryState()
            }
            is SubjectIntent.SearchBarFocusChanged -> handleFocusChange(intent.isFocused)
            is SubjectIntent.SubmitSearch -> submitSearchTerm(intent.query)
            is SubjectIntent.HistoryItemClicked -> handleHistoryItemClick(intent.term)
            is SubjectIntent.ClearSearchHistory -> clearUserSearchHistory()
            is SubjectIntent.AddNewSubject -> addNewSubject(intent.name)
            is SubjectIntent.UpdateExistingSubject -> updateUserSubject(intent.subject)
            is SubjectIntent.DeleteExistingSubject -> deleteUserSubject(intent.id)
        }
    }

    private fun loadInitialSubjects() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) } // <-- Сброс ошибки, isLoading = true
            getAllSubjectsUseCase() // Это Flow<List<Subject>>
                .catch { e -> // <-- Ловим ошибку из Flow (например, от репозитория)
                    Log.e("SubjectViewModel", "Error in getAllSubjectsUseCase Flow", e)
                    _state.update {
                        it.copy(
                            isLoading = false, // <-- Загрузка завершена (неудачно)
                            error = e.message ?: "Не удалось загрузить предметы. Проверьте подключение.", // <-- Устанавливаем ошибку
                            subjects = emptyList() // Список пуст
                        )
                    }
                    _fullSubjectList = emptyList()
                }
                .collectLatest { subjectsFromServer -> // <-- Сюда попадаем, если НЕ БЫЛО ошибки в Flow
                    Log.d("SubjectViewModel", "Collected ${subjectsFromServer.size} subjects from server.")
                    _fullSubjectList = subjectsFromServer
                    filterSubjectsAndUpdateState(_state.value.searchQuery)
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false, // <-- Загрузка завершена (успешно)
                            error = null // <-- Ошибки нет
                        )
                    }
                }
        }
    }

    // ... (filterSubjectsAndUpdateState и другие приватные методы без изменений по теме ошибки)
    private fun filterSubjectsAndUpdateState(query: String) {
        val filtered = if (query.isBlank()) {
            _fullSubjectList
        } else {
            _fullSubjectList.filter { it.name.contains(query, ignoreCase = true) }
        }
        Log.d("SubjectViewModel", "Filtering: query='$query', fullListSize=${_fullSubjectList.size}, filteredSize=${filtered.size}")
        _state.update { it.copy(subjects = filtered) } // isLoading здесь не меняем, им управляют операции загрузки/CRUD
    }

    private fun handleFocusChange(isFocused: Boolean) {
        _state.update { it.copy(isSearchBarFocused = isFocused) }
        updateShowHistoryState()
    }

    private fun updateShowHistoryState() {
        _state.update { currentState ->
            val shouldShow = currentState.isSearchBarFocused &&
                    currentState.searchQuery.isBlank() &&
                    currentState.searchHistory.isNotEmpty()
            Log.d("SubjectViewModel", "updateShowHistoryState: isFocused=${currentState.isSearchBarFocused}, queryBlank=${currentState.searchQuery.isBlank()}, historyNotEmpty=${currentState.searchHistory.isNotEmpty()}, resultShouldShow=$shouldShow")
            currentState.copy(showHistory = shouldShow)
        }
    }

    private fun submitSearchTerm(term: String) {
        val trimmedTerm = term.trim()
        if (trimmedTerm.isNotBlank()) {
            viewModelScope.launch { addSearchTermUseCase(trimmedTerm) }
        }
        _state.update { it.copy(showHistory = false, isSearchBarFocused = false) }
    }

    private fun handleHistoryItemClick(term: String) {
        _state.update { it.copy(searchQuery = term, isSearchBarFocused = false, showHistory = false) }
        _searchQueryFlow.value = term
        viewModelScope.launch { addSearchTermUseCase(term) }
    }

    private fun clearUserSearchHistory() {
        viewModelScope.launch { clearSearchHistoryUseCase() }
    }

    private fun addNewSubject(name: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = addSubjectUseCase(name)) {
                is Result.Success -> {
                    Log.d("SubjectViewModel", "Subject added. Reloading subjects.")
                    loadInitialSubjects()
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Result.Loading -> { /* Обработка состояния загрузки, если UseCase его возвращает */ }
            }
        }
    }

    private fun updateUserSubject(subject: Subject) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                updateSubjectUseCase(subject)
                Log.d("SubjectViewModel", "Subject updated. Reloading subjects.")
                loadInitialSubjects()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to update subject") }
            }
        }
    }

    private fun deleteUserSubject(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                deleteSubjectUseCase(id)
                Log.d("SubjectViewModel", "Subject deleted. Reloading subjects.")
                loadInitialSubjects()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to delete subject") }
            }
        }
    }
}
