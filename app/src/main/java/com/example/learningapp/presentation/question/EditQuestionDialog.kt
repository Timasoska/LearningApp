package com.example.learningapp.presentation.question

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningapp.domain.model.Question
import com.example.learningapp.presentation.question.QuestionIntent
import com.example.learningapp.presentation.question.QuestionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQuestionScreen(
    question: Question,
    viewModel: QuestionViewModel,
    onQuestionUpdated: () -> Unit
) {
    var title by remember { mutableStateOf(question.title) }
    var answer by remember { mutableStateOf(question.answer) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Редактировать вопрос") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (title.isNotBlank() && answer.isNotBlank()) {
                    val updatedQuestion = question.copy(title = title, answer = answer)
                    viewModel.processIntent(QuestionIntent.UpdateQuestion(updatedQuestion))
                    onQuestionUpdated()
                }
            }) {
                Icon(Icons.Default.Check, contentDescription = "Сохранить изменения")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Вопрос") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it },
                label = { Text("Ответ") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
