package com.example.learningapp.presentation.authorization

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel // Import Hilt ViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(), // Inject ViewModel
    onLoginSuccess: () -> Unit,
    onRegistrationClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current // For showing Toasts or Snackbars

    // Observe login success state to trigger navigation
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            // Optionally show a success message
            // Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
            viewModel.resetNavigationFlags() // Reset flag after navigation
        }
    }

    // Optional: Show error messages
    LaunchedEffect(state.error) {
        state.error?.let {
            // Show error using Snackbar or Toast
            // For now, just log it
            println("Login Error: $it")
            // Consider adding a SnackbarHostState and showing a Snackbar
            viewModel.clearError() // Clear error after showing
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Авторизация", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null // Optional: Highlight field on error
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null // Optional: Highlight field on error
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Show error message text
        if (state.error != null) {
            Text(
                text = state.error ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                // Basic client-side validation
                if (email.isNotBlank() && password.isNotBlank()) {
                    viewModel.login(email, password)
                } else {
                    // Show local validation error
                    println("Email and password cannot be blank")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading // Disable button while loading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Войти")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onRegistrationClick, enabled = !state.isLoading) {
            Text("Нет аккаунта? Зарегистрироваться")
        }
    }
}
