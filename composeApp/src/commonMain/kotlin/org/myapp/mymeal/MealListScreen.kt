package org.myapp.mymeal
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.myapp.mymeal.NavigationProvider.navigationManager

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealListScreen(
    repository: FirestoreRepository,
    onMealClick: (Meal) -> Unit,
) {
    val nutritionRepository = remember { NutritionRepository() }
    val firestoreRepository = remember { FirestoreRepository() }
    val httpClient = remember { HttpClient() }

    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var filteredMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var nutritionData by remember { mutableStateOf<NutritionResponse?>(null) }
    var healthMetrics by remember { mutableStateOf<HealthMetrics?>(null) }
    var coins by remember { mutableStateOf(0.0) }
    var aiRecommendations by remember { mutableStateOf("") }

    // State for Bottom Navigation Bar
    val selectedItem = remember { mutableStateOf(0) }

    var meal = Meal(
        name = "Default Meal",
        photo = "https://example.com/default-photo.jpg",
        price = 0.0,
        description = "Default description",
        type = "Default type"
    )


    // State for showing the AI recommendations popup dialog
    var showAIPopup by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            meals = repository.getMeals()
            filteredMeals = meals
            errorMessage = if (meals.isEmpty()) "No meals found" else ""

            val nutrition = firestoreRepository.fetchNutritionData("kavindaudara75@gmail.com")
            coins = firestoreRepository.fetchCoinCount("kavindaudara75@gmail.com")!!
            val dayCount = firestoreRepository.fetchUniqueDateCountExcludingToday("kavindaudara75@gmail.com")
            healthMetrics = calculateHealthMetrics(nutrition, nutritionData, dayCount, "Male", "Moderate", "Maintain Weight")

            healthMetrics?.let { metrics ->
                val healthStatus = metrics.healthStatus
                aiRecommendations = callOpenAIAPIX(httpClient, healthStatus).toString()
            }
        } catch (e: Exception) {
            errorMessage = "Error fetching data: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Enable scrolling
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            healthMetrics?.let { metrics ->
                HealthMetricsCard(metrics = metrics, coins = coins)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (aiRecommendations.isNotEmpty()) {
                // Show the AI recommendations popup dialog
                showAIPopup = true
            } else if (isLoading) {
                Text("Loading AI recommendations...", style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(16.dp))
            }

            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    filteredMeals = if (query.isEmpty()) {
                        meals
                    } else {
                        meals.filter { it.name.contains(query, ignoreCase = true) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                placeholder = { Text(text = "Search meals...") },
                singleLine = true
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage)
            } else {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    filteredMeals.forEach { meal ->
                        MealCard(meal = meal, onMealClick = onMealClick)
                    }
                }
            }
        }

        // Bottom Navigation Bar positioned at the bottom
        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            backgroundColor = MaterialTheme.colors.primarySurface
        ) {
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                selected = selectedItem.value == 0,
                onClick = { selectedItem.value = 0 }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                selected = selectedItem.value == 1,
                onClick = { selectedItem.value = 1 }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
                selected = selectedItem.value == 2,
                onClick = { selectedItem.value = 2 }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") },
                selected = selectedItem.value == 3,
                onClick={
                    navigationManager.navigateTo(Screen.ProfileScreen(meal=meal))
                },
            )
        }
    }

    // AI Recommendations Popup Dialog
    /*if (showAIPopup) {
        AlertDialog(
            onDismissRequest = { showAIPopup = false },
            title = { Text("AI Recommendations") },
            text = { Text(aiRecommendations) },
            confirmButton = {
                TextButton(
                    onClick = { showAIPopup = false }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }*/
}




@Composable
fun AIRecommendationsCard(aiRecommendations: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "AI Recommendations",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = aiRecommendations)
        }
    }
}

@Composable
fun HealthMetricsCard(metrics: HealthMetrics, coins: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Health Status", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Coins Earned: $coins")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Overall Health: ${metrics.healthStatus}",
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun MealCard(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .width(550.dp)
            .padding(8.dp)
            .clickable { onMealClick(meal) },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = meal.photo,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Meal: ${meal.name}", fontWeight = FontWeight.Bold)
            Text(text = "Type: ${meal.type}", fontWeight = FontWeight.Bold)
            Text(text = "Price: $${meal.price}")
        }
    }
}

suspend fun callOpenAIAPIX(httpClient: HttpClient, healthStatus: String): List<String> {
    val apiKey = "ssk-proj-ol3VJytWAEJXgsa5qdKxI6_0J630Oa3SqNskTBqLSJMC2eiG6zPUPUr_qHlnQebHvXU2kUHj8CT3BlbkFJMXq8Oz5vfTBEt7mXvjQcEtdFDCh_aaVlkHZTIpf1M2HwadQkLvadoJCWX4QPICGXv9z5yZkqgA" // Replace with your actual API key
    val apiUrl = "https://api.openai.com/v1/chat/completions"

    val requestBody = """
        {
            "model": "gpt-4",
            "messages": [
                {"role": "system", "content": "You are a helpful assistant."},
                {"role": "user", "content": "Health status: $healthStatus, suggest 3 meals to eat and give only meal names as array format"}
            ]
        }
    """

    return try {
        val response: HttpResponse = httpClient.post(apiUrl) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(requestBody)
        }

        val fullResponseText = response.bodyAsText()
        val responseJson = Json.parseToJsonElement(fullResponseText)
        val choices = responseJson.jsonObject["choices"]?.jsonArray
        val content = choices?.get(0)?.jsonObject?.get("message")?.jsonObject?.get("content")?.jsonPrimitive?.content
        content?.let { Json.parseToJsonElement(it).jsonArray.map { it.jsonPrimitive.content } } ?: emptyList()
    } catch (e: Exception) {
        println("Error: ${e.message}")
        emptyList()
    }
}
