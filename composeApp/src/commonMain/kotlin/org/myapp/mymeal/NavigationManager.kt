package org.myapp.mymeal

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationManager {
    private val _currentScreen = MutableStateFlow<Screen>(Screen.MealList)
    val currentScreen: StateFlow<Screen> = _currentScreen

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        // Implement back navigation logic if needed
    }
}
