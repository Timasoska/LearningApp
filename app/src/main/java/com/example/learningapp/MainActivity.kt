package com.example.learningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.learningapp.presentation.AppNavigation
import com.example.learningapp.presentation.question.QuestionViewModel
import com.example.learningapp.presentation.subject.SubjectViewModel
import com.example.learningapp.presentation.subject.SubjectsListScreen
import com.example.learningapp.presentation.ui.SplashScreen
import com.example.learningapp.presentation.ui.theme.LearningAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val subjectViewModel: SubjectViewModel by viewModels()
    private val questionViewModel: QuestionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearningAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        subjectViewModel = subjectViewModel,
                        questionViewModel = questionViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    val app = LocalContext.current.applicationContext as App

    Column(modifier = Modifier.padding(16.dp)) {
        Switch(
            checked = app.isDarkTheme,
            onCheckedChange = { app.isDarkTheme = it },
            modifier = Modifier.fillMaxWidth(),
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        Text("Темная тема", style = MaterialTheme.typography.bodyLarge)
    }
}

