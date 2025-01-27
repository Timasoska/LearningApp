package com.example.learningapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.learningapp.presentation.navigation.NavGraph
import com.example.learningapp.presentation.question.QuestionListScreen
import com.example.learningapp.presentation.question.QuestionViewModel
import com.example.learningapp.presentation.ui.theme.LearningAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: QuestionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearningAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LearningApp()
                }
            }
        }
    }
}

@Composable
fun LearningApp(){
    LearningAppTheme {
        NavGraph()
    }
}