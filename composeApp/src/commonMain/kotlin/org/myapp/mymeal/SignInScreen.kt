package org.myapp.mymeal

import AuthService
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    authService: AuthService,
    isLoading: Boolean,
    message: String,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    sharedViewModel: SharedViewModel,
) {
    // Mutable states for the UI
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email Input Field
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Password Input Field
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        // Sign-In Button
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    coroutineScope.launch {
                        isLoading = true
                        message = ""

                        try {
                            val isAuthenticated = authService.signIn(email, password)
                            if (isAuthenticated) {
                                message = "Sign-In Successful!"
                                sharedViewModel.setCurrentUserEmail(email)
                                onSignIn(email, password) // Navigate on success
                            } else {
                                message = "Invalid Email or Password."
                            }
                        } catch (e: Exception) {
                            message = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    message = "Please fill in both fields."
                }
            },
            modifier = Modifier.padding(top = 8.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            } else {
                Text("Sign In")
            }
        }

        // Display Sign-In Error or Success Messages
        if (message.isNotEmpty()) {
            Text(message, modifier = Modifier.padding(top = 16.dp), color = MaterialTheme.colors.error)
        }

        // Sign-Up Button
        Button(
            onClick = { onSignUp(email, password) }, // Navigate to Sign-Up
            modifier = Modifier.padding(top = 8.dp),
            enabled = !isLoading
        ) {
            Text("Sign Up")
        }
    }
}
