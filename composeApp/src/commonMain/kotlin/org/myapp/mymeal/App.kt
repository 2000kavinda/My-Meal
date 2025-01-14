package org.myapp.mymeal

import org.myapp.mymeal.controller.AuthService
import androidx.compose.runtime.Composable
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.navigation.NavigationHost
import org.myapp.mymeal.navigation.NavigationProvider
import org.myapp.mymeal.state.SharedViewModel

@Composable
fun App() {
    val repository = HistoryController()
    val sharedViewModel = SharedViewModel()
    val authService = AuthService(repository, sharedViewModel)

val navigationManager = NavigationProvider.navigationManager

    NavigationHost(
        navigationManager = navigationManager,
        authService = authService,
        firestoreRepository = repository,
        sharedViewModel = sharedViewModel // Pass SharedViewModel for sharing state
    )
}
