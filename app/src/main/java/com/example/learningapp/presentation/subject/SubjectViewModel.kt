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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*


@OptIn(FlowPreview::class) // Для debounce
@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val addSubjectUseCase: AddSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase,
    private val getAllSubjectsUseCase: GetAllSubjectsUseCase,
    private val getSubjectByIdUseCase: GetSubjectByIdUseCase, // Возможно, для другого экрана
    private val updateSubjectUseCase: UpdateSubjectUseCase
) : ViewModel() {

    // Основной StateFlow для UI
    private val _state = MutableStateFlow(SubjectState())
    val state: StateFlow<SubjectState> = _state.asStateFlow()

    // Внутренний StateFlow для поискового запроса для debounce
    private val _searchQuery = MutableStateFlow("")

    // Flow для необработанных предметов из UseCase
    // Добавляем обработку ошибок и начальное состояние загрузки
    private val subjectsDataFlow: Flow<List<Subject>> = getAllSubjectsUseCase()
        .onStart { _state.update { it.copy(isLoading = true, error = null) } } // Начать загрузку
        .catch { e ->
            // Обработать ошибку загрузки
            _state.update { it.copy(isLoading = false, error = "Ошибка загрузки: ${e.message}") }
            emit(emptyList<Subject>()) // Отправить пустой список при ошибке, чтобы combine работал
        }


    init {
        // 1. Устанавливаем сам Flow в состояние (если это требование)
        // Это менее практично для прямого использования в combine ниже,
        // но соответствует структуре State, если она нужна где-то еще.
        // _state.update { it.copy(subjects = subjectsDataFlow) }

        // 2. Запускаем combine для получения отфильтрованных данных
        viewModelScope.launch {
            combine(
                subjectsDataFlow, // Используем flow данных напрямую
                _searchQuery.debounce(300) // Дебаунс для поискового запроса
            ) { subjectsList, query ->
                // Эта лямбда вызывается, когда приходит новый список ИЛИ новый запрос (после debounce)
                // Возвращаем пару: полный список и отфильтрованный список
                Pair(subjectsList, filterSubjects(subjectsList, query))
            }
                .onEach { (_, _) ->
                    // Выключаем индикатор загрузки ПОСЛЕ того, как combine отработал
                    _state.update { it.copy(isLoading = false) }
                }
                .collectLatest { (fullList, filteredList) ->
                    // Обновляем состояние последним отфильтрованным списком
                    // и сохраняем актуальный поисковый запрос
                    _state.update { currentState ->
                        currentState.copy(
                            // Здесь не обновляем subjects: Flow, он остается изначальным
                            searchQuery = _searchQuery.value, // Берем актуальное значение запроса
                            filteredSubjects = filteredList
                            // isLoading = false - уже обработано в onEach
                        )
                    }
                }
        }

        // 3. Отдельно обновляем isLoading при изменении searchQuery для индикации во время debounce
        viewModelScope.launch {
            _searchQuery.collect { query ->
                // Показываем isLoading если запрос не пуст (т.е. идет ввод/debounce)
                // И выключаем isLoading если запрос пуст
                val currentlyLoading = _state.value.isLoading // Запомним текущее состояние
                val shouldBeLoading = query.isNotEmpty()
                // Обновляем только если состояние должно измениться или если запрос меняется
                if(currentlyLoading != shouldBeLoading || _state.value.searchQuery != query) {
                    _state.update { it.copy(searchQuery = query, isLoading = shouldBeLoading) }
                }
            }
        }
    }

    fun processIntent(intent: SubjectIntent) {
        when (intent) {
            is SubjectIntent.SearchQueryChanged -> {
                // Обновляем внутренний StateFlow запроса
                _searchQuery.value = intent.query
            }
            SubjectIntent.LoadSubjects -> {
                // Для явного обновления - можно использовать триггерный Flow,
                // но обычно Flow от Room/UseCase сам обновится после Add/Delete/Update
                _state.update { it.copy(isLoading = true) } // Показываем загрузку при попытке обновить
            }
            is SubjectIntent.AddSubject -> addSubject(intent.name)
            is SubjectIntent.DeleteSubject -> deleteSubject(intent.id)
            is SubjectIntent.LoadSubjectById -> { /* Логика для другого экрана */ }
            is SubjectIntent.UpdateSubject -> updateSubject(intent.subject)
        }
    }

    // Функция фильтрации остается той же
    private fun filterSubjects(subjects: List<Subject>, query: String): List<Subject> {
        return if (query.isBlank()) {
            subjects
        } else {
            subjects.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
    }

    // --- CRUD операции ---
    // Они вызывают UseCase, который меняет данные в БД.
    // Flow от getAllSubjectsUseCase() автоматически эмитит новый список.
    // Запущенный combine в init среагирует на новый список и обновит filteredSubjects.
    private fun addSubject(name: String) {
        viewModelScope.launch {
            try {
                // Показываем загрузку на время операции? Опционально.
                // _state.update { it.copy(isLoading = true) }
                addSubjectUseCase(name)
                // _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Ошибка добавления: ${e.message}") }
            }
        }
    }

    private fun deleteSubject(id: Int) {
        viewModelScope.launch {
            try {
                 _state.update { it.copy(isLoading = true) }
                deleteSubjectUseCase(id)
                 _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Ошибка удаления: ${e.message}") }
            }
        }
    }

    private fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            try {
                 _state.update { it.copy(isLoading = true) }
                updateSubjectUseCase(subject)
                 _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Ошибка обновления: ${e.message}")
                }
            }
        }
    }
    private fun loadSubjectById(id: Int){
        // Для экрана деталей, может установить currentSubject
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val subject = getSubjectByIdUseCase(id)
                _state.update { it.copy(isLoading = false, currentSubject = subject) }
            } catch(e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Не удалось загрузить предмет $id: ${e.message}") }
            }
        }
    }
}