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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsListScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    onAddSubjectRequested: () -> Unit
) {
    // LaunchedEffect не нужен для явной загрузки, т.к. ViewModel делает это в init
    // LaunchedEffect(Unit) { viewModel.processIntent(SubjectIntent.LoadSubjects) }

    val state by viewModel.state.collectAsState()
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = { /* ... без изменений ... */ }
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
                    .padding(vertical = 8.dp),
                label = { Text("Поиск предметов...") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    // Индикатор загрузки/debounce
                    if (state.isLoading) { // Показываем всегда, когда isLoading = true
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    // Иконка очистки
                    else if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.processIntent(SubjectIntent.SearchQueryChanged("")) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистить поиск")
                        }
                    }
                }
            )

            // Отображение результатов
            Box(modifier = Modifier.fillMaxSize()) {
                // Показываем список отфильтрованных результатов
                // ProgressBar внутри списка не нужен, т.к. он в TextField/trailingIcon
                SubjectResultList(
                    // Используем отфильтрованный список из состояния
                    subjects = state.filteredSubjects,
                    // Показываем сообщение "не найдено", только если *не* идет загрузка
                    // и список действительно пуст
                    showEmptyMessage = !state.isLoading && state.filteredSubjects.isEmpty(),
                    onSubjectClick = { subject ->
                        navController.navigate("questions_list/${subject.id}")
                    },
                    onEditClick = { subject -> subjectToEdit = subject },
                    onDeleteClick = { subject -> subjectToDelete = subject }
                )

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
