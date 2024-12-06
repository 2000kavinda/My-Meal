package org.myapp.mymeal

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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

    suspend fun getMeals(): List<Meal> {
        return try {
            // Fetch meals collection from Firestore
            val snapshot = db.collection("meals").get().await()

            // Map the snapshot to a list of Meal objects
            snapshot.documents.map { document ->
                val name = document.getString("name") ?: "Unknown" // Default to "Unknown" if name is missing
                val photo = document.getString("photo") ?: "" // Default to empty string if photo is missing
                val price = document.getDouble("price") ?: 0.0 // Default to 0.0 if price is missing
                Meal(name, photo, price)
            }
        } catch (e: Exception) {
            e.printStackTrace() // Print the error for debugging purposes
            emptyList() // Return an empty list if there's an error
        }
    }
}
