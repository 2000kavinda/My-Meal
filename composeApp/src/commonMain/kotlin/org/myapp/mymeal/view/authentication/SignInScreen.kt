package org.myapp.mymeal.view.authentication

import org.myapp.mymeal.controller.AuthService
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.myapp.mymeal.SharedViewModel
import org.myapp.mymeal.components.CustomOutlinedTextField
import org.myapp.mymeal.components.CustomPasswordTextField
import org.myapp.mymeal.components.LoadingButton
import org.myapp.mymeal.utils.Constants
import org.myapp.mymeal.ui.theme.PrimaryBgColor
import org.myapp.mymeal.ui.theme.PrimaryButtonColor
import org.myapp.mymeal.ui.theme.PrimaryTextColor

@Composable
fun SignInScreen(
    authService: AuthService,
    isLoading: Boolean,
    message: String,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    sharedViewModel: SharedViewModel,
) {


    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 600.dp // Adjust breakpoint as needed
        val backgroundColor = if (isWideScreen) PrimaryBgColor else MaterialTheme.colors.background

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            if (isWideScreen) {
                Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    // First column with an image
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = Constants.logoUrl,
                            contentDescription = "Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )

                    }

                    // Second column with form content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(16.dp)
                            .background(Color.White, shape = RoundedCornerShape(24.dp)) // Rounded corners
                            .padding(48.dp), // Inner padding
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        SignInForm(authService=authService,
                            isLoading=isLoading,
                            message=message,
                            onSignIn=onSignIn,
                            onSignUp=onSignUp,
                            sharedViewModel=sharedViewModel
                        )

                    }

                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SignInForm(authService=authService,
                        isLoading=isLoading,
                        message=message,
                        onSignIn=onSignIn,
                        onSignUp=onSignUp,
                        sharedViewModel=sharedViewModel
                    )
                }
            }
        }
    }
}





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
            fontSize = 36.sp,
        ),
        color = Color(0xFF002945),
        modifier = Modifier.padding(bottom = 48.dp)
    )

    // Email TextField
    CustomOutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Your email") }
    )


    // Password TextField with visibility toggle

    CustomPasswordTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Your password") },
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityChange = { isPasswordVisible = it }
    )


    // Log In Button


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
        backgroundColor = PrimaryButtonColor,
        textColor = PrimaryTextColor
    )



    // Display error or success message below the button
    if (messageState.isNotEmpty()) {
        Text(
            text = messageState,
            color = if (messageState.contains("Error") || messageState.contains("Invalid")) Color.Red else Color.Red,
            modifier = Modifier
                .padding(top = 16.dp)

        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Sign-up Text
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Don't have an account?", color = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Sign up",
            color = Color(0xFF002945),
            modifier = Modifier.clickable { onSignUp(email, password) },
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.SemiBold // Makes the text bold
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








