package org.myapp.mymeal

import org.myapp.mymeal.controller.AuthService
import androidx.compose.runtime.Composable
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.navigation.NavigationHost
import org.myapp.mymeal.navigation.NavigationProvider

@Composable
fun App() {
    // Initialize FirestoreRepository and org.myapp.mymeal.controller.AuthService
    val repository = FirestoreRepository()
    // Initialize the shared state holder (SharedViewModel)
    val sharedViewModel = SharedViewModel()
    val authService = AuthService(repository, sharedViewModel)



    // Get the navigation manager from the provider
    val navigationManager = NavigationProvider.navigationManager

    // Pass the navigation manager, authService, firestoreRepository, and sharedViewModel to the NavigationHost
    NavigationHost(
        navigationManager = navigationManager,
        authService = authService,  // Pass org.myapp.mymeal.controller.AuthService for authentication handling
        firestoreRepository = repository, // Pass FirestoreRepository for saving users
        sharedViewModel = sharedViewModel // Pass SharedViewModel for sharing state
    )
}
