package com.example.learningapp.presentation.question


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningapp.domain.model.Question
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.CheckCircle // Для выученного
import androidx.compose.material3.Checkbox // Альтернатива
import androidx.compose.ui.graphics.Color // Для изменения цвета
import androidx.compose.ui.platform.LocalContext
import com.example.learningapp.App
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionManagementScreen(
    subjectId: Int, // Этот ID теперь очень важен
    navController: NavController,
    viewModel: QuestionViewModel,
    onAddQuestionRequested: () -> Unit,
    onEditQuestionRequested: (Question) -> Unit,
    onQuestionDetails: (Int) -> Unit
) {
    var questionToDelete by remember { mutableStateOf<Question?>(null) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(subjectId) { // Загружаем вопросы при входе или смене subjectId
        Log.d("QMS", "LaunchedEffect: Loading questions for subjectId: $subjectId")
        viewModel.processIntent(QuestionIntent.LoadQuestionBySubject(subjectId))
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Список вопросов") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddQuestionRequested) {
                Icon(Icons.Default.Add, contentDescription = "Добавить вопрос")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.questions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.questions.isEmpty() && !state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error ?: "Вопросы для этого предмета еще не добавлены.")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.questions, key = { it.id }) { question ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (question.isLearned) Color.Green.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    Log.d("QMS", "Toggle learned for Q_ID: ${question.id}, newStatus: ${!question.isLearned}, subjectId: $subjectId")
                                    viewModel.processIntent(
                                        QuestionIntent.ToggleLearnedStatus(
                                            questionId = question.id,
                                            newStatus = !question.isLearned,
                                            subjectIdForReload = subjectId // Передаем subjectId для перезагрузки
                                        )
                                    )
                                }) {
                                    Icon(
                                        imageVector = if (question.isLearned) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                        contentDescription = if (question.isLearned) "Выучено" else "Не выучено",
                                        tint = if (question.isLearned) Color.Green.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                        .clickable { onQuestionDetails(question.id) } // Клик по тексту для деталей
                                ) {
                                    Text(text = question.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = question.answer,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(onClick = {
                                    Log.d("QMS", "Edit clicked for question: ID=${question.id}, Title='${question.title}'")
                                    onEditQuestionRequested(question)
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                                }
                                IconButton(onClick = { questionToDelete = question }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                                }
                            }
                        }
                    }
                }
            }
            // Отображение общей ошибки, если questions пуст и есть ошибка
            if (state.questions.isEmpty() && state.error != null && !state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }

    questionToDelete?.let { question ->
        DeleteQuestionDialog(
            question = question,
            subjectId = subjectId, // Передаем subjectId для перезагрузки списка
            viewModel = viewModel,
            onDeleteConfirmed = { questionToDelete = null },
            onDismiss = { questionToDelete = null }
        )
    }
}





