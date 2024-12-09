package org.myapp.mymeal

sealed class Screen {
    object SignIn : Screen()
    object SaveUser : Screen()
    object MealList : Screen()
    data class MealDetails(val meal: Meal) : Screen()
}
