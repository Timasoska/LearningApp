package com.example.learningapp.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningapp.domain.model.Question
import com.example.learningapp.presentation.question.QuestionIntent
import com.example.learningapp.presentation.question.QuestionViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.learningapp.domain.model.Association

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    subjectId: Int,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    var associationDialogQuestion by remember { mutableStateOf<Question?>(null) }
    var associationText by remember { mutableStateOf("") }

    val state by viewModel.state.collectAsState()
    val associations by viewModel.associations.collectAsState()

    LaunchedEffect(subjectId) {
        viewModel.processIntent(QuestionIntent.LoadQuestionsBySubject(subjectId))
    }

    Column(modifier = Modifier.padding(16.dp)) {
        val questions by state.questions.collectAsState(initial = emptyList())

        LazyColumn {
            items(questions) { question ->
                val questionAssociations = associations[question.id] ?: emptyList()
                QuestionItem(
                    question = question,
                    associations = questionAssociations,
                    onEdit = { /* логика редактирования */ },
                    onDelete = { viewModel.processIntent(QuestionIntent.DeleteQuestion(question.id)) },
                    onAddAssociation = {
                        associationText = ""
                        associationDialogQuestion = question
                    },
                    onDeleteAssociation = { associationId ->
                        viewModel.processIntent(QuestionIntent.DeleteAssociation(associationId))
                    }
                )
            }
        }
    }

    associationDialogQuestion?.let { questionForAssoc ->
        AlertDialog(
            onDismissRequest = { associationDialogQuestion = null },
            title = { Text(text = "Добавить ассоциацию к вопросу") },
            text = {
                TextField(
                    value = associationText,
                    onValueChange = { associationText = it },
                    label = { Text("Ассоциация") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.processIntent(
                        QuestionIntent.AddAssociation(
                            Association(
                                id = 0,
                                questionId = questionForAssoc.id,
                                association = associationText
                            )
                        )
                    )
                    associationDialogQuestion = null
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Button(onClick = { associationDialogQuestion = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}


@Composable
fun QuestionItem(
    question: Question,
    associations: List<Association>,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddAssociation: () -> Unit,
    onDeleteAssociation: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Вопрос: ${question.title}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ответ: ${question.answer}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (associations.isNotEmpty()) {
                Text(text = "Ассоциации:", style = MaterialTheme.typography.titleSmall)
                Column {
                    associations.forEach { association ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "- ${association.association}")
                            IconButton(onClick = { onDeleteAssociation(association.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Удалить ассоциацию")
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Удалить")
                }
                IconButton(onClick = onAddAssociation) {
                    Icon(imageVector = Icons.Default.MailOutline, contentDescription = "Добавить ассоциацию")
                }
            }
        }
    }
}


