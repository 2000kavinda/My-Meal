package org.myapp.mymeal

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun MealDetailsScreen(meal: Meal, onBack: () -> Unit) {
    val nutritionRepository = remember { NutritionRepository() }
    var nutritionData by remember { mutableStateOf<NutritionResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Fetch nutrition data when the screen is loaded
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoading = true
                nutritionData = nutritionRepository.getNutritionData(meal.name)
            } catch (e: Exception) {
                errorMessage = "Error fetching nutrition data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {

                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = meal.photo,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Meal: ${meal.name}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h5
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: $${meal.price}",
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display loading, error, or nutrition data
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage.isNotEmpty() -> {
                    Text(text = errorMessage, color = MaterialTheme.colors.error)
                }
                nutritionData != null -> {
                    NutritionDetails(nutritionData = nutritionData!!)
                }
                else -> {
                    Text(text = "No nutrition data available.")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}

@Composable
fun NutritionDetails(nutritionData: NutritionResponse) {
    val nutritionItem = nutritionData.items.firstOrNull()

    if (nutritionItem != null) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Nutrition Information", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Calories: ${nutritionItem.calories}")
            Text(text = "Serving Size: ${nutritionItem.serving_size_g}g")
            Text(text = "Total Fat: ${nutritionItem.fat_total_g}g")
            Text(text = "Saturated Fat: ${nutritionItem.fat_saturated_g}g")
            Text(text = "Protein: ${nutritionItem.protein_g}g")
            Text(text = "Sodium: ${nutritionItem.sodium_mg}mg")
            Text(text = "Potassium: ${nutritionItem.potassium_mg}mg")
            Text(text = "Cholesterol: ${nutritionItem.cholesterol_mg}mg")
            Text(text = "Carbohydrates: ${nutritionItem.carbohydrates_total_g}g")
            Text(text = "Fiber: ${nutritionItem.fiber_g}g")
            Text(text = "Sugar: ${nutritionItem.sugar_g}g")
        }
    } else {
        Text(text = "No detailed nutrition data found.")
    }
}
