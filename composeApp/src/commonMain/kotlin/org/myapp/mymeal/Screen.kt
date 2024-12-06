package org.myapp.mymeal

sealed class Screen {
    object MealList : Screen()
    data class MealDetails(val meal: Meal) : Screen()
}
