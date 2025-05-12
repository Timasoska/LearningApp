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
            // Строка поиска (здесь filteredSubjects не используется)
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.processIntent(SubjectIntent.SearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .onFocusChanged { focusState ->
                        viewModel.processIntent(SubjectIntent.SearchBarFocusChanged(focusState.isFocused))
                    },
                label = { Text("Поиск предметов...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.processIntent(SubjectIntent.SubmitSearch(state.searchQuery))
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    // Здесь filteredSubjects не используется
                    if (state.isLoading && state.searchQuery.isNotEmpty()) { // Показываем индикатор во время активного поиска
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
                if (state.showHistory) {
                    SearchHistoryList(
                        history = state.searchHistory,
                        onHistoryItemClick = { term ->
                            viewModel.processIntent(SubjectIntent.HistoryItemClicked(term))
                        },
                        onClearHistoryClick = {
                            viewModel.processIntent(SubjectIntent.ClearSearchHistory)
                        }
                    )
                } else {
                    // Условие для индикатора загрузки:
                    // Показываем, если isLoading=true И (либо это начальная загрузка и список state.subjects пуст,
                    // ЛИБО идет активный поиск по непустому запросу)
                    val showLoadingIndicator = state.isLoading && (state.subjects.isEmpty() || state.searchQuery.isNotEmpty())

                    if (showLoadingIndicator) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else {
                        SubjectResultList(
                            subjects = state.subjects, // <<< ЗАМЕНА №1: Используем state.subjects
                            // Сообщение "не найдено" показывается, если:
                            // - Загрузка не идет
                            // - Список state.subjects пуст
                            // - И поисковый запрос state.searchQuery НЕ пустой
                            showEmptyMessage = !state.isLoading && state.subjects.isEmpty() && state.searchQuery.isNotBlank(), // <<< ЗАМЕНА №2: Условие использует state.subjects и state.searchQuery
                            onSubjectClick = { subject ->
                                navController.navigate("questions_list/${subject.id}")
                                viewModel.processIntent(SubjectIntent.SubmitSearch(subject.name))
                                focusManager.clearFocus()
                            },
                            onEditClick = { subject -> subjectToEdit = subject },
                            onDeleteClick = { subject -> subjectToDelete = subject }
                        )
                    }
                }

                // Отображение ошибки (здесь filteredSubjects не используется)
                if (state.error != null) {
                    Text(
                        text = state.error ?: "Неизвестная ошибка",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                    )
                }
            }
        }
    }

    // Диалоги (здесь filteredSubjects не используется)
    subjectToEdit?.let { subject ->
        EditSubjectDialog(
            subject = subject,
            viewModel = viewModel,
            onDismiss = { subjectToEdit = null }
        )
    }

    subjectToDelete?.let { subject ->
        DeleteSubjectScreen(
            subject = subject,
            viewModel = viewModel,
            onDeleteConfirmed = { subjectToDelete = null },
            onDismiss = { subjectToDelete = null }
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
    subjects: List<Subject>, // Принимает уже state.subjects
    showEmptyMessage: Boolean, // true, если "не найдено по запросу"
    onSubjectClick: (Subject) -> Unit,
    onEditClick: (Subject) -> Unit,
    onDeleteClick: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showEmptyMessage) { // Если искали и не нашли
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Предметы не найдены по вашему запросу.") // Это сообщение теперь корректно
        }
    } else if (subjects.isEmpty()) { // Если список просто пуст (не было поиска или все удалили)
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Список предметов пуст. Нажмите '+' для добавления.")
        }
    }
    else {
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
