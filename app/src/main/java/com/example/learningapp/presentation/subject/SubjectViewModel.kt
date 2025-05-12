package com.example.learningapp.presentation.subject

import android.util.Log
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
import kotlinx.coroutines.Job


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
    private val sessionManager: SessionManager // Инжектируем SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(SubjectState())
    val state: StateFlow<SubjectState> = _state.asStateFlow()

    private var _fullSubjectList = emptyList<Subject>() // Локальный кэш полного списка
    private var searchJob: Job? = null // Для отмены предыдущего поиска/фильтрации

    // Для обработки ввода в реальном времени в TextField поиска
    private val _searchQueryFlow = MutableStateFlow("")

    init {
        // Загрузка истории поиска при инициализации (не зависит от userId)
        viewModelScope.launch {
            getSearchHistoryUseCase().collect { history ->
                _state.update { it.copy(searchHistory = history) }
                updateShowHistoryState()
            }
        }

        // Обработка изменений searchQuery (не зависит от userId напрямую для debounce)
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(300)
                .collectLatest { query ->
                    filterSubjectsAndUpdateState(query) // Фильтрует _fullSubjectList
                }
        }
        // Для немедленного обновления searchQuery в UI при вводе
        viewModelScope.launch {
            _searchQueryFlow.collect { query ->
                _state.update { it.copy(searchQuery = query) }
            }
        }

        // Подписываемся на изменения userId. Загружаем данные только когда userId известен.
        viewModelScope.launch {
            sessionManager.currentUserIdFlow.collectLatest { userId ->
                if (userId != null) {
                    Log.d("SubjectViewModel", "UserId available: $userId. Loading initial subjects.")
                    processIntent(SubjectIntent.LoadInitialData) // Теперь LoadInitialData будет вызван с userId
                } else {
                    Log.d("SubjectViewModel", "UserId is null. Clearing subjects.")
                    _fullSubjectList = emptyList()
                    _state.update { it.copy(subjects = emptyList(), isLoading = false, error = null) }
                }
            }
        }
    }


    fun processIntent(intent: SubjectIntent) {
        Log.d("SubjectViewModel", "Processing Intent: $intent")
        when (intent) {
            is SubjectIntent.LoadInitialData -> loadInitialSubjects()
            is SubjectIntent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
                _searchQueryFlow.value = intent.query // Триггерим debounce-поток
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
            _state.update { it.copy(isLoading = true, error = null) }
            getAllSubjectsUseCase() // Возвращает Flow<List<Subject>>
                .catch { e -> // Обработка ошибок на уровне сбора Flow
                    Log.e("SubjectViewModel", "Error loading initial subjects", e)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load subjects",
                            subjects = emptyList() // Очищаем список при ошибке
                        )
                    }
                    _fullSubjectList = emptyList()
                }
                .collectLatest { subjectsFromServer ->
                    Log.d("SubjectViewModel", "Loaded ${subjectsFromServer.size} subjects from server.")
                    _fullSubjectList = subjectsFromServer
                    // Фильтруем по текущему searchQuery (если он был введен до завершения загрузки)
                    filterSubjectsAndUpdateState(_state.value.searchQuery)
                    _state.update { it.copy(isLoading = false) } // Загрузка завершена
                }
        }
    }

    private fun filterSubjectsAndUpdateState(query: String) {
        val filtered = if (query.isBlank()) {
            _fullSubjectList
        } else {
            _fullSubjectList.filter { it.name.contains(query, ignoreCase = true) }
        }
        _state.update { it.copy(subjects = filtered) }
    }


    private fun handleFocusChange(isFocused: Boolean) {
        _state.update { it.copy(isSearchBarFocused = isFocused) }
        updateShowHistoryState()
    }

    private fun updateShowHistoryState() {
        _state.update { currentState ->
            val shouldShow = currentState.isSearchBarFocused &&
                    currentState.searchQuery.isBlank() && // Показываем историю только если поле поиска пустое
                    currentState.searchHistory.isNotEmpty()
            currentState.copy(showHistory = shouldShow)
        }
    }

    private fun submitSearchTerm(term: String) {
        val trimmedTerm = term.trim()
        if (trimmedTerm.isNotBlank()) {
            viewModelScope.launch {
                addSearchTermUseCase(trimmedTerm) // UseCase для добавления в историю
            }
        }
        // Скрываем историю и убираем фокус после сабмита
        _state.update { it.copy(showHistory = false, isSearchBarFocused = false) }
        // Фильтрация произойдет через _searchQueryFlow, если query изменился
    }

    private fun handleHistoryItemClick(term: String) {
        // Обновляем searchQuery в State и в _searchQueryFlow, чтобы запустить фильтрацию
        _state.update { it.copy(searchQuery = term, isSearchBarFocused = false, showHistory = false) }
        _searchQueryFlow.value = term
        viewModelScope.launch {
            addSearchTermUseCase(term) // Добавляем/обновляем в истории
        }
    }

    private fun clearUserSearchHistory() {
        viewModelScope.launch {
            clearSearchHistoryUseCase()
            // История обновится через Flow от getSearchHistoryUseCase,
            // и updateShowHistoryState скроет ее, если она стала пустой.
        }
    }

    private fun addNewSubject(name: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = addSubjectUseCase(name)) {
                is Result.Success -> {
                    // После успешного добавления нужно перезагрузить список предметов
                    // или, если сервер возвращает созданный объект, можно добавить его локально (сложнее с MVI)
                    // Проще вс его - инициировать перезагрузку.
                    Log.d("SubjectViewModel", "Subject added with ID: ${result.data}. Reloading subjects.")
                    loadInitialSubjects() // Перезагружаем весь список
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> { /* No-op для Result.Loading, если есть */ }
            }
        }
    }

    private fun updateUserSubject(subject: Subject) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Предположим, updateSubjectUseCase возвращает Result<Unit> или кидает Exception
            try {
                updateSubjectUseCase(subject) // suspend fun
                Log.d("SubjectViewModel", "Subject updated: ${subject.id}. Reloading subjects.")
                loadInitialSubjects() // Перезагружаем
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to update subject") }
            }
        }
    }

    private fun deleteUserSubject(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Предположим, deleteSubjectUseCase возвращает Result<Unit> или кидает Exception
            try {
                deleteSubjectUseCase(id) // suspend fun
                Log.d("SubjectViewModel", "Subject deleted: $id. Reloading subjects.")
                loadInitialSubjects() // Перезагружаем
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to delete subject") }
            }
        }
    }
}