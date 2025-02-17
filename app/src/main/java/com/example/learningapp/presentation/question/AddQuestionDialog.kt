package com.example.learningapp.presentation.question

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningapp.domain.model.Question
import com.example.learningapp.presentation.question.QuestionIntent
import com.example.learningapp.presentation.question.QuestionViewModel

@Composable
fun AddQuestionDialog(
    subjectId: Int,
    viewModel: QuestionViewModel,
    onQuestionAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onQuestionAdded,
        title = { Text("Добавить вопрос") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Вопрос") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    label = { Text("Ответ") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isNotBlank() && answer.isNotBlank()) {
                    val question = Question(
                        id = 0, title = title, answer = answer, isLearned = false,
                        subjectId = subjectId
                    )
                    viewModel.processIntent(QuestionIntent.AddQuestion(question))
                    onQuestionAdded()
                }
            }) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onQuestionAdded) {
                Text("Отмена")
            }
        }
    )
}
