package org.myapp.mymeal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.myapp.mymeal.NavigationProvider.navigationManager

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SaveUserScreen(
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

    var selectedGender by remember { mutableStateOf("Select Gender") }
    var selectedActivityLevel by remember { mutableStateOf("Select Activity Level") }
    var selectedGoal by remember { mutableStateOf("Select Goal") }

    val genderOptions = listOf("Male", "Female", "Other")
    val activityLevelOptions = listOf("Sedentary", "Light", "Moderate", "Active", "Very Active")
    val goalOptions = listOf("Lose Weight", "Maintain Weight", "Gain Weight")


    BoxWithConstraints(modifier = Modifier.fillMaxSize()){
        val isWideScreen = maxWidth > 600.dp // Adjust breakpoint as needed
        val backgroundColor = if (isWideScreen) Color(0xFFCBD5ED) else MaterialTheme.colors.background

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
                            model = "https://firebasestorage.googleapis.com/v0/b/care-cost.appspot.com/o/meal%20photos%2FUntitled_design__1_-removebg.png?alt=media&token=0dacbe0d-7fa8-407a-86ab-020832fb83b8",
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
                                    .padding(bottom = 16.dp),
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

                            // Gender Dropdown
                            ExposedDropdownMenuBox(
                                expanded = genderExpanded,
                                onExpandedChange = { genderExpanded = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)) // Add border with rounded corners
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(12.dp) // Match the border shape
                                        )
                                ) {
                                    TextField(
                                        value = selectedGender,
                                        onValueChange = { selectedGender = it },
                                        label = { Text("Gender") },
                                        enabled = !isLoading,
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { genderExpanded = !genderExpanded },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowDropDown,
                                                contentDescription = "Dropdown Icon"
                                            )
                                        },
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color.Transparent, // Ensure no additional background
                                            focusedIndicatorColor = Color.Transparent, // Remove focused underline
                                            unfocusedIndicatorColor = Color.Transparent, // Remove unfocused underline
                                            disabledIndicatorColor = Color.Transparent // Remove disabled underline
                                        )
                                    )
                                }

                                ExposedDropdownMenu(
                                    expanded = genderExpanded,
                                    onDismissRequest = { genderExpanded = false }
                                ) {
                                    genderOptions.forEach { option ->
                                        DropdownMenuItem(onClick = {
                                            selectedGender = option
                                            genderExpanded = false
                                        }) {
                                            Text(text = option)
                                        }
                                    }
                                }
                            }



                            Spacer(modifier = Modifier.height(16.dp))

                            // Activity Level Dropdown
                            ExposedDropdownMenuBox(
                                expanded = activityLevelExpanded,
                                onExpandedChange = { activityLevelExpanded = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)) // Add border with rounded corners
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(12.dp) // Match the border shape
                                        )
                                ) {
                                    TextField(
                                        value = selectedActivityLevel,
                                        onValueChange = { selectedActivityLevel = it },
                                        label = { Text("Activity Level") },
                                        enabled = !isLoading,
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                activityLevelExpanded = !activityLevelExpanded
                                            },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowDropDown,
                                                contentDescription = "Dropdown Icon"
                                            )
                                        },
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color.Transparent, // Transparent to avoid overlapping
                                            focusedIndicatorColor = Color.Transparent, // Remove focused underline
                                            unfocusedIndicatorColor = Color.Transparent, // Remove unfocused underline
                                            disabledIndicatorColor = Color.Transparent // Remove disabled underline
                                        )
                                    )
                                }

                                ExposedDropdownMenu(
                                    expanded = activityLevelExpanded,
                                    onDismissRequest = { activityLevelExpanded = false }
                                ) {
                                    activityLevelOptions.forEach { option ->
                                        DropdownMenuItem(onClick = {
                                            selectedActivityLevel = option
                                            activityLevelExpanded = false
                                        }) {
                                            Text(text = option)
                                        }
                                    }
                                }
                            }


                            Spacer(modifier = Modifier.height(16.dp))

                            // Goal Dropdown
                            ExposedDropdownMenuBox(
                                expanded = goalExpanded,
                                onExpandedChange = { goalExpanded = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)) // Add border with rounded corners
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(12.dp) // Ensure the background matches the border shape
                                        )
                                ) {
                                    TextField(
                                        value = selectedGoal,
                                        onValueChange = { selectedGoal = it },
                                        label = { Text("Goal") },
                                        enabled = !isLoading,
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { goalExpanded = !goalExpanded },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.ArrowDropDown, // Dropdown arrow icon
                                                contentDescription = "Dropdown Icon"
                                            )
                                        },
                                        colors = TextFieldDefaults.textFieldColors(
                                            backgroundColor = Color.Transparent, // Prevent overlapping backgrounds
                                            focusedIndicatorColor = Color.Transparent, // Remove focused underline
                                            unfocusedIndicatorColor = Color.Transparent, // Remove unfocused underline
                                            disabledIndicatorColor = Color.Transparent // Remove disabled underline
                                        )
                                    )
                                }

                                ExposedDropdownMenu(
                                    expanded = goalExpanded,
                                    onDismissRequest = { goalExpanded = false }
                                ) {
                                    goalOptions.forEach { option ->
                                        DropdownMenuItem(onClick = {
                                            selectedGoal = option
                                            goalExpanded = false
                                        }) {
                                            Text(text = option)
                                        }
                                    }
                                }
                            }



                            Spacer(modifier = Modifier.height(38.dp))

                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                            } else {
                                Button(
                                    onClick = {
                                        if (email.text.isNotBlank() && password.text.isNotBlank() &&
                                            selectedGender != "Select Gender" &&
                                            selectedActivityLevel != "Select Activity Level" &&
                                            selectedGoal != "Select Goal"
                                        ) {
                                            onSave(
                                                email.text,
                                                password.text,
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

                            if (message.isNotBlank()) {
                                Text(
                                    text = message,
                                    color = if (message.contains("Error")) Color.Red else Color.Green,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    style = MaterialTheme.typography.body1
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Sign-up Text
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Already have a account ?", color = Color.Gray)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Log In",
                                    color = Color(0xFF002945),
                                    modifier = Modifier.clickable {navigationManager.navigateTo(Screen.SignIn) },
                                    style = MaterialTheme.typography.body2.copy(
                                        fontWeight = FontWeight.SemiBold // Makes the text bold
                                    ) )
                            }
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
                            .padding(bottom = 16.dp),
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

                    // Gender Dropdown
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)) // Add border with rounded corners
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp) // Match the border shape
                                )
                        ) {
                            TextField(
                                value = selectedGender,
                                onValueChange = { selectedGender = it },
                                label = { Text("Gender") },
                                enabled = !isLoading,
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { genderExpanded = !genderExpanded },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Dropdown Icon"
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.Transparent, // Ensure no additional background
                                    focusedIndicatorColor = Color.Transparent, // Remove focused underline
                                    unfocusedIndicatorColor = Color.Transparent, // Remove unfocused underline
                                    disabledIndicatorColor = Color.Transparent // Remove disabled underline
                                )
                            )
                        }

                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(onClick = {
                                    selectedGender = option
                                    genderExpanded = false
                                }) {
                                    Text(text = option)
                                }
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(16.dp))

                    // Activity Level Dropdown
                    ExposedDropdownMenuBox(
                        expanded = activityLevelExpanded,
                        onExpandedChange = { activityLevelExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)) // Add border with rounded corners
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp) // Match the border shape
                                )
                        ) {
                            TextField(
                                value = selectedActivityLevel,
                                onValueChange = { selectedActivityLevel = it },
                                label = { Text("Activity Level") },
                                enabled = !isLoading,
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activityLevelExpanded = !activityLevelExpanded
                                    },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Dropdown Icon"
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.Transparent, // Transparent to avoid overlapping
                                    focusedIndicatorColor = Color.Transparent, // Remove focused underline
                                    unfocusedIndicatorColor = Color.Transparent, // Remove unfocused underline
                                    disabledIndicatorColor = Color.Transparent // Remove disabled underline
                                )
                            )
                        }

                        ExposedDropdownMenu(
                            expanded = activityLevelExpanded,
                            onDismissRequest = { activityLevelExpanded = false }
                        ) {
                            activityLevelOptions.forEach { option ->
                                DropdownMenuItem(onClick = {
                                    selectedActivityLevel = option
                                    activityLevelExpanded = false
                                }) {
                                    Text(text = option)
                                }
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // Goal Dropdown
                    ExposedDropdownMenuBox(
                        expanded = goalExpanded,
                        onExpandedChange = { goalExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)) // Add border with rounded corners
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp) // Ensure the background matches the border shape
                                )
                        ) {
                            TextField(
                                value = selectedGoal,
                                onValueChange = { selectedGoal = it },
                                label = { Text("Goal") },
                                enabled = !isLoading,
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { goalExpanded = !goalExpanded },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown, // Dropdown arrow icon
                                        contentDescription = "Dropdown Icon"
                                    )
                                },
                                colors = TextFieldDefaults.textFieldColors(
                                    backgroundColor = Color.Transparent, // Prevent overlapping backgrounds
                                    focusedIndicatorColor = Color.Transparent, // Remove focused underline
                                    unfocusedIndicatorColor = Color.Transparent, // Remove unfocused underline
                                    disabledIndicatorColor = Color.Transparent // Remove disabled underline
                                )
                            )
                        }

                        ExposedDropdownMenu(
                            expanded = goalExpanded,
                            onDismissRequest = { goalExpanded = false }
                        ) {
                            goalOptions.forEach { option ->
                                DropdownMenuItem(onClick = {
                                    selectedGoal = option
                                    goalExpanded = false
                                }) {
                                    Text(text = option)
                                }
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(38.dp))

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Button(
                            onClick = {
                                if (email.text.isNotBlank() && password.text.isNotBlank() &&
                                    selectedGender != "Select Gender" &&
                                    selectedActivityLevel != "Select Activity Level" &&
                                    selectedGoal != "Select Goal"
                                ) {
                                    onSave(
                                        email.text,
                                        password.text,
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

                    if (message.isNotBlank()) {
                        Text(
                            text = message,
                            color = if (message.contains("Error")) Color.Red else Color.Green,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            style = MaterialTheme.typography.body1
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sign-up Text
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Already have a account ?", color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Log In",
                            color = Color(0xFF002945),
                            modifier = Modifier.clickable {navigationManager.navigateTo(Screen.SignIn) },
                            style = MaterialTheme.typography.body2.copy(
                                fontWeight = FontWeight.SemiBold // Makes the text bold
                            ) )
                    }
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
    }
}

