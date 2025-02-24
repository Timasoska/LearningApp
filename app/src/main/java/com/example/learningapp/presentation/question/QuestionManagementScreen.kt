package com.example.learningapp.presentation.question

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningapp.domain.model.Question
import com.example.learningapp.presentation.question.QuestionIntent
import com.example.learningapp.presentation.question.QuestionViewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionManagementScreen(
    subjectId: Int,  // если нужен фильтр по предмету
    navController: NavController,
    viewModel: QuestionViewModel,
    onAddQuestionRequested: () -> Unit,
    onEditQuestionRequested: (Question) -> Unit,
    onDeleteQuestionRequested: (Question) -> Unit,
    onQuestionDetails: (Int) -> Unit
) {
    // Загружаем вопросы при открытии экрана
    LaunchedEffect(Unit) {
        viewModel.processIntent(QuestionIntent.LoadQuestions)
    }
    val state by viewModel.state.collectAsState()
    val questions by state.questions.collectAsState(initial = emptyList())

    // Состояние для поискового запроса
    var searchQuery by remember { mutableStateOf("") }
    val filteredQuestions = if (searchQuery.isEmpty()) {
        questions
    } else {
        questions.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Список вопросов") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddQuestionRequested) {
                Icon(Icons.Default.Add, contentDescription = "Добавить вопрос")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Строка поиска
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Поиск по названию") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredQuestions) { question ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onQuestionDetails(question.id) }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = question.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = question.answer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row {
                                IconButton(onClick = { onEditQuestionRequested(question) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                                }
                                IconButton(onClick = { onDeleteQuestionRequested(question) }) {
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


