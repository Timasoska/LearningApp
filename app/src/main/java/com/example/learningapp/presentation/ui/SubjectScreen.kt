package com.example.learningapp.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.learningapp.domain.model.Subject
import kotlinx.coroutines.flow.toList
import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add

@Composable
fun SubjectScreen(viewModel: SubjectViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.processIntent(SubjectIntent.LoadSubject)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            SubjectList(
                subjects = state.subjects,
                onDelete = { id ->
                    viewModel.processIntent(SubjectIntent.DeleteSubject(id))
                },
                onItemClick = { id ->
                    viewModel.processIntent(SubjectIntent.LoadSubjectById(id))
                }
            )
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Subject")
        }

        state.error?.let { error ->
            AlertDialog(
                onDismissRequest = {
                    viewModel.processIntent(SubjectIntent.LoadSubject)
                },
                title = { Text("Ошибка") },
                text = { Text(error) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.processIntent(SubjectIntent.LoadSubject)
                        }
                    ) {
                        Text("Повторить")
                    }
                }
            )
        }

        if (showAddDialog) {
            var subjectName by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Добавить предмет") },
                text = {
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = { subjectName = it },
                        label = { Text("Название предмета") }
                    )
                },
                confirmButton = {
                    Button(
                        enabled = subjectName.isNotBlank(),
                        onClick = {
                            viewModel.processIntent(SubjectIntent.AddSubject(subjectName))
                            showAddDialog = false
                        }
                    ) {
                        Text("Добавить")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showAddDialog = false }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Composable
private fun SubjectList(
    subjects: Flow<List<Subject>>,
    onDelete: (Int) -> Unit,
    onItemClick: (Int) -> Unit
) {
    val subjectList by subjects.collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(subjectList) { subject ->
            SubjectListItem(
                subject = subject,
                onDelete = { onDelete(subject.id) },
                onClick = { onItemClick(subject.id) }
            )
            Divider()
        }
    }
}

@Composable
private fun SubjectListItem(
    subject: Subject,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.bodyLarge
            )

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить"
                )
            }
        }
    }
}