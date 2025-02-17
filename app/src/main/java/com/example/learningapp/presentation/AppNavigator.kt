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


        // 🌟 Splash Screen
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("subjects_list") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // 🌟 Экран списка предметов (нажатие на предмет → открывает список вопросов)
        composable(
            route = "questions_list/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0

            var showAddQuestionDialog by remember { mutableStateOf(false) }

            QuestionManagementScreen(
                subjectId = subjectId,
                navController = navController,
                viewModel = questionViewModel,
                onAddQuestionRequested = { showAddQuestionDialog = true },
                onEditQuestionRequested = { /* Редактировать вопрос */ },
                onDeleteQuestionRequested = { /* Удалить вопрос */ },
                onQuestionDetails = { questionId ->
                    navController.navigate("question_details/$questionId")
                }
            )

            if (showAddQuestionDialog) {
                AddQuestionDialog(
                    subjectId = subjectId, // <-- Передаём subjectId
                    viewModel = questionViewModel,
                    onQuestionAdded = { showAddQuestionDialog = false }
                )
            }
        }


        // 🌟 Экран добавления предмета
        composable("add_subject") {
            AddSubjectScreen(
                viewModel = subjectViewModel,
                onSubjectAdded = { navController.popBackStack() }
            )
        }

        // 🌟 Экран списка вопросов (по конкретному предмету)
        composable(
            route = "questions_list/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0

            var questionToDelete by remember { mutableStateOf<Question?>(null) }
            var showAddQuestionDialog by remember { mutableStateOf(false) }
            var questionToEdit by remember { mutableStateOf<Question?>(null) }

            QuestionManagementScreen(
                subjectId = subjectId,
                navController = navController,
                viewModel = questionViewModel,
                onAddQuestionRequested = { showAddQuestionDialog = true },
                onEditQuestionRequested = { question -> questionToEdit = question },
                onDeleteQuestionRequested = { question -> questionToDelete = question },
                onQuestionDetails = { questionId ->
                    navController.navigate("question_details/$questionId")
                }
            )

            if (questionToDelete != null) {
                DeleteQuestionDialog(
                    question = questionToDelete!!,
                    viewModel = questionViewModel,
                    onDeleteConfirmed = { questionToDelete = null },
                    onDismiss = { questionToDelete = null }
                )
            }
            if (showAddQuestionDialog) {
                AddQuestionDialog(
                    viewModel = questionViewModel,
                    onQuestionAdded = { showAddQuestionDialog = false },
                    subjectId = subjectId
                )
            }
            if (questionToEdit != null) {
                navController.navigate("edit_question/${questionToEdit!!.id}")
                questionToEdit = null
            }
        }

        // 🌟 Экран деталей вопроса
        composable(
            route = "question_details/{questionId}",
            arguments = listOf(navArgument("questionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt("questionId") ?: 0
            val question = questionViewModel.state.value.questions.collectAsState(initial = emptyList()).value
                .find { it.id == questionId }

            question?.let {
                QuestionDetailsScreen(
                    question = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // 🌟 Экран редактирования вопроса
        composable(
            route = "edit_question/{questionId}",
            arguments = listOf(navArgument("questionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt("questionId") ?: 0
            val question = questionViewModel.state.value.questions.collectAsState(initial = emptyList()).value
                .find { it.id == questionId }

            question?.let {
                EditQuestionScreen(
                    question = it,
                    viewModel = questionViewModel,
                    onQuestionUpdated = { navController.popBackStack() }
                )
            }
        }
    }
}


