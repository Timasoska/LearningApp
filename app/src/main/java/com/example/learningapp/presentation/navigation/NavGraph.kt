package com.example.learningapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.learningapp.presentation.question.QuestionDetailScreen
import com.example.learningapp.presentation.question.QuestionListScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "question_list"
    ) {
        composable("question_list") {
            QuestionListScreen(navController = navController)
        }
        composable("question_detail/{questionId}") { backStackEntry ->
            val questionId = backStackEntry.arguments?.getString("questionId")?.toIntOrNull() ?: 0
            QuestionDetailScreen(questionId = questionId)
        }
    }
}
