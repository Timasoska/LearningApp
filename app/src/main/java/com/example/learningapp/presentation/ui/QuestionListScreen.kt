package com.example.learningapp.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.question.QuestionIntent
import com.example.learningapp.presentation.question.QuestionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsListScreen(
    subject: Subject,
    onAddQuestion: () -> Unit,
    onQuestionClick: (Question) -> Unit,
    viewModel: QuestionViewModel
) {
    // Загружаем вопросы (возможно, нужно добавить фильтрацию по subject.id)
    LaunchedEffect(subject.id) {
        viewModel.processIntent(QuestionIntent.LoadQuestions)
    }

    val state by viewModel.state.collectAsState()
    val questions by state.questions.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Вопросы по ${subject.name}") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddQuestion) {
                Icon(Icons.Default.Add, contentDescription = "Добавить вопрос")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(questions) { question ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onQuestionClick(question) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = question.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = question.answer, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
