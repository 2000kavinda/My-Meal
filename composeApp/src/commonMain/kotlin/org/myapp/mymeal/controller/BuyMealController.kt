package org.myapp.mymeal.controller

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.tasks.await
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.model.Order
import org.myapp.mymeal.model.Card
import org.myapp.mymeal.utils.Constants
import java.time.LocalDate

class BuyMealController {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getMeals(): List<Meal> {
        return try {
            val snapshot = db.collection("meals").get().await()
            snapshot.documents.map { document ->
                val name = document.getString("name") ?: "Unknown"
                val photo = document.getString("photo") ?: ""
                val price = document.getDouble("price") ?: 0.0
                val description = document.getString("description") ?: "Unknown"
                val type = document.getString("type") ?: "Unknown"
                Meal(name, photo, price,description,type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
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

    suspend fun deductBalance(cardNumber: String, newBalance: Double): Boolean {
        return try {
            val querySnapshot = db.collection("cards").whereEqualTo("card", cardNumber).get().await()
            val document = querySnapshot.documents.firstOrNull()

            if (document != null) {
                document.reference.update("balance", newBalance).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
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

    suspend fun fetchUniqueDateCountExcludingToday(email: String): Int {
        val today = LocalDate.now().toString()

        val snapshot = db.collection("orders")
            .whereEqualTo("email", email)
            .get()
            .await()

        val uniqueDates = snapshot.documents.mapNotNull { doc ->
            doc.getString("day")
        }.filter { day ->
            day != today
        }.toSet()

        return uniqueDates.size
    }

    suspend fun callOpenAIAPI(httpClient: HttpClient, healthStatus: String): String {
        val apiKey = Constants.keyValue
        val apiUrl = "https://api.openai.com/v1/chat/completions"

        val requestBody = """
        {
            "model": "gpt-4",
            "messages": [
                {"role": "system", "content": "You are a helpful assistant."},
                {"role": "user", "content": "Health status: $healthStatus, suggest what should i eat in short manner as a small paragraph"}
            ]
        }
    """

        return try {
            val response: HttpResponse = httpClient.post(apiUrl) {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(requestBody)
            }

            val responseText = response.bodyAsText()
            val contentStart = responseText.indexOf("\"content\": \"") + 12
            val contentEnd = responseText.indexOf("\"", contentStart)

            if (contentStart != -1 && contentEnd != -1) {
                responseText.substring(contentStart, contentEnd)
            } else {
                "Error: Unable to extract content."
            }
        } catch (e: Exception) {
            "Error fetching AI response: ${e.message}"
        }
    }


}
