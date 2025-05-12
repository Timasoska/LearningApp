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
import android.util.Log // Добавь для логов
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner // <<< ЯВНЫЙ ИМПОРТ ПРАВИЛЬНОГО


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
    val lifecycleOwner = LocalLifecycleOwner.current

    // Эффект для загрузки данных при ON_RESUME, если это необходимо
    // (основная загрузка должна происходить во ViewModel при появлении userId)
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Log.d("SubjectsListScreen", "ON_RESUME event detected. Current state: isLoading=${state.isLoading}, error=${state.error}, subjectsEmpty=${state.subjects.isEmpty()}")
                // Повторно пытаемся загрузить, если была ошибка и список пуст,
                // или если список просто пуст, нет ошибки и не идет загрузка.
                // ViewModel сама проверит, залогинен ли пользователь, перед реальной загрузкой.
                if ((state.error != null && state.subjects.isEmpty()) ||
                    (state.subjects.isEmpty() && state.error == null && !state.isLoading)
                ) {
                    Log.d("SubjectsListScreen", "ON_RESUME: Triggering LoadInitialData.")
                    viewModel.processIntent(SubjectIntent.LoadInitialData)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
    ) { paddingValues -> // Используем paddingValues из Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Применяем paddingValues здесь
                .padding(horizontal = 16.dp) // Дополнительный горизонтальный padding
        ) {
            // Кнопка "Тест Обновить" (для отладки, можно убрать для финальной версии)
            Button(
                onClick = { viewModel.processIntent(SubjectIntent.LoadInitialData) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp) // Немного отступов
            ) {
                Text("Тест Обновить (принудительно)")
            }

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
                    if (state.isLoading && state.searchQuery.isNotEmpty() && state.subjects.isNotEmpty()) {
                        CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.processIntent(SubjectIntent.SearchQueryChanged("")) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить поиск")
                        }
                    }
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Log.d("SubjectsListScreen", "Recomposing Box: isLoading=${state.isLoading}, error='${state.error}', subjectsEmpty=${state.subjects.isEmpty()}, searchQueryBlank=${state.searchQuery.isBlank()}, showHistory=${state.showHistory}")
                when {
                    state.showHistory -> {
                        SearchHistoryList(
                            history = state.searchHistory,
                            onHistoryItemClick = { term ->
                                viewModel.processIntent(SubjectIntent.HistoryItemClicked(term))
                            },
                            onClearHistoryClick = {
                                viewModel.processIntent(SubjectIntent.ClearSearchHistory)
                            }
                        )
                    }
                    !state.isLoading && state.error != null && state.subjects.isEmpty() && state.searchQuery.isBlank() -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.error ?: "Произошла непредвиденная ошибка",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.processIntent(SubjectIntent.LoadInitialData) },
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("Попробовать снова")
                            }
                        }
                    }
                    state.isLoading && (state.subjects.isEmpty() || state.searchQuery.isNotEmpty()) -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    else -> {
                        SubjectResultList(
                            subjects = state.subjects,
                            showEmptyMessage = !state.isLoading && state.subjects.isEmpty() && state.searchQuery.isNotBlank(),
                            showEmptyListMessage = !state.isLoading && state.subjects.isEmpty() && state.searchQuery.isBlank() && state.error == null,
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
            }
        }
    }

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
        if (history.isEmpty()){
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("История поиска пуста.")
            }
        } else {
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
                            Icons.Outlined.DateRange, // Используем другую иконку для истории
                            contentDescription = "Запись истории",
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
}

@Composable
fun SubjectResultList(
    subjects: List<Subject>,
    showEmptyMessage: Boolean,
    showEmptyListMessage: Boolean,
    onSubjectClick: (Subject) -> Unit,
    onEditClick: (Subject) -> Unit,
    onDeleteClick: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        showEmptyMessage -> {
            Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Предметы не найдены по вашему запросу.", textAlign = TextAlign.Center)
            }
        }
        showEmptyListMessage -> {
            Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Список предметов пуст. Нажмите '+' для добавления.", textAlign = TextAlign.Center)
            }
        }
        subjects.isNotEmpty() -> {
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
}