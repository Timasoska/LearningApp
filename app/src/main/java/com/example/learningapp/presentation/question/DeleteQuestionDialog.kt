package com.example.learningapp.presentation.question

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.learningapp.domain.model.Question
import com.example.learningapp.presentation.question.QuestionIntent
import com.example.learningapp.presentation.question.QuestionViewModel

@Composable
fun DeleteQuestionDialog(
    question: Question,
    viewModel: QuestionViewModel,
    onDeleteConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить вопрос") },
        text = { Text("Вы действительно хотите удалить вопрос \"${question.title}\"?") },
        confirmButton = {
            TextButton(onClick = {
                viewModel.processIntent(QuestionIntent.DeleteQuestion(question.id))
                onDeleteConfirmed()
            }) {
                Text("Удалить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
