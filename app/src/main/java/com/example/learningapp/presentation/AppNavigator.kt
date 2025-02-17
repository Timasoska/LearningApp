package com.example.learningapp.presentation

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learningapp.presentation.subject.AddSubjectScreen
import com.example.learningapp.presentation.subject.DeleteSubjectScreen
import com.example.learningapp.presentation.subject.EditSubjectDialog
import com.example.learningapp.presentation.subject.SubjectsListScreen
import com.example.learningapp.presentation.ui.SplashScreen
import androidx.navigation.navArgument
import com.example.learningapp.presentation.question.AddQuestionDialog
import com.example.learningapp.presentation.question.DeleteQuestionDialog
import com.example.learningapp.presentation.question.EditQuestionScreen
import com.example.learningapp.presentation.question.QuestionDetailsScreen
import com.example.learningapp.presentation.question.QuestionManagementScreen
import com.example.learningapp.presentation.ui.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.question.QuestionViewModel
import com.example.learningapp.presentation.subject.SubjectIntent
import com.example.learningapp.presentation.subject.SubjectViewModel
import com.example.learningapp.presentation.ui.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavigation(
    subjectViewModel: SubjectViewModel,
    questionViewModel: QuestionViewModel
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("subjects_list") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("subjects_list") {
            SubjectsListScreen(
                navController = navController,
                viewModel = subjectViewModel,
                onAddSubjectRequested = {
                    navController.navigate("add_subject")
                }
            )
        }

        composable("add_subject") {
            AddSubjectScreen(
                viewModel = subjectViewModel,
                onSubjectAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "questions_list/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            QuestionManagementScreen(
                subjectId = subjectId,
                navController = navController,
                viewModel = questionViewModel,
                onAddQuestionRequested = { navController.navigate("add_question/$subjectId") },
                onEditQuestionRequested = { /* Handle edit question */ },
                onDeleteQuestionRequested = { /* Handle delete question */ },
                onQuestionDetails = { questionId ->
                    navController.navigate("question_details/$questionId")
                }
            )
        }

        composable(
            route = "add_question/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            AddQuestionDialog(
                subjectId = subjectId,
                viewModel = questionViewModel,
                onQuestionAdded = {
                    navController.popBackStack() // Возвращаемся назад после добавления
                }
            )
        }
    }
}


