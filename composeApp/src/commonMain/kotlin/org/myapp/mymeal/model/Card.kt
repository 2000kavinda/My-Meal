package org.myapp.mymeal.model

data class Card(
    val balance: Double = 0.0,
    val card: String = "",
    val cvv: String = "",
    val expiry: String = ""
)