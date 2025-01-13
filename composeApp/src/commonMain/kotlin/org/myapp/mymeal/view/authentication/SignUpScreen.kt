package org.myapp.mymeal.view.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.components.CustomDropdown
import org.myapp.mymeal.components.CustomOutlinedTextField
import org.myapp.mymeal.components.CustomPasswordTextField
import org.myapp.mymeal.utils.Constants
import org.myapp.mymeal.ui.theme.PrimaryBgColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignUpScreen(
    isLoading: Boolean,
    message: String,
    onSave: (String, String, String, String, String, () -> Unit) -> Unit
) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Dropdown fields states
    var genderExpanded by remember { mutableStateOf(false) }
    var activityLevelExpanded by remember { mutableStateOf(false) }
    var goalExpanded by remember { mutableStateOf(false) }




    BoxWithConstraints(modifier = Modifier.fillMaxSize()){
        val isWideScreen = maxWidth > 600.dp // Adjust breakpoint as needed
        val backgroundColor = if (isWideScreen) PrimaryBgColor else MaterialTheme.colors.background

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Left Column: Image
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Replace this with your desired image
                        AsyncImage(
                            model = Constants.logoUrl,
                            contentDescription = "Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }

                    // Right Column: Form Content
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier

                                .fillMaxHeight()
                                .padding(16.dp)
                                .background(Color.White, shape = RoundedCornerShape(24.dp)) // Rounded corners
                                .padding(48.dp)// Inner padding
                            .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {

                            SignUpForm(
                                isLoading = isLoading,
                                message = message,

                                onSave = onSave
                            )



                            /*Button(
                                onClick = {
                                    navigationManager.navigateTo(Screen.SignIn)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                Text("Sign in")
                            }*/
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier

                        .fillMaxHeight()
                        .padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(24.dp)) // Rounded corners
                        .padding(48.dp)// Inner padding
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    SignUpForm(
                        isLoading = isLoading,
                        message = message,

                        onSave = onSave
                    )
                }
            }
        }
    }
}
@Composable
fun SignUpForm(
    isLoading: Boolean,
    message: String,
    onSave: (String, String, String, String, String, () -> Unit) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Select Gender") }
    var selectedActivityLevel by remember { mutableStateOf("Select Activity Level") }
    var selectedGoal by remember { mutableStateOf("Select Goal") }
    var errorMessage by remember { mutableStateOf("") }

    val genderOptions = listOf("Male", "Female", "Other")
    val activityLevelOptions = listOf("Sedentary", "Light", "Moderate", "Active", "Very Active")
    val goalOptions = listOf("Lose Weight", "Maintain Weight", "Gain Weight")

    Text(
        text = "Register",
        style = MaterialTheme.typography.h4.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
        ),
        color = Color(0xFF002945),
        modifier = Modifier.padding(bottom = 24.dp)
    )

    // Email TextField
    CustomOutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Your email") }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Password TextField
    CustomPasswordTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Your password") },
        isPasswordVisible = isPasswordVisible,
        onPasswordVisibilityChange = { isPasswordVisible = it }
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Gender Dropdown
    CustomDropdown(
        options = genderOptions,
        selectedOption = selectedGender,
        onOptionSelected = { selectedGender = it },
        label = "Gender",
        isLoading = isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Activity Level Dropdown
    CustomDropdown(
        options = activityLevelOptions,
        selectedOption = selectedActivityLevel,
        onOptionSelected = { selectedActivityLevel = it },
        label = "Activity Level",
        isLoading = isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Goal Dropdown
    CustomDropdown(
        options = goalOptions,
        selectedOption = selectedGoal,
        onOptionSelected = { selectedGoal = it },
        label = "Goal",
        isLoading = isLoading
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.padding(16.dp)
        )
    } else {
        Button(
            onClick = {
                // Validate all fields
                if (email.isBlank() ||
                    password.isBlank() ||
                    selectedGender == "Select Gender" ||
                    selectedActivityLevel == "Select Activity Level" ||
                    selectedGoal == "Select Goal"
                ) {
                    errorMessage = "Please fill all fields"
                } else {
                    errorMessage = "" // Clear error message
                    onSave(
                        email,
                        password,
                        selectedGender,
                        selectedActivityLevel,
                        selectedGoal
                    ) {
                        // Handle navigation after saving
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))
        ) {
            Text("Register", color = Color.White)
        }
    }

    // Display error message if any
    if (errorMessage.isNotBlank()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.body1)

        }

    Spacer(modifier = Modifier.height(24.dp))

    // Sign-up Text
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Already have an account?", color = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Log In",
            color = Color(0xFF002945),
            modifier = Modifier.clickable {
                navigationManager.navigateTo(Screen.SignInScreen)
            },
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}}



