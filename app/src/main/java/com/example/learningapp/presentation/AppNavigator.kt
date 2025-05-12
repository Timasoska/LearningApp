package com.example.learningapp.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
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
import com.example.learningapp.presentation.authorization.LoginScreen
import com.example.learningapp.presentation.authorization.RegistrationScreen
import com.example.learningapp.presentation.question.QuestionIntent
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
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("subjects_list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegistrationClick = {
                    navController.navigate("registration")
                }
            )
        }
        composable("registration") {
            RegistrationScreen(
                onRegistrationComplete = {
                    navController.navigate("login") {
                        popUpTo("registration") { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("registration") { inclusive = true }
                    }
                }
            )
        }
        composable("subjects_list") {
            SubjectsListScreen(
                navController = navController,
                viewModel = subjectViewModel,
                onAddSubjectRequested = { navController.navigate("add_subject") }
            )
        }
        composable("add_subject") {
            AddSubjectScreen(
                viewModel = subjectViewModel,
                onSubjectAdded = { navController.popBackStack() }
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
                onEditQuestionRequested = { question ->
                    navController.navigate("edit_question/${question.id}")
                },
                onQuestionDetails = { questionId ->
                    navController.navigate("question_details/$questionId")
                }
            )
        }
        composable(
            route = "edit_question/{questionId}",
            arguments = listOf(navArgument("questionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt("questionId") ?: 0
            LaunchedEffect(questionId) {
                questionViewModel.processIntent(QuestionIntent.LoadQuestionById(questionId))
                Log.d("AppNavigation", "EditQuestionScreen: LaunchedEffect for questionId: $questionId")
            }
            val questionState by questionViewModel.state.collectAsState()
            val currentQuestion = questionState.currentQuestion // currentQuestion теперь типа Question?
            Log.d("AppNavigation", "EditQuestionScreen: currentQuestion value: $currentQuestion")
            if (currentQuestion != null) {
                EditQuestionScreen(
                    question = currentQuestion,
                    viewModel = questionViewModel,
                    onQuestionUpdated = { navController.popBackStack() }
                )
            }else {
                // <<< ЧТО ПРОИСХОДИТ ЗДЕСЬ? Показывается ли что-то? Может, это и есть твой "белый экран"?
                Log.d(
                    "AppNavigation",
                    "EditQuestionScreen: currentQuestion is null. Displaying loading or empty state."
                )
            }
        }
        composable(
            route = "question_details/{questionId}",
            arguments = listOf(navArgument("questionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt("questionId") ?: 0
            LaunchedEffect(questionId) {
                questionViewModel.processIntent(QuestionIntent.LoadQuestionById(questionId))
            }
            val currentQuestion = questionViewModel.state.collectAsState().value.currentQuestion
            if (currentQuestion != null) {
                QuestionDetailsScreen(
                    question = currentQuestion,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(
            route = "add_question/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            AddQuestionDialog(
                subjectId = subjectId,
                viewModel = questionViewModel,
                onQuestionAdded = { navController.popBackStack() }
            )
        }
        // Новый маршрут для настроек темы
        composable("theme_settings") {
            ThemeSettingsScreen()
        }
    }
}


