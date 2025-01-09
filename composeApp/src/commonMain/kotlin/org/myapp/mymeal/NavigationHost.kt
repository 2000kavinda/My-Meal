package org.myapp.mymeal

import AuthService
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Composable
fun NavigationHost(
    navigationManager: NavigationManager,
    authService: AuthService,
    firestoreRepository: FirestoreRepository,
    sharedViewModel: SharedViewModel,
) {
    val currentScreen by navigationManager.currentScreen.collectAsState()

    when (currentScreen) {
        is Screen.SignIn -> SignInScreen(
            authService = authService,
            isLoading = false,
            message = "",
            onSignUp = { email, password ->
                // Implement the SignIn logic if needed
                // Navigate to SaveUserScreen after successful sign-in (if needed)
                navigationManager.navigateTo(Screen.SaveUser)
            },
            onSignIn = { email, password ->
                // Implement the SignIn logic if needed
                // Navigate to SaveUserScreen after successful sign-in (if needed)
                navigationManager.navigateTo(Screen.PlatformImagePicker)
            },
            sharedViewModel = sharedViewModel,
        )
        is Screen.SaveUser -> SaveUserScreen(
            isLoading = false,
            message = "",
            onSave = { email, password,gender, activityLevel,  goal ,onSuccess ->
                // Call firestoreRepository to add the user
                onSaveUser(email, password, gender, activityLevel, goal, firestoreRepository, onSuccess,sharedViewModel, navigationManager)
            }
        )
        is Screen.MealList -> MealListScreen(
            repository = firestoreRepository,
            onMealClick = { meal ->
                navigationManager.navigateTo(Screen.MealDetails(meal))
                //navigationManager.navigateTo(Screen.ProfileScreen(meal))
            }
        )
        is Screen.History -> HistoryScreen(
        repository = firestoreRepository,
        onMealClick = { meal ->
            navigationManager.navigateTo(Screen.MealDetails(meal))
            //navigationManager.navigateTo(Screen.ProfileScreen(meal))
        }
    )
        is Screen.ImagePickerUI -> ImagePickerUI(
            onPickImage = { /* Your logic to pick an image */ },
            imageBitmap = null // Pass the appropriate ImageBitmap or state here
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
            onBack = { navigationManager.navigateBack() }
        )
    }
}

fun onSaveUser(
    email: String,
    password: String,
    gender: String,
    activityLevel: String,
    goal: String,
    firestoreRepository: FirestoreRepository,
    onSuccess: () -> Unit,
    sharedViewModel: SharedViewModel,
    navigationManager: NavigationManager
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val hashedBytes = messageDigest.digest(password.toByteArray())
            val encryptedPassword= hashedBytes.joinToString("") { "%02x".format(it) }
            //val encryptedPassword = encryptPassword(password)
            val user = User(email, encryptedPassword, gender, activityLevel, goal)
            val result = firestoreRepository.addUser(user)

            if (result.isSuccess) {
                onSuccess()  // Call the onSuccess callback to indicate successful registration
                sharedViewModel.setCurrentUserEmail(email)
                // Navigate to the MealList screen after successful registration
                navigationManager.navigateTo(Screen.MealList)
            } else {
                // Handle failure (navigate to SignIn or show a failure message)
                navigationManager.navigateTo(Screen.SignIn)
            }
        } catch (e: Exception) {
            // Handle exception (show error message and navigate to SignIn)
            navigationManager.navigateTo(Screen.SignIn)
        }
    }
}

