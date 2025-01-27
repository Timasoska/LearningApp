package com.example.learningapp.presentation.question

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.learningapp.domain.model.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionListScreen(
    navController: NavController,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    // Загрузка вопросов при запуске экрана
    LaunchedEffect(Unit) {
        viewModel.processIntent(QuestionIntent.LoadQuestions)
    }

    val state by viewModel.state.collectAsState()
    val questions = state.questions

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Список вопросов") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(questions) { question ->
                QuestionItem(
                    question = question,
                    onClick = {
                        navController.navigate("question_detail/${question.id}")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun QuestionItem(
    question: Question,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (question.isLearned) "Изучено" else "Не изучено",
                style = MaterialTheme.typography.bodySmall,
                color = if (question.isLearned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}