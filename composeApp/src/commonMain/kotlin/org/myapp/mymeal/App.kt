package org.myapp.mymeal

import AuthService
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    // Initialize FirestoreRepository and AuthService
    val repository = FirestoreRepository()
    val authService = AuthService(repository)

    // Initialize the shared state holder (SharedViewModel)
    val sharedViewModel = SharedViewModel()

    // Get the navigation manager from the provider
    val navigationManager = NavigationProvider.navigationManager

    // Pass the navigation manager, authService, firestoreRepository, and sharedViewModel to the NavigationHost
    NavigationHost(
        navigationManager = navigationManager,
        authService = authService,  // Pass AuthService for authentication handling
        firestoreRepository = repository, // Pass FirestoreRepository for saving users
        sharedViewModel = sharedViewModel // Pass SharedViewModel for sharing state
    )
}
