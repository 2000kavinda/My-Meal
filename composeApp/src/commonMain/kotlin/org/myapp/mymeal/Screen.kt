package org.myapp.mymeal

sealed class Screen {
    object SignInScreen : Screen()


    object SignUpScreen : Screen()
    object MealList : Screen()
    object History : Screen()
    object GameScreen : Screen()
    object PlayScreen : Screen()
    object ImagePickerUI : Screen()
    object PlatformImagePicker : Screen()
    data class MealDetails(val meal: Meal) : Screen()
    data class ProfileScreen(val meal: Meal) : Screen()
}
