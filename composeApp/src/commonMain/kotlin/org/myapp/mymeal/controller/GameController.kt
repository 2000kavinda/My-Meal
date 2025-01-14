package org.myapp.mymeal.controller

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GameController {

    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchCoinCount(email: String): Double? {
        return try {
            val document = db.collection("coins").whereEqualTo("email", email).get().await()
            document.documents.firstOrNull()?.getDouble("count")
        } catch (e: Exception) {
            null
        }
    }


    suspend fun reduceCoinAmountByEmail(email: String, reductionAmount: Double ): Boolean {
        return try {
            val querySnapshot = db.collection("coins").whereEqualTo("email", email).get().await()
            val document = querySnapshot.documents.firstOrNull()

            if (document != null) {
                val currentAmount = document.getDouble("count") ?: return false
                if (currentAmount >= reductionAmount) {
                    val newAmount = currentAmount - reductionAmount

                    document.reference.update("count", newAmount).await()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun increaseCoinAmountByEmail(email: String, increaseAmount: Double ): Boolean {
        return try {
            val querySnapshot = db.collection("coins").whereEqualTo("email", email).get().await()
            val document = querySnapshot.documents.firstOrNull()

            if (document != null) {
                val currentAmount = document.getDouble("count") ?: return false
                val newAmount = currentAmount + increaseAmount

                document.reference.update("count", newAmount).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }


}
