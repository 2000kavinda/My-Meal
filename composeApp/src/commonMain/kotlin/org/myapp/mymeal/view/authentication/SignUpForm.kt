package org.myapp.mymeal.view.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.components.CustomDropdown
import org.myapp.mymeal.components.CustomOutlinedTextField
import org.myapp.mymeal.components.CustomPasswordTextField
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.ui.theme.ColorThemes

@Composable
fun SignUpForm(
    isLoading: Boolean,
    message: String,
    onSave: (String, String, String, String, String, () -> Unit) -> Unit,
    sharedViewModel: SharedViewModel,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("Select Gender") }
    var selectedActivityLevel by remember { mutableStateOf("Select Activity Level") }
    var selectedGoal by remember { mutableStateOf("Select Goal") }
    var errorMessage by remember { mutableStateOf("") }
    val genderOptions = listOf("Male", "Female")
    val activityLevelOptions = listOf("Low", "Moderate", "High")
    val goalOptions = listOf("Weight Loss", "Muscle Gain", "Maintain")

    Text(
        text = "Register",
        style = MaterialTheme.typography.h4.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
        ),
        color = ColorThemes.PrimaryButtonColor,
        modifier = Modifier.padding(bottom = 24.dp)
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

    CustomDropdown(
        options = genderOptions,
        selectedOption = selectedGender,
        onOptionSelected = { selectedGender = it },
        label = "Gender",
        isLoading = isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

    CustomDropdown(
        options = activityLevelOptions,
        selectedOption = selectedActivityLevel,
        onOptionSelected = { selectedActivityLevel = it },
        label = "Activity Level",
        isLoading = isLoading
    )

    Spacer(modifier = Modifier.height(16.dp))

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
                    errorMessage = ""
                        sharedViewModel.setCurrentUserGoal(selectedGoal)
                        sharedViewModel.setCurrentUserGender(selectedGender)
                        sharedViewModel.setCurrentUserActivityLevel(selectedActivityLevel)

                    onSave(
                        email,
                        password,
                        selectedGender,
                        selectedActivityLevel,
                        selectedGoal
                    ) {

                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = ColorThemes.PrimaryButtonColor)
        ) {
            Text("Register", color = Color.White)
        }
    }

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


}
    Spacer(modifier = Modifier.height(24.dp))

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Already have an account?", color = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Log In",
            color = ColorThemes.PrimaryButtonColor,
            modifier = Modifier.clickable {
                navigationManager.navigateTo(Screen.SignInScreen)
            },
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}