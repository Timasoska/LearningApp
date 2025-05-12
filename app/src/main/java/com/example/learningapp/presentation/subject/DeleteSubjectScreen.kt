package com.example.learningapp.presentation.subject

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectViewModel

@Composable
fun DeleteSubjectScreen(
    subject: Subject,
    onDeleteConfirmed: () -> Unit, // Вызывается для закрытия диалога/обновления UI после подтверждения
    onDismiss: () -> Unit,         // Вызывается для закрытия диалога при отмене
    viewModel: SubjectViewModel
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить предмет") },
        text = { Text("Вы действительно хотите удалить предмет \"${subject.name}\"?") },
        confirmButton = {
            TextButton(onClick = {
                // Используем новое имя интента: DeleteExistingSubject
                viewModel.processIntent(SubjectIntent.DeleteExistingSubject(subject.id))
                onDeleteConfirmed() // Вызываем после отправки интента
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