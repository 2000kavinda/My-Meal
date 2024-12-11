package org.myapp.mymeal

import AuthService
import GameScreen
import MealService

import com.google.firebase.FirebasePlatform
import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import createHttpClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import java.security.MessageDigest
/*
fun main() = application {

    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {

        val storage = mutableMapOf<String, String>()
        override fun clear(key: String) {
            storage.remove(key)
        }

        override fun log(msg: String) = println(msg)

        override fun retrieve(key: String) = storage[key]

        override fun store(key: String, value: String) = storage.set(key, value)
    })

    val options = FirebaseOptions(
        projectId = "my-meal-a86b4",
        applicationId = "1:325606968269:web:efa4e20a278776ff135284",
        apiKey = "AIzaSyCXTLffMjXXxN2uPURAIx0bkwqFr7RZSC4"
    )

    Firebase.initialize(Application(), options)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Firestore",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 1280.dp,
            height = 720.dp
        )
    )
    {

        val client = createHttpClient(CIO)

        val repository = FirestoreRepository()
        val authService = AuthService(repository)
        val mealService = MealService(client)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        var isLoading by remember { mutableStateOf(false) } // Track loading state
        var message by remember { mutableStateOf("") } // Track success/error messages

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*SignInScreen(
                authService = authService,
                isLoading = isLoading,
                message = message,
                onSignIn = { email, password ->
                    if (email.isNotBlank() && password.isNotBlank()) {
                        coroutineScope.launch {
                            try {
                                isLoading = true // Show loading
                                message = ""
                                val isAuthenticated = authService.signIn(email, password)
                                withContext(Dispatchers.Main) {
                                    isLoading = false // Hide loading
                                    message = if (isAuthenticated) {
                                        "Sign-In Successful!"
                                    } else {
                                        "Invalid Email or Password"
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false // Hide loading
                                    message = "Error: ${e.message}" // Error message
                                }
                            }
                        }
                    } else {
                        message = "Please fill in both fields." // Validation message
                    }
                }
            )*/
            /*SaveUserScreen(
                isLoading = isLoading,
                message = message,
                onSave = { email, password ->
                    if (email.isNotBlank() && password.isNotBlank()) {
                        coroutineScope.launch {
                            try {
                                isLoading = true // Show loading
                                message = ""
                                val encryptedPassword = encryptPassword(password)
                                val result = repository.addUser(User(email, encryptedPassword))
                                withContext(Dispatchers.Main) {
                                    isLoading = false // Hide loading
                                    if (result.isSuccess) {
                                        message = "User added successfully!" // Success message
                                    } else {
                                        message = "Error: ${result.exceptionOrNull()?.message}" // Error message
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false // Hide loading
                                    message = "Error: ${e.message}" // Error message
                                }
                            }
                        }
                    } else {
                        message = "Please fill in both fields." // Validation message
                    }
                }
            )*/

            /*MaterialTheme {
                MealApp(mealService = mealService, coroutineScope = coroutineScope)
            }*/
            MealListScreen(repository = repository)
        }
    }
}



fun encryptPassword(password: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = messageDigest.digest(password.toByteArray())
    return hashedBytes.joinToString("") { "%02x".format(it) }
}*/



fun main() = application {

    // Firebase initialization
    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {

        val storage = mutableMapOf<String, String>()
        override fun clear(key: String) {
            storage.remove(key)
        }

        override fun log(msg: String) = println(msg)

        override fun retrieve(key: String) = storage[key]

        override fun store(key: String, value: String) = storage.set(key, value)
    })

    // Firebase options for configuration
    val options = FirebaseOptions(
        projectId = "my-meal-a86b4",
        applicationId = "1:325606968269:web:efa4e20a278776ff135284",
        apiKey = "AIzaSyCXTLffMjXXxN2uPURAIx0bkwqFr7RZSC4"
    )

    // Initialize Firebase with the provided options
    Firebase.initialize(Application(), options)

    // Launch the Window for the app
    Window(
        onCloseRequest = ::exitApplication,
        title = "Firestore",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 1280.dp,
            height = 720.dp
        )
    ) {/*
        val client = createHttpClient(CIO) // Assume this client is configured for HTTP calls if needed
        val repository = FirestoreRepository() // Firestore repository to interact with Firestore
        val authService = AuthService(repository) // Assuming AuthService is for handling authentication
        val mealService = MealService(client) // Meal service for additional functionalities if needed
        val coroutineScope = CoroutineScope(IO) // Coroutine scope for managing background tasks

        // State to track loading and error messages
        var isLoading by remember { mutableStateOf(false) }
        var message by remember { mutableStateOf("") }

        // NavController for handling navigation between screens
        val navController = rememberNavController()

        // Set up the NavHost for navigating between Meal List and Meal Details screens
        NavHost(navController = navController, startDestination = "mealList") {
            composable("mealList") {
                MealListScreen(repository = repository, navController = navController)
            }

            composable(
                route = "mealDetails/{name}/{photo}/{price}",
                arguments = listOf(
                    androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType },
                    androidx.navigation.navArgument("photo") { type = androidx.navigation.NavType.StringType },
                    androidx.navigation.navArgument("price") { type = androidx.navigation.NavType.StringType }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
                val photo = backStackEntry.arguments?.getString("photo") ?: ""
                val price = backStackEntry.arguments?.getString("price") ?: "0.0"
                MealDetailsScreen(name = name, photo = photo, price = price)
            }
        }*/
        //App()
        //GameScreen()
        PlatformImagePicker()
    }
}


// Password encryption function
fun encryptPassword(password: String): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    val hashedBytes = messageDigest.digest(password.toByteArray())
    return hashedBytes.joinToString("") { "%02x".format(it) }
}

