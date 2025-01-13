package org.myapp.mymeal.controller

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.model.Order
import org.myapp.mymeal.model.User
import org.myapp.mymeal.model.Card
import org.myapp.mymeal.model.Coin
import org.myapp.mymeal.state.SharedViewModel
import java.time.LocalDate

class FirestoreRepository {

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
                        calories = 4000.0,
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

                // Save the order details to Firestore

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

    suspend fun fetchMeals(): List<Meal> {
        val mealsCollection = FirebaseFirestore.getInstance().collection("meals")
        return mealsCollection.get()
            .await() // Ensure you're using kotlinx-coroutines-play-services for this.
            .documents.mapNotNull { it.toObject(Meal::class.java) }
    }

    suspend fun getUsers(): List<User> {
        return try {
            // Fetch users collection from Firestore
            val snapshot = db.collection("users").get().await()

            // Map the snapshot to a list of User objects
            snapshot.documents.map { document ->
                val email = document.getString("email") ?: ""
                val password = document.getString("password") ?: ""
                User(email, password)
            }
        } catch (e: Exception) {
            emptyList() // Return empty list if there's an error
        }
    }

    suspend fun getMealsByNamesg(mealNames: List<String>): List<Meal> {
        return try {
            // Fetch meals from Firestore where the name is in the provided list
            val snapshot = db.collection("meals")
                .whereIn("name", mealNames)
                .get()
                .await()

            // Map the snapshot to a list of Meal objects
            snapshot.documents.map { document ->
                val name = document.getString("name") ?: "Unknown" // Default to "Unknown" if name is missing
                val photo = document.getString("photo") ?: "" // Default to empty string if photo is missing
                val price = document.getDouble("price") ?: 0.0 // Default to 0.0 if price is missing
                val description = document.getString("description") ?: "Unknown" // Default to "Unknown" if description is missing
                val type = document.getString("type") ?: "Unknown" // Default to "Unknown" if type is missing
                Meal(name, photo, price, description, type)
            }
        } catch (e: Exception) {
            e.printStackTrace() // Print the error for debugging purposes
            emptyList() // Return an empty list if there's an error
        }
    }


    suspend fun getMeals(): List<Meal> {
        return try {
            // Fetch meals collection from Firestore
            val snapshot = db.collection("meals").get().await()

            // Map the snapshot to a list of Meal objects
            snapshot.documents.map { document ->
                val name = document.getString("name") ?: "Unknown" // Default to "Unknown" if name is missing
                val photo = document.getString("photo") ?: "" // Default to empty string if photo is missing
                val price = document.getDouble("price") ?: 0.0 // Default to 0.0 if price is missing
                val description = document.getString("description") ?: "Unknown"
                val type = document.getString("type") ?: "Unknown"
                Meal(name, photo, price,description,type)
            }
        } catch (e: Exception) {
            e.printStackTrace() // Print the error for debugging purposes
            emptyList() // Return an empty list if there's an error
        }
    }

    suspend fun getOrders(email: String): List<Meal> {
        return try {
            // Fetch meals collection from Firestore filtered by email
            val snapshot = db.collection("orders")
                .whereEqualTo("email", email)  // Filter by email
                .whereNotEqualTo("name", "")   // Filter where name is not equal to an empty string
                .get()
                .await()


            // Map the snapshot to a list of Meal objects
            snapshot.documents.map { document ->
                val name = document.getString("name") ?: "Unknown" // Default to "Unknown" if name is missing
                val photo = document.getString("photo") ?: "" // Default to empty string if photo is missing
                val price = document.getDouble("price") ?: 0.0 // Default to 0.0 if price is missing
                val description = document.getString("description") ?: "Unknown"
                val type = document.getString("type") ?: "Unknown"
                Meal(name, photo, price, description, type)
            }
        } catch (e: Exception) {
            e.printStackTrace() // Print the error for debugging purposes
            emptyList() // Return an empty list if there's an error
        }
    }

    suspend fun fetchNutritionData(email: String): List<Order> {
        val snapshot = db.collection("orders")
            .whereEqualTo("email", email)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Order::class.java)
        }
    }

    suspend fun fetchCardDetails(cardNumber: String): Card? {
        return try {
            val document = db.collection("cards").whereEqualTo("card", cardNumber).get().await()
            document.documents.firstOrNull()?.toObject(Card::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /*suspend fun fetchCardDetails(cardNumber: String): CardDetails? {
        val docRef = db.collection("cards").document(cardNumber)
        val document = docRef.get().await()
        return if (document.exists()) {
            document.toObject(CardDetails::class.java)
        } else {
            null
        }
    }*/

    suspend fun fetchCoinCount(email: String): Double? {
        return try {
            val document = db.collection("coins").whereEqualTo("email", email).get().await()
            document.documents.firstOrNull()?.getDouble("count")
        } catch (e: Exception) {
            null
        }
    }



    // Deduct balance from the card
    suspend fun deductBalance(cardNumber: String, newBalance: Double): Boolean {
        return try {
            val querySnapshot = db.collection("cards").whereEqualTo("card", cardNumber).get().await()
            val document = querySnapshot.documents.firstOrNull()

            if (document != null) {
                document.reference.update("balance", newBalance).await()
                true // Update was successful
            } else {
                false // Card not found
            }
        } catch (e: Exception) {
            false // An error occurred
        }
    }
    suspend fun reduceCoinAmountByEmail(email: String, reductionAmount: Double ): Boolean {
        return try {
            // Query the "coins" collection to find a document with the specified email
            val querySnapshot = db.collection("coins").whereEqualTo("email", email).get().await()
            val document = querySnapshot.documents.firstOrNull()

            if (document != null) {
                // Retrieve the current amount and reduce it by 5.5
                val currentAmount = document.getDouble("count") ?: return false
                if (currentAmount >= reductionAmount) {
                    val newAmount = currentAmount - reductionAmount

                    // Update the "amount" field in the document
                    document.reference.update("count", newAmount).await()
                    true // Update was successful
                } else {
                    false // Insufficient balance
                }
            } else {
                false // No document found for the given email
            }
        } catch (e: Exception) {
            false // An error occurred
        }
    }

    suspend fun increaseCoinAmountByEmail(email: String, increaseAmount: Double ): Boolean {
        return try {
            // Query the "coins" collection to find a document with the specified email
            val querySnapshot = db.collection("coins").whereEqualTo("email", email).get().await()
            val document = querySnapshot.documents.firstOrNull()

            if (document != null) {
                // Retrieve the current amount and increase it by the specified amount
                val currentAmount = document.getDouble("count") ?: return false
                val newAmount = currentAmount + increaseAmount

                // Update the "count" field in the document
                document.reference.update("count", newAmount).await()
                true // Update was successful
            } else {
                false // No document found for the given email
            }
        } catch (e: Exception) {
            false // An error occurred
        }
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




    suspend fun fetchUniqueDateCountExcludingToday(email: String): Int {
        val today = LocalDate.now().toString() // Get today's date in the format "yyyy-MM-dd"

        val snapshot = db.collection("orders")
            .whereEqualTo("email", email)
            .get()
            .await()

        // Extract the `day` field, filter out today's date, and count unique days
        val uniqueDates = snapshot.documents.mapNotNull { doc ->
            doc.getString("day") // Extract the 'day' field as a String
        }.filter { day ->
            day != today // Exclude today's date
        }.toSet() // Use a Set to ensure only unique days are considered

        return uniqueDates.size // Return the count of unique days excluding today
    }

}
