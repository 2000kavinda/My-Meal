package org.myapp.mymeal.controller

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.myapp.mymeal.model.Order
import org.myapp.mymeal.model.User
import org.myapp.mymeal.model.Coin

class AuthController {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addUser(user: User): Result<String> {
        return try {
            val existingUser = db.collection("users")
                .whereEqualTo("email", user.email)
                .get()
                .await()

            if (!existingUser.isEmpty) {

                Result.failure(Exception("User with this email is already registered."))
            } else {

                val document = db.collection("users").add(user).await()

                saveOrder(
                    Order(
                        name = "",
                        calories = 3000.0,
                        carbohydrates = 400.0,
                        proteins = 125.0,
                        fats = 120.0,
                        price = 0.0,
                        photo = "",
                        email = user.email,
                        description = "",
                        type = ""
                    )
                )
                saveCoin(Coin(
                    count = 0.0,
                    email = user.email
                ))
                Result.success(document.id)

            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getUserByEmail(email: String): User? {
        val snapshot = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
        return if (!snapshot.isEmpty) {
            snapshot.documents.first().toObject(User::class.java)
        } else null
    }


    suspend fun saveOrder(order: Order) {
        db.collection("orders")
            .add(order)
            .addOnSuccessListener {
                Log.d("Firestore", "Order saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving order", e)
            }
    }

    suspend fun saveCoin(coin: Coin) {
        db.collection("coins")
            .add(coin)
            .addOnSuccessListener {
                Log.d("Firestore", "Order saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving order", e)
            }
    }

}
