package org.myapp.mymeal.navigation

import org.myapp.mymeal.controller.AuthService
import GameScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.myapp.mymeal.PlatformImagePicker
import org.myapp.mymeal.controller.AuthController
import org.myapp.mymeal.view.playGame.PlayScreen
import org.myapp.mymeal.view.profile.ProfileScreen
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.model.User
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.view.history.HistoryScreen
import org.myapp.mymeal.view.buyMeal.MealDetailsScreen
import org.myapp.mymeal.view.buyMeal.MealListScreen
import org.myapp.mymeal.view.authentication.SignUpScreen
import org.myapp.mymeal.view.authentication.SignInScreen
import java.security.MessageDigest

@Composable
fun NavigationHost(
    navigationManager: NavigationManager,
    authService: AuthService,
    firestoreRepository: HistoryController,
    sharedViewModel: SharedViewModel,
) {
    val currentScreen by navigationManager.currentScreen.collectAsState()

    when (currentScreen) {
        is Screen.SignInScreen -> SignInScreen(
            authService = authService,
            isLoading = false,
            message = "",
            onSignUp = { email, password ->
                navigationManager.navigateTo(Screen.SignUpScreen)
            },
            onSignIn = { email, password ->
                navigationManager.navigateTo(Screen.PlatformImagePicker)
            },
            sharedViewModel = sharedViewModel,
        )
        is Screen.SignUpScreen -> SignUpScreen(
            isLoading = false,
            message = "",
            onSave = { email, password,gender, activityLevel,  goal ,onSuccess ->
                onSaveUser(email, password, gender, activityLevel, goal, firestoreRepository, onSuccess,sharedViewModel, navigationManager)
            },sharedViewModel=sharedViewModel
        )
        is Screen.MealList -> MealListScreen(
            repository = firestoreRepository,
            onMealClick = { meal ->
                navigationManager.navigateTo(Screen.MealDetails(meal))
            }
        )
        is Screen.History -> HistoryScreen(
            sharedViewModel=sharedViewModel,
        repository = firestoreRepository,
        onMealClick = { meal ->
            navigationManager.navigateTo(Screen.MealDetails(meal))
        }
    )

        is Screen.PlatformImagePicker -> PlatformImagePicker()

        is Screen.MealDetails -> MealDetailsScreen(
            meal = (currentScreen as Screen.MealDetails).meal,
            sharedViewModel = sharedViewModel,
            onBack = { navigationManager.navigateBack() }
        )
        is Screen.ProfileScreen -> ProfileScreen(
            meal = (currentScreen as Screen.ProfileScreen).meal,
            sharedViewModel = sharedViewModel,
        )
        is Screen.PlayScreen -> PlayScreen(
            repository = firestoreRepository,
            onMealClick = { meal ->
                navigationManager.navigateTo(Screen.MealDetails(meal))
            },sharedViewModel=sharedViewModel)
        is Screen.GameScreen -> GameScreen(
            sharedViewModel = sharedViewModel,

            )
    }
}

fun onSaveUser(
    email: String,
    password: String,
    gender: String,
    activityLevel: String,
    goal: String,
    firestoreRepository: HistoryController,
    onSuccess: () -> Unit,
    sharedViewModel: SharedViewModel,
    navigationManager: NavigationManager
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val hashedBytes = messageDigest.digest(password.toByteArray())
            val encryptedPassword= hashedBytes.joinToString("") { "%02x".format(it) }
            val user = User(email, encryptedPassword, gender, activityLevel, goal)
            val authController= AuthController();
            val result = authController.addUser(user)

            if (result.isSuccess) {
                onSuccess()
                sharedViewModel.setCurrentUserEmail(email)
                navigationManager.navigateTo(Screen.MealList)
            } else {
                navigationManager.navigateTo(Screen.SignInScreen)
            }
        } catch (e: Exception) {
            navigationManager.navigateTo(Screen.SignInScreen)
        }
    }
}

