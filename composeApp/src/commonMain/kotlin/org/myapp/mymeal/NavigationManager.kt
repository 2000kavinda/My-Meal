package org.myapp.mymeal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationManager {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.SignIn) // Default screen is SignIn
    val currentScreen: StateFlow<Screen> = _currentScreen

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        // Example back navigation logic
        if (_currentScreen.value is Screen.MealDetails) {
            _currentScreen.value = Screen.MealList
        } else if (_currentScreen.value is Screen.SaveUser) {
            _currentScreen.value = Screen.SignIn
        }
    }
}
