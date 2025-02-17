package com.example.learningapp.presentation.subject

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectScreen(
    onSubjectAdded: () -> Unit,
    viewModel: SubjectViewModel
){
    var subjectName by remember { mutableStateOf("")}
    Scaffold(
        topBar = { TopAppBar(title = {Text("Добавить предмет")})},
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if(subjectName.isNotBlank()){
                    viewModel.processIntent(SubjectIntent.AddSubject(subjectName))
                    onSubjectAdded() //Возможно тут будет ошибка и это нужно будет убрать
                }
            }) {
                Icon(Icons.Default.Check, contentDescription = "Save")
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
                onValueChange = { subjectName = it},
                label = { Text("Название предмета") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}