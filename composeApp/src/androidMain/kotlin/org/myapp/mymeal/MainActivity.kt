/*package org.myapp.mymeal

import org.myapp.mymeal.controller.AuthService
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.security.MessageDigest

class MainActivity : ComponentActivity() {
    
    private val repository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isLoading by remember { mutableStateOf(false) } // Track loading state
            var message by remember { mutableStateOf("") } // Track success/error messages

            App(
                isLoading = isLoading,
                message = message, // Pass message to display
                onSaveUser = { email, password ->
                    // Use lifecycleScope to handle asynchronous work in the activity
                    lifecycleScope.launch {
                        try {
                            isLoading = true // Set loading state to true when the process starts
                            message = "" // Clear previous message
                            val encryptedPassword = encryptPassword(password)

                            val result = repository.addUser(User(email, encryptedPassword))
                            if (result.isSuccess) {
                                message = "User added successfully!" // Success message
                            } else {
                                message = "Error: ${result.exceptionOrNull()?.message}" // Error message
                            }
                        } catch (e: Exception) {
                            message = "Error: ${e.message}" // Error message
                        } finally {
                            isLoading = false // Set loading state to false when the process ends
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun App(
    isLoading: Boolean,
    message: String,
    onSaveUser: (String, String) -> Unit
) {
    // Pass the loading state and message to SaveUserScreen
    SaveUserScreen(
        isLoading = isLoading,
        message = message,
        onSave = onSaveUser
    )
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(isLoading = false, message = "", onSaveUser = { _, _ -> })
}


fun encryptPassword(password: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = messageDigest.digest(password.toByteArray())
    return hashedBytes.joinToString("") { "%02x".format(it) }
}*/
/*
package org.myapp.mymeal

import org.myapp.mymeal.controller.AuthService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

    private val authService = org.myapp.mymeal.controller.AuthService(FirestoreRepository()) // Replace with your actual service instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isLoading by remember { mutableStateOf(false) } // Track loading state
            var message by remember { mutableStateOf("") } // Track success/error messages

            App(
                authService = authService,
                isLoading = isLoading,
                message = message, // Pass message to display
                onSignIn = { email, password ->
                    // Use lifecycleScope to handle asynchronous work in the activity
                    lifecycleScope.launch {
                        try {
                            isLoading = true // Set loading state to true when the process starts
                            message = "" // Clear previous message

                            val result = authService.signIn(email, password) // Call your auth logic
                            try {
                                val isSignedIn = authService.signIn(email, password)
                                if (isSignedIn) {
                                    message = "Sign-in successful!"
                                } else {
                                    message = "Invalid email or password"
                                }
                            } catch (e: Exception) {
                                message = "Error: ${e.message}"
                            }
                        } catch (e: Exception) {
                            message = "Error: ${e.message}" // Error message
                        } finally {
                            isLoading = false // Set loading state to false when the process ends
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun App(
    authService: org.myapp.mymeal.controller.AuthService,
    isLoading: Boolean,
    message: String,
    onSignIn: (String, String) -> Unit
) {
    // Pass the loading state, message, and sign-in logic to SignInScreen
    SignInScreen(
        authService = authService,
        isLoading = isLoading,
        message = message,
        onSignIn = onSignIn
    )
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(
        authService = org.myapp.mymeal.controller.AuthService(FirestoreRepository()), // Mock or real auth service for preview
        isLoading = false,
        message = "",
        onSignIn = { _, _ -> }
    )
}*/


package org.myapp.mymeal

import GameScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*

/*
class MainActivity : ComponentActivity() {

    private val authService = org.myapp.mymeal.controller.AuthService(FirestoreRepository()) // Replace with your actual service instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isLoading by remember { mutableStateOf(false) } // Track loading state
            var message by remember { mutableStateOf("") } // Track success/error messages

            // Use rememberNavController to handle navigation
            val navController = rememberNavController()

            App(
                authService = authService,
                isLoading = isLoading,
                message = message, // Pass message to display
                onSignIn = { email, password ->
                    // Use lifecycleScope to handle asynchronous work in the activity
                    lifecycleScope.launch {
                        try {
                            isLoading = true // Set loading state to true when the process starts
                            message = "" // Clear previous message

                            // Perform sign-in logic
                            val result = authService.signIn(email, password) // Call your auth logic
                            if (result) {
                                message = "Sign-in successful!"
                                // Navigate to MealListScreen after successful sign-in
                                navController.navigate("mealList") // Use your navigation path here
                            } else {
                                message = "Invalid email or password"
                            }
                        } catch (e: Exception) {
                            message = "Error: ${e.message}" // Error message
                        } finally {
                            isLoading = false // Set loading state to false when the process ends
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun App(
    authService: org.myapp.mymeal.controller.AuthService,
    isLoading: Boolean,
    message: String,
    onSignIn: (String, String) -> Unit
) {
    // Pass the loading state, message, and sign-in logic to SignInScreen
    SignInScreen(
        authService = authService,
        isLoading = isLoading,
        message = message,
        onSignIn = onSignIn
    )
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(
        authService = org.myapp.mymeal.controller.AuthService(FirestoreRepository()), // Mock or real auth service for preview
        isLoading = false,
        message = "",
        onSignIn = { _, _ -> }
    )
}
*/
/*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navigationController = NavigationController()

        setContent {
            AndroidApp(navigationController = navigationController)
        }
    }
}


@Composable
fun AndroidApp(navigationController: NavigationController) {
    when (navigationController.currentScreen.value) {
        Screen.MealList -> MealListScreen(repository = FirestoreRepository(), navigationController)
        Screen.MealDetails -> navigationController.mealDetailsArgs?.let { meal ->
            MealDetailsScreen(meal = meal as org.myapp.mymeal.model.Meal , onBack = { navigationController.goBack() })
        }
    }
}*/

import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //GameScreen()
            PlatformImagePicker()
            App()
        }
    }
}
