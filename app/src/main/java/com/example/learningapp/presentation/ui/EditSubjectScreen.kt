package com.example.learningapp.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubjectScreen(
    subject: Subject,
    onSubjectUpdated: () -> Unit,
    viewModel: SubjectViewModel
) {
    var subjectName by remember { mutableStateOf(subject.name) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Редактировать предмет") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (subjectName.isNotBlank()) {
                    // Отправляем intent обновления
                    viewModel.processIntent(SubjectIntent.UpdateSubject(Subject(subject.id, subjectName)))
                    onSubjectUpdated()
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
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Название предмета") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
