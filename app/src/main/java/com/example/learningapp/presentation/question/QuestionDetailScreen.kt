package com.example.learningapp.presentation.question

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    questionId: Int,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val question = state.currentQuestion

    // Загрузка вопроса при запуске экрана
    LaunchedEffect(questionId) {
        viewModel.processIntent(QuestionIntent.LoadQuestionById(questionId))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Детали вопроса") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            question?.let { q ->
                Text(
                    text = q.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = q.answer,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (q.isLearned) "Изучено" else "Не изучено",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (q.isLearned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}