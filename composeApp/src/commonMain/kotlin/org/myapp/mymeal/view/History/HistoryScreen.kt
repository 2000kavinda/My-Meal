package org.myapp.mymeal.view.History
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.ktor.client.HttpClient
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.NutritionRepository
import org.myapp.mymeal.NutritionResponse
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.view.HomeAndBuyMeal.calculateHealthMetrics
import org.myapp.mymeal.components.BottomNavigationBar
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.model.HealthMetrics

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
    var meal = Meal(
        name = "Default Meal",
        photo = "https://example.com/default-photo.jpg",
        price = 0.0,
        description = "Default description",
        type = "Default type"
    )
    val selectedItem = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            meals = repository.getOrders("kavindaudara75@gmail.com")
            filteredMeals = meals
            errorMessage = if (meals.isEmpty()) "No meals found" else ""

            val nutrition = firestoreRepository.fetchNutritionData("kavindaudara75@gmail.com")
            coins = firestoreRepository.fetchCoinCount("kavindaudara75@gmail.com") ?: 0.0
            val dayCount = firestoreRepository.fetchUniqueDateCountExcludingToday("kavindaudara75@gmail.com")
            healthMetrics = calculateHealthMetrics(nutrition, nutritionData, dayCount, "Male", "Moderate", "Maintain Weight")
        } catch (e: Exception) {
            errorMessage = "Error fetching data: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Main Content Scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(end = 16.dp,
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 56.dp), // Reserve space for the bottom bar
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
                        .padding(8.dp)
                ) {
                    filteredMeals.forEach { meal ->
                        MealCard1(meal = meal, onMealClick = onMealClick)
                    }
                }
            }
        }

        // Bottom Navigation Bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                ) {
            BottomNavigationBar(
                selectedItem = selectedItem.value,
                onItemSelected = { selectedItem.value = it },
                onProfileClick = {
                    navigationManager.navigateTo(Screen.ProfileScreen(meal = meal))
                }
            )
        }
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







@Composable
fun MealCard1(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .width(600.dp)
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
                Text(text = "${meal.name}", fontWeight = FontWeight.Bold)
                Text(text = "${meal.type}")
                Text(text = "$${meal.price}", fontWeight = FontWeight.Bold)
            }

            // Spacer between columns
            Spacer(modifier = Modifier.width(16.dp))

            // Third Column: Reorder Button
            Button(
                onClick = { onMealClick(meal) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945)), // Replace with your desired hex code
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
            ) {
                Text(text = "Reorder", color = Color.White) // Set text color if needed
            }

        }
    }
}




