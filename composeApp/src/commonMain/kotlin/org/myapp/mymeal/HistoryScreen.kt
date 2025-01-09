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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
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

    // State for showing the AI recommendations popup dialog
    var showAIPopup by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            meals = repository.getOrders("kavindaudara75@gmail.com")
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
                        MealCard1(meal = meal, onMealClick = onMealClick)
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
                onClick = { selectedItem.value = 3 }
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
fun MealCard1(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .width(550.dp)
            .padding(8.dp)
            .clickable { onMealClick(meal) },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First Column: Meal Image
            AsyncImage(
                model = meal.photo,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .size(100.dp) // Set a fixed size for the image
            )

            // Spacer between columns
            Spacer(modifier = Modifier.width(16.dp))

            // Second Column: Text Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Meal: ${meal.name}", fontWeight = FontWeight.Bold)
                Text(text = "Type: ${meal.type}", fontWeight = FontWeight.Bold)
                Text(text = "Price: $${meal.price}")
            }

            // Spacer between columns
            Spacer(modifier = Modifier.width(16.dp))

            // Third Column: Reorder Button
            Button(
                onClick = { /* Handle reorder logic here */ },
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
            ) {
                Text(text = "Reorder")
            }
        }
    }
}

