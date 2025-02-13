package com.example.learningapp.presentation.ui

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsListScreen(
    viewModel: SubjectViewModel,
    onEditSubject: (Subject) -> Unit, // ✅ Добавили параметр
    onDeleteSubject: (Subject) -> Unit,
    onSubjectClick: (Subject) -> Unit,
    onAddSubject: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.processIntent(SubjectIntent.LoadSubject)
    }

    val state by viewModel.state.collectAsState()
    val subjects by state.subjects.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Список предметов") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSubject) {
                Icon(Icons.Default.Add, contentDescription = "Добавить предмет")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(subjects) { subject ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Название предмета (по нажатию открывает список вопросов)
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSubjectClick(subject) }
                        )

                        // Кнопки "Редактировать" и "Удалить"
                        Row {
                            IconButton(onClick = { onEditSubject(subject) }) { // ✅ Теперь работает
                                Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                            }
                            IconButton(onClick = { onDeleteSubject(subject) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}


