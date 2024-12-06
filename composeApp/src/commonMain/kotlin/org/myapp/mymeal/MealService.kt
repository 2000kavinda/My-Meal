import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

class MealService(private val client: HttpClient) {

    suspend fun getMeals(query: String): List<Meal> {
        // Explicitly specify the expected type for deserialization
        val response: MealResponse = client.get("https://www.themealdb.com/api/json/v1/1/search.php?s=$query").body()
        return response.meals ?: emptyList()
    }
}

@Serializable
data class MealResponse(
    val meals: List<Meal>?
)

@Serializable
data class Meal(
    val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val strCategory: String,
    val strArea: String,
    val strInstructions: String,
    val strTags: String? = null,
    val strYoutube: String? = null
)
