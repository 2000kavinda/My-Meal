package org.myapp.mymeal

data class Order(
    val name: String = "",
    val calories: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val proteins: Double = 0.0,
    val fats: Double = 0.0,
    val price: Double = 0.0,
    val photo: String = "",
    val email: String = "",
    val day: String = "",
)
