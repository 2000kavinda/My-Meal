package org.myapp.mymeal.controller

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.myapp.mymeal.model.Meal

class HistoryController {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getOrders(email: String): List<Meal> {
        return try {
            val snapshot = db.collection("orders")
                .whereEqualTo("email", email)
                .whereNotEqualTo("name", "")
                .get()
                .await()

            snapshot.documents.map { document ->
                val name = document.getString("name") ?: "Unknown"
                val photo = document.getString("photo") ?: ""
                val price = document.getDouble("price") ?: 0.0
                val description = document.getString("description") ?: "Unknown"
                val type = document.getString("type") ?: "Unknown"
                Meal(name, photo, price, description, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


}
