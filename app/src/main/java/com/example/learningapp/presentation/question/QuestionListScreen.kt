package com.example.learningapp.presentation.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun QuestionListScreen(viewModel: QuestionViewModel) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { viewModel.processIntent(QuestionIntent.LoadQuestions) }
        ) {
            Text("Загрузить вопросы")
        }

        when {
            state.isLoading -> CircularProgressIndicator()
            state.error != null -> Text("Ошибка: ${state.error}", color = Color.Red)
            else -> Text("Загружено вопросов: ${state.questions.size}")
        }
    }
}