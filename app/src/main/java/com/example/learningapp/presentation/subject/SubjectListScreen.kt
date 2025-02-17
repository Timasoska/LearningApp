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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsListScreen(
    navController: NavController,
    viewModel: SubjectViewModel,
    onEditSubjectRequested: (Subject) -> Unit,
    onDeleteSubjectRequested: (Subject) -> Unit
) {
    // Загружаем предметы при открытии экрана
    LaunchedEffect(Unit) {
        viewModel.processIntent(SubjectIntent.LoadSubject)
    }
    val state by viewModel.state.collectAsState()
    val subjects by state.subjects.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Список предметов") }) }
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
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        // По нажатию на предмет переходим на экран вопросов
                        Text(
                            text = subject.name,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    navController.navigate("questions_list/${subject.id}")
                                },
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row {
                            // Иконка редактирования
                            IconButton(onClick = { onEditSubjectRequested(subject) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                            }
                            // Иконка удаления
                            IconButton(onClick = { onDeleteSubjectRequested(subject) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить")
                            }
                        }
                    }
                }
            }
        }
    }
}

