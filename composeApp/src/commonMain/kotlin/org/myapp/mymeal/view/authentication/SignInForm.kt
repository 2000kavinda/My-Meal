package org.myapp.mymeal.view.authentication

import org.myapp.mymeal.controller.AuthService
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.components.CustomOutlinedTextField
import org.myapp.mymeal.components.CustomPasswordTextField
import org.myapp.mymeal.components.LoadingButton
import org.myapp.mymeal.ui.theme.ColorThemes
import org.myapp.mymeal.ui.theme.FontSizes

@Composable
fun SignInForm(
    authService: AuthService,
    isLoading: Boolean,
    message: String,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    sharedViewModel: SharedViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoadingState by remember { mutableStateOf(false) }
    var messageState by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Text(
        text = "Log in",
        style = MaterialTheme.typography.h4.copy(
            fontWeight = FontWeight.Bold,
            fontSize = FontSizes.font36,
        ),
        color = ColorThemes.PrimaryButtonColor,
        modifier = Modifier.padding(bottom = 48.dp)
    )

    CustomOutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Your email") }
    )


    CustomPasswordTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Your password") },
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityChange = { isPasswordVisible = it }
    )
    Spacer(modifier = Modifier.height(16.dp))

    LoadingButton(
        onClick = {
            handleSignIn(
                email = email,
                password = password,
                coroutineScope = coroutineScope,
                authService = authService,
                sharedViewModel = sharedViewModel,
                onSignIn = onSignIn,
                onStateChange = { isLoading, message ->
                    isLoadingState = isLoading
                    messageState = message
                }
            )
        },
        isLoading = isLoadingState,
        buttonText = "Log in",
        backgroundColor = ColorThemes.PrimaryButtonColor,
        textColor = ColorThemes.PrimaryTextColor
    )



    if (messageState.isNotEmpty()) {
        Text(
            text = messageState,
            color = if (messageState.contains("Error") || messageState.contains("Invalid")) Color.Red else Color.Red,
            modifier = Modifier
                .padding(top = 16.dp)

        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Don't have an account?", color = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Sign up",
            color = ColorThemes.PrimaryButtonColor,
            modifier = Modifier.clickable { onSignUp(email, password) },
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.SemiBold
            ) )
    }
}


fun handleSignIn(
    email: String,
    password: String,
    coroutineScope: CoroutineScope,
    authService: AuthService,
    sharedViewModel: SharedViewModel,
    onSignIn: (String, String) -> Unit,
    onStateChange: (Boolean, String) -> Unit
) {
    if (email.isNotBlank() && password.isNotBlank()) {
        coroutineScope.launch {
            onStateChange(true, "")

            try {
                val isAuthenticated = authService.signIn(email, password)
                if (isAuthenticated) {
                    onStateChange(false, "Sign-In Successful!")
                    sharedViewModel.setCurrentUserEmail(email)
                    onSignIn(email, password)
                } else {
                    onStateChange(false, "Invalid Email or Password.")
                }
            } catch (e: Exception) {
                onStateChange(false, "Error: ${e.message}")
            }
        }
    } else {
        onStateChange(false, "Please fill in both fields.")
    }
}