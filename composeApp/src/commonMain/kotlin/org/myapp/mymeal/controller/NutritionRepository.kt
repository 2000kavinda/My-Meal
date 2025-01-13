package org.myapp.mymeal.controller

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class NutritionRepository {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Ignore extra fields in the response
            })
        }
    }

    suspend fun getNutritionData(query: String): NutritionResponse {
        return client.get("https://api.calorieninjas.com/v1/nutrition") {
            url {
                parameters.append("query", query)
            }
            headers {
                append("X-Api-Key", "54EXf/SPDEA+b2lyROsTSg==8GKxlxVUe1Hayn3w")
            }
        }.body()
    }
}

@Serializable
data class NutritionResponse(
    val items: List<NutritionItem>
)

@Serializable
data class NutritionItem(
    val name: String,
    val calories: Double,
    val serving_size_g: Double,
    val fat_total_g: Double,
    val fat_saturated_g: Double,
    val protein_g: Double,
    val sodium_mg: Int,
    val potassium_mg: Int,
    val cholesterol_mg: Int,
    val carbohydrates_total_g: Double,
    val fiber_g: Double,
    val sugar_g: Double
)
