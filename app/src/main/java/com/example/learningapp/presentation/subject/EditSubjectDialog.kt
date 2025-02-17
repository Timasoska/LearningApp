package com.example.learningapp.presentation.subject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectViewModel

@Composable
fun EditSubjectDialog(
    subject: Subject,
    viewModel: SubjectViewModel,
    onDismiss: () -> Unit
) {
    var subjectName by remember { mutableStateOf(subject.name) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать предмет") },
        text = {
            Column {
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Название предмета") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (subjectName.isNotBlank()) {
                    viewModel.processIntent(SubjectIntent.UpdateSubject(Subject(subject.id, subjectName)))
                    onDismiss()
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
