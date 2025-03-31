package com.example.learningapp.presentation.subject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningapp.domain.model.Subject
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.onFocusChanged // --- ВОЗВРАЩАЕМ ---
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsListScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    onAddSubjectRequested: () -> Unit
) {
    // Собираем состояние из ViewModel
    val state by viewModel.state.collectAsState()

    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список предметов") },
                actions = {
                    IconButton(onClick = onAddSubjectRequested) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить предмет")
                    }
                    IconButton(onClick = { navController.navigate("theme_settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки темы")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Строка поиска
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.processIntent(SubjectIntent.SearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    // --- ВОЗВРАЩАЕМ обработчик фокуса ---
                    .onFocusChanged { focusState ->
                        viewModel.processIntent(SubjectIntent.SearchBarFocusChanged(focusState.isFocused))
                    },
                label = { Text("Поиск предметов...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // --- ВОЗВРАЩАЕМ SubmitSearch ---
                        viewModel.processIntent(SubjectIntent.SubmitSearch(state.searchQuery))
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.processIntent(SubjectIntent.SearchQueryChanged("")) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить поиск")
                        }
                    }
                }
            )

            // Отображение ProgressBar или Истории/Результатов
            Box(modifier = Modifier.fillMaxSize()) {
                // --- Условное отображение Истории или Результатов ---
                if (state.showHistory) {
                    // --- Показываем Историю ---
                    SearchHistoryList(
                        history = state.searchHistory,
                        onHistoryItemClick = { term ->
                            viewModel.processIntent(SubjectIntent.HistoryItemClicked(term))
                            // Фокус и клавиатура управляются ViewModel
                        },
                        onClearHistoryClick = {
                            viewModel.processIntent(SubjectIntent.ClearSearchHistory)
                        }
                    )
                } else {
                    // --- Показываем ProgressBar или Результаты ---
                    // Показываем ProgressBar если isLoading И (список пуст ИЛИ запрос непустой - т.е. идет debounce/фильтрация)
                    val showLoadingIndicator = state.isLoading && (state.filteredSubjects.isEmpty() || state.searchQuery.isNotEmpty())
                    if (showLoadingIndicator) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else {
                        // Показываем список результатов
                        SubjectResultList(
                            subjects = state.filteredSubjects,
                            // Сообщение "не найдено", если не грузим и список пуст
                            showEmptyMessage = !state.isLoading && state.filteredSubjects.isEmpty(),
                            onSubjectClick = { subject ->
                                navController.navigate("questions_list/${subject.id}")
                                // --- ВОЗВРАЩАЕМ добавление в историю при клике ---
                                viewModel.processIntent(SubjectIntent.SubmitSearch(subject.name))
                                focusManager.clearFocus() // Убираем фокус с поиска
                            },
                            onEditClick = { subject -> subjectToEdit = subject },
                            onDeleteClick = { subject -> subjectToDelete = subject }
                        )
                    }
                }

                // Отображение ошибки
                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                    )
                }
            }
        }
    }

    // Диалоги редактирования и удаления (без изменений)
    // Если subjectToEdit не null (т.е. пользователь нажал "Редактировать"),
    // показываем диалог редактирования
    subjectToEdit?.let { subject -> // `it` здесь будет равно subjectToEdit
        EditSubjectDialog(
            subject = subject,      // Передаем выбранный предмет
            viewModel = viewModel,  // Передаем ViewModel для вызова UpdateSubject
            onDismiss = {           // Лямбда, которая вызовется при закрытии диалога
                subjectToEdit = null // Сбрасываем состояние, чтобы диалог скрылся
            }
        )
    }

    // Если subjectToDelete не null (т.е. пользователь нажал "Удалить"),
    // показываем диалог подтверждения удаления
    subjectToDelete?.let { subject -> // `it` здесь будет равно subjectToDelete
        // Используем существующий Composable DeleteSubjectScreen,
        // который по сути является AlertDialog'ом
        DeleteSubjectScreen(
            subject = subject,          // Передаем выбранный предмет
            viewModel = viewModel,      // Передаем ViewModel для вызова DeleteSubject
            onDeleteConfirmed = {       // Лямбда при подтверждении удаления
                subjectToDelete = null  // Сбрасываем состояние, чтобы диалог скрылся
            },
            onDismiss = {               // Лямбда при отмене или закрытии
                subjectToDelete = null  // Сбрасываем состояние, чтобы диалог скрылся
            }
        )
    }
}

// --- Компонент SearchHistoryList (ВОЗВРАЩАЕМ) ---
@Composable
fun SearchHistoryList(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("История поиска", style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = onClearHistoryClick) {
                Text("Очистить историю")
            }
        }
        LazyColumn {
            items(history) { term ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHistoryItemClick(term) }
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(text = term, style = MaterialTheme.typography.bodyMedium)
                }
                Divider()
            }
        }
    }
}

// Обновляем SubjectResultList, чтобы он мог показывать сообщение о пустом списке
@Composable
fun SubjectResultList(
    subjects: List<Subject>,
    showEmptyMessage: Boolean, // Новый параметр
    onSubjectClick: (Subject) -> Unit,
    onEditClick: (Subject) -> Unit,
    onDeleteClick: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showEmptyMessage) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Предметы не найдены")
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(subjects, key = { it.id }) { subject ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSubjectClick(subject) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subject.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row {
                            IconButton(onClick = { onEditClick(subject) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                            }
                            IconButton(onClick = { onDeleteClick(subject) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}
