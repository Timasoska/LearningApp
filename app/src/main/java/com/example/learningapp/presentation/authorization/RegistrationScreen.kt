package com.example.learningapp.presentation.authorization

import androidx.compose.runtime.Composable
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
fun RegistrationScreen(
    viewModel: AuthViewModel = hiltViewModel(), // Inject ViewModel
    onRegistrationComplete: () -> Unit,
    onLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") } // Added login field state
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Observe registration success state to trigger navigation
    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            onRegistrationComplete()
            viewModel.resetNavigationFlags()
        }
    }

    // Optional: Show error messages
    LaunchedEffect(state.error) {
        state.error?.let {
            println("Registration Error: $it")
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Регистрация", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Add TextField for Login/Username
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") }, // Or "Username"
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль (мин. 6 символов)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Подтвердите пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = state.error != null && password != confirmPassword // Highlight if passwords don't match
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
        // Show password mismatch error locally
        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            Text(
                text = "Пароли не совпадают",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }


        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (email.isNotBlank() && login.isNotBlank() && password.length >= 6 && password == confirmPassword) {
                    viewModel.register(email, login, password) // Pass login value
                } else {
                    // Show local validation error
                    println("Validation failed: Check fields and password match/length.")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && password == confirmPassword // Also check password match locally
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Зарегистрироваться")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onLoginClick, enabled = !state.isLoading) {
            Text("Уже есть аккаунт? Войти")
        }
    }
}
