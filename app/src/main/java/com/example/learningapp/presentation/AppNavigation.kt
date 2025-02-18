package com.example.learningapp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learningapp.domain.model.Question
import com.example.learningapp.domain.model.Subject
import com.example.learningapp.presentation.question.QuestionViewModel
import com.example.learningapp.presentation.subject.SubjectViewModel
import com.example.learningapp.presentation.ui.AddQuestionScreen
import com.example.learningapp.presentation.ui.AddSubjectScreen
import com.example.learningapp.presentation.ui.DeleteSubjectScreen
import com.example.learningapp.presentation.ui.EditQuestionScreen
import com.example.learningapp.presentation.ui.EditSubjectDialog
import com.example.learningapp.presentation.ui.EditSubjectScreen
import com.example.learningapp.presentation.ui.QuestionManagementScreen
import com.example.learningapp.presentation.ui.QuestionsListScreen
import com.example.learningapp.presentation.ui.SplashScreen
import com.example.learningapp.presentation.ui.SubjectDetailsScreen
import com.example.learningapp.presentation.ui.SubjectsListScreen


@Composable
fun AppNavigation(
    subjectViewModel: SubjectViewModel,
    questionViewModel: QuestionViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        // 1. Экран загрузки
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("subjects_list") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("subjects_list") {
            var subjectToEdit by remember { mutableStateOf<Subject?>(null) } // Храним предмет для редактирования

            SubjectsListScreen(
                viewModel = subjectViewModel,
                onSubjectClick = { subject ->
                    navController.navigate("subject_details/${subject.id}") // ✅ Открываем детали
                },
                onEditSubject = { subject ->
                    subjectToEdit = subject // ✅ Показываем диалог вместо перехода на экран
                },
                onDeleteSubject = { subject ->
                    navController.navigate("delete_subject/${subject.id}") // ✅ Удаление
                },
                onAddSubject = { navController.navigate("add_subject") }
            )

            // Показываем диалог, если выбран предмет для редактирования
            subjectToEdit?.let { subject ->
                EditSubjectDialog(
                    subject = subject,
                    viewModel = subjectViewModel,
                    onDismiss = { subjectToEdit = null } // Закрываем диалог
                )
            }
        }



        // 3. Экран добавления предмета
        composable("add_subject") {
            AddSubjectScreen(
                viewModel = subjectViewModel,
                onSubjectAdded = { navController.popBackStack() }
            )
        }

        // 4. Экран деталей предмета (добавил его!)
        composable(
            "subject_details/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            val dummySubject = Subject(subjectId, "Предмет $subjectId") // Заглушка

            SubjectDetailsScreen(
                subject = dummySubject,
                onQuestionsClick = { navController.navigate("questions_list/$subjectId") },
                onEditClick = { navController.navigate("edit_subject/$subjectId") },
                onDeleteClick = { navController.navigate("delete_subject/$subjectId") }
            )
        }

        // 5. Экран редактирования предмета
        composable(
            "edit_subject/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            val dummySubject = Subject(subjectId, "Предмет $subjectId")
            EditSubjectScreen(
                subject = dummySubject,
                viewModel = subjectViewModel,
                onSubjectUpdated = { navController.popBackStack() }
            )
        }

        // 6. Экран подтверждения удаления предмета
        composable(
            "delete_subject/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            val dummySubject = Subject(subjectId, "Предмет $subjectId")
            DeleteSubjectScreen(
                subject = dummySubject,
                viewModel = subjectViewModel,
                onDeleteConfirmed = { navController.popBackStack("subjects_list", inclusive = false) },
                onDismiss = { navController.popBackStack() }
            )
        }

        // 7. Экран со списком вопросов для предмета
        composable(
            "questions_list/{subjectId}",
            arguments = listOf(navArgument("subjectId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            val dummySubject = Subject(subjectId, "Предмет $subjectId")

            QuestionsListScreen(
                subject = dummySubject,
                viewModel = questionViewModel,
                onAddQuestion = { navController.navigate("add_question/$subjectId") },
                onQuestionClick = { question ->
                    navController.navigate("edit_question/${question.id}")
                }
            )
        }

        // 8. Экран редактирования вопроса
        composable(
            "edit_question/{questionId}",
            arguments = listOf(navArgument("questionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt("questionId") ?: 0
            val dummyQuestion = Question(questionId, "Вопрос $questionId", "Ответ $questionId", false)

            EditQuestionScreen(
                question = dummyQuestion,
                viewModel = questionViewModel,
                onQuestionUpdated = { navController.popBackStack() }
            )
        }
    }
}



