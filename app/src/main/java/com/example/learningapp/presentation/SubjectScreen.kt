package com.example.learningapp.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectState
import com.example.learningapp.presentation.subject.SubjectViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SubjectScreen(
    viewModel: SubjectViewModel = hiltViewModel()
) {
    // Локальные состояния для диалога
    var showDialog by remember { mutableStateOf(false) }
    var subjectName by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.processIntent(SubjectIntent.LoadSubject)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Открываем диалог для добавления нового предмета
                subjectName = ""
                isEditing = false
                subjectToEdit = null
                showDialog = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Добавить предмет")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Text(text = "Ошибка: ${state.error}", color = MaterialTheme.colorScheme.error)
                }
                else -> {
                    // Предполагаем, что state.subjects содержит поток списка предметов.
                    // Для простоты преобразуем его в состояние.
                    val subjects by state.subjects.collectAsState(initial = emptyList())
                    LazyColumn {
                        items(subjects) { subject ->
                            SubjectItem(
                                subject = subject,
                                onEdit = {
                                    subjectName = subject.name
                                    isEditing = true
                                    subjectToEdit = subject
                                    showDialog = true
                                },
                                onDelete = {
                                    viewModel.processIntent(SubjectIntent.DeleteSubject(subject.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Диалог для добавления / редактирования предмета
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = if (isEditing) "Редактировать предмет" else "Добавить предмет") },
            text = {
                TextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Название предмета") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (isEditing && subjectToEdit != null) {
                        val updatedSubject = subjectToEdit!!.copy(name = subjectName)
                        viewModel.processIntent(SubjectIntent.UpdateSubject(updatedSubject))
                    } else {
                        // При добавлении id = 0, чтобы Room сгенерировал новый id
                        viewModel.processIntent(SubjectIntent.AddSubject(Subject(id = 0, name = subjectName)))
                    }
                    showDialog = false
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun SubjectItem(
    subject: Subject,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = subject.name, modifier = Modifier.weight(1f))
            IconButton(onClick = onEdit) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Редактировать")
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}
