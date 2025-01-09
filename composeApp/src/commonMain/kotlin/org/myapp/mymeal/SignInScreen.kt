package org.myapp.mymeal

import AuthService
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
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
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoadingState by remember { mutableStateOf(false) }
    var messageState by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 600.dp // Adjust breakpoint as needed
        val backgroundColor = if (isWideScreen) Color(0xFFCBD5ED) else MaterialTheme.colors.background

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
                            model = "https://firebasestorage.googleapis.com/v0/b/care-cost.appspot.com/o/meal%20photos%2FUntitled_design__1_-removebg.png?alt=media&token=0dacbe0d-7fa8-407a-86ab-020832fb83b8",
                            contentDescription = "Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                       /*Image(
                            painter = painterResource("logo.png"), // Replace with your image resource
                            contentDescription = "My Meal Logo",
                            modifier = Modifier
                                .size(400.dp) // Adjust size as needed
                                .padding(16.dp)
                        )}*/
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
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Your email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            )
                        )

                        // Password TextField with visibility toggle
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Your password") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 42.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Check else Icons.Default.CheckCircle,
                                        contentDescription = "Toggle password visibility"
                                    )
                                }
                            }
                        )

                        // Log In Button
                        Button(
                            onClick = {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    coroutineScope.launch {
                                        isLoadingState = true
                                        messageState = ""

                                        try {
                                            val isAuthenticated = authService.signIn(email, password)
                                            if (isAuthenticated) {
                                                messageState = "Sign-In Successful!"
                                                sharedViewModel.setCurrentUserEmail(email)
                                                onSignIn(email, password)
                                            } else {
                                                messageState = "Invalid Email or Password."
                                            }
                                        } catch (e: Exception) {
                                            messageState = "Error: ${e.message}"
                                        } finally {
                                            isLoadingState = false
                                        }
                                    }
                                } else {
                                    messageState = "Please fill in both fields."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))
                        ) {
                            if (isLoadingState) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White // Sets the color to white
                                )
                            } else {
                                Text("Log in", color = Color.White)
                            }
                        }

                        // Display error or success message below the button
                        if (messageState.isNotEmpty()) {
                            Text(
                                text = messageState,
                                color = if (messageState.contains("Error") || messageState.contains("Invalid")) Color.Red else Color.Red,
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .align(Alignment.CenterHorizontally)
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

                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Your email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        )
                    )

                    // Password TextField with visibility toggle
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Your password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 42.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Gray,
                            unfocusedLabelColor = Color.Gray
                        ),
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Check else Icons.Default.CheckCircle,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    )

                    // Log In Button
                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                coroutineScope.launch {
                                    isLoadingState = true
                                    messageState = ""

                                    try {
                                        val isAuthenticated = authService.signIn(email, password)
                                        if (isAuthenticated) {
                                            messageState = "Sign-In Successful!"
                                            sharedViewModel.setCurrentUserEmail(email)
                                            onSignIn(email, password)
                                        } else {
                                            messageState = "Invalid Email or Password."
                                        }
                                    } catch (e: Exception) {
                                        messageState = "Error: ${e.message}"
                                    } finally {
                                        isLoadingState = false
                                    }
                                }
                            } else {
                                messageState = "Please fill in both fields."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))
                    ) {
                        if (isLoadingState) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.White // Sets the color to white
                            )
                        } else {
                            Text("Log in", color = Color.White)
                        }
                    }

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
    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Your email") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray
        )
    )

    // Password TextField with visibility toggle
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Your password") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 42.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.Gray,
            unfocusedLabelColor = Color.Gray
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Check else Icons.Default.CheckCircle,
                    contentDescription = "Toggle password visibility"
                )
            }
        }
    )

    // Log In Button
    Button(
        onClick = {
            if (email.isNotBlank() && password.isNotBlank()) {
                coroutineScope.launch {
                    isLoadingState = true
                    messageState = ""

                    try {
                        val isAuthenticated = authService.signIn(email, password)
                        if (isAuthenticated) {
                            messageState = "Sign-In Successful!"
                            sharedViewModel.setCurrentUserEmail(email)
                            onSignIn(email, password)
                        } else {
                            messageState = "Invalid Email or Password."
                        }
                    } catch (e: Exception) {
                        messageState = "Error: ${e.message}"
                    } finally {
                        isLoadingState = false
                    }
                }
            } else {
                messageState = "Please fill in both fields."
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))
    ) {
        if (isLoadingState) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = Color.White // Sets the color to white
            )
        } else {
            Text("Log in", color = Color.White)
        }
    }

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




