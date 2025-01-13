package org.myapp.mymeal.model

data class CardDetails(
    val card: String = "",
    val cvv: String = "",
    val expiry: String = "",
    val balance: Double = 0.0
)
