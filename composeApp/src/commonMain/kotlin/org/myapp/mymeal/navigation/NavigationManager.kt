package org.myapp.mymeal.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationManager {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.SignInScreen)
    val currentScreen: StateFlow<Screen> = _currentScreen

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (_currentScreen.value is Screen.MealDetails) {
            _currentScreen.value = Screen.MealList
        } else if (_currentScreen.value is Screen.SignUpScreen) {
            _currentScreen.value = Screen.SignInScreen
        }
    }
}
