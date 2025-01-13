package org.myapp.mymeal.view.HomeAndBuyMeal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.myapp.mymeal.Meal
import org.myapp.mymeal.NutritionRepository
import org.myapp.mymeal.NutritionResponse
import org.myapp.mymeal.Order
import org.myapp.mymeal.SharedViewModel
import org.myapp.mymeal.components.NutrientCard
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.currentPlatform
import org.myapp.mymeal.model.HealthMetrics


@Composable
fun MealDetailsScreen(meal: Meal, sharedViewModel: SharedViewModel, onBack: () -> Unit,) {
    val nutritionRepository = remember { NutritionRepository() }
    val firestoreRepository = remember { FirestoreRepository() }
    val httpClient = remember { HttpClient() }

    var nutritionData by remember { mutableStateOf<NutritionResponse?>(null) }
    var healthMetrics by remember { mutableStateOf<HealthMetrics?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var apiResponse by remember { mutableStateOf<String?>(null) }
    var isApiLoading by remember { mutableStateOf(false) }
    var showCardDetailsDialog by remember { mutableStateOf(false) }
    var coins by remember { mutableStateOf(0.0) }

    val coroutineScope = rememberCoroutineScope()
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()

    // Fetch data when the screen loads
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoading = true
                nutritionData = nutritionRepository.getNutritionData(meal.name)
                val meals = firestoreRepository.fetchNutritionData(currentUserEmail?:"")
                 coins = firestoreRepository.fetchCoinCount(currentUserEmail?:"")!!
                val dayCount = firestoreRepository.fetchUniqueDateCountExcludingToday(currentUserEmail?:"")
                healthMetrics = calculateHealthMetrics(meals, nutritionData, dayCount, "Male", "Moderate", "Maintain Weight")
            } catch (e: Exception) {
                errorMessage = "Error fetching data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(healthMetrics) {
        if (healthMetrics != null && healthMetrics!!.healthStatus.isNotEmpty()) {
            isApiLoading = true
            coroutineScope.launch {
                apiResponse = callOpenAIAPI(httpClient, healthMetrics!!.healthStatus)
                isApiLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",tint = Color.White)
                    }
                },backgroundColor = Color(0xFF002945)
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()

        if (currentPlatform == "Desktop") {
            // Desktop Layout: Two Columns
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
            ) {
                // First Column (Meal Image and Details)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                        .verticalScroll(scrollState) // Add scrolling to this column
                ) {
                    MealImageAndDetails(meal)
                }

                // Second Column (Additional Details)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()) // Add scrolling to the second column as well
                ) {
                    MealAdditionalDetails(sharedViewModel,coins,
                        meal, isLoading, errorMessage, nutritionData, healthMetrics, isApiLoading, apiResponse
                    ) { showCardDetailsDialog = true }
                }
            }
        }
        else {
            // Android Layout: Single Column with Scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(scrollState) // Add scrolling
            ) {
                MealImageAndDetails(meal)
                Spacer(modifier = Modifier.height(15.dp))

                MealAdditionalDetails(sharedViewModel,coins,
                    meal,isLoading, errorMessage, nutritionData, healthMetrics, isApiLoading, apiResponse
                ) { showCardDetailsDialog = true }
            }
        }

        // Show Dialog
        if (showCardDetailsDialog) {
            CardDetailsDialog(
                sharedViewModel = sharedViewModel,
                meal = meal,
                nutritionData = nutritionData,
                email = currentUserEmail?:"",
                onDismiss = { showCardDetailsDialog = false }
            )
        }
    }
}


@Composable
fun AIResponseDisplay(apiResponse: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Split the response by newline, filter out blank lines, and display the content
        val responseLines = apiResponse.split("\n").filter { it.isNotBlank() }

        responseLines.forEach { line ->
            Text(
                text = line,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}


//add to the service
suspend fun callOpenAIAPI(httpClient: HttpClient, healthStatus: String): String {
    val apiKey = "ssk-proj-ol3VJytWAEJXgsa5qdKxI6_0J630Oa3SqNskTBqLSJMC2eiG6zPUPUr_qHlnQebHvXU2kUHj8CT3BlbkFJMXq8Oz5vfTBEt7mXvjQcEtdFDCh_aaVlkHZTIpf1M2HwadQkLvadoJCWX4QPICGXv9z5yZkqgA" // Replace with your API key
    val apiUrl = "https://api.openai.com/v1/chat/completions"

    val requestBody = """
        {
            "model": "gpt-4",
            "messages": [
                {"role": "system", "content": "You are a helpful assistant."},
                {"role": "user", "content": "Health status: $healthStatus, give recommendations in short manner as a small paragraph"}
            ]
        }
    """

    return try {
        val response: HttpResponse = httpClient.post(apiUrl) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(requestBody)
        }

        // Parse the response to extract only the content
        val responseText = response.bodyAsText()
        val contentStart = responseText.indexOf("\"content\": \"") + 12
        val contentEnd = responseText.indexOf("\"", contentStart)

        // Extract the content between the quotation marks
        if (contentStart != -1 && contentEnd != -1) {
            responseText.substring(contentStart, contentEnd)
        } else {
            "Error: Unable to extract content."
        }
    } catch (e: Exception) {
        "Error fetching AI response: ${e.message}"
    }
}

fun calculateHealthMetrics(
    // "Male", "Moderate", "Maintain Weight"
    meals: List<Order>,
    nutritionData: NutritionResponse?,
    dayCount: Int,
    gender: String, // "Male" or "Female"
    activityLevel: String, // "Sedentary", "Lightly Active", "Active", "Very Active"
    goal: String // "Weight Loss", "Muscle Gain", "Maintain Weight"
): HealthMetrics {
    var totalCalories = 0.0
    var totalCarbs = 0.0
    var totalProteins = 0.0
    var totalFats = 0.0

    // Sum up nutrients from meals
    meals.forEach { meal ->
        totalCalories += meal.calories
        totalCarbs += meal.carbohydrates
        totalProteins += meal.proteins
        totalFats += meal.fats
    }

    // Include nutrition data from an external source (if available)
    nutritionData?.items?.firstOrNull()?.let { nutritionItem ->
        totalCalories += nutritionItem.calories
        totalCarbs += nutritionItem.carbohydrates_total_g
        totalProteins += nutritionItem.protein_g
        totalFats += nutritionItem.fat_total_g
    }

    // Calculate averages per day
    val calorieAverage = totalCalories / (dayCount + 1)
    val carbAverage = totalCarbs / (dayCount + 1)
    val proteinAverage = totalProteins / (dayCount + 1)
    val fatAverage = totalFats / (dayCount + 1)
// Adjust thresholds based on user metadata
    val calorieLimit = when (activityLevel) {
        "Low" -> 1800.0
        "Moderate" -> 2200.0
        "High" -> 2600.0
        else -> 2000.0
    }
    val carbRange = when (goal) {
        "Weight Loss" -> 130.0..250.0
        "Maintain" -> 150.0..325.0
        "Muscle Gain" -> 200.0..350.0
        else -> 150.0..325.0
    }
    val proteinRange = when (goal) {
        "Weight Loss" -> 50.0..150.0
        "Maintain" -> 50.0..175.0
        "Muscle Gain" -> 70.0..200.0
        else -> 50.0..175.0
    }
    val fatRange = when (gender) {
        "Male" -> 44.0..77.0
        "Female" -> 40.0..70.0
        else -> 44.0..77.0
    }

    // Update health status logic
    val healthStatus = when {
        calorieAverage > calorieLimit -> "Unhealthy: High Calorie Intake"
        carbAverage !in carbRange -> "Unhealthy: Your Carbohydrate Intake Out of Range "
        proteinAverage !in proteinRange -> "Unhealthy: Your Protein Intake Out of Range "
        fatAverage !in fatRange -> "Unhealthy: Your Fat Intake Out of Range "
        else -> "Healthy for You"
    }

    return HealthMetrics(
        calorieAverage = calorieAverage,
        carbAvg = carbAverage,
        proteinAvg = proteinAverage,
        fatAvg = fatAverage,
        healthStatus = healthStatus
    )
}


@Composable
fun HealthMetricsDisplay(healthMetrics: HealthMetrics,meal: Meal) {
    Column(horizontalAlignment = Alignment.Start) {
        //Text(text = "Health Metrics", style = MaterialTheme.typography.h6)
        //Spacer(modifier = Modifier.height(8.dp))
        //Text(text = "Total Calories: ${"%.2f".format(healthMetrics.calorieAverage)}")
        //Text(text = "Carbohydrate Percentage: ${"%.2f".format(healthMetrics.carbAvg)}g")
        //Text(text = "Protein Percentage: ${"%.2f".format(healthMetrics.proteinAvg)}g")
        //Text(text = "Fat Percentage: ${"%.2f".format(healthMetrics.fatAvg)}g")
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${meal.name} is ${healthMetrics.healthStatus}",
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
                color = if (healthMetrics.healthStatus.contains("Healthy"))
                    MaterialTheme.colors.primary else MaterialTheme.colors.error
            )
        )
    }
}





/*
suspend fun callOpenAIAPI(httpClient: HttpClient, healthStatus: String): String {
    val apiKey = "sk-proj-gMJ2i6pc81rlfAZD9rnKT3BlbkFJic8JE5LUhTfmF45JMhi5" // Replace with your API key
    val apiUrl = "https://api.openai.com/v1/chat/completions"

    val requestBody = """
        {
            "model": "gpt-4",
            "messages": [
                {"role": "system", "content": "You are a helpful assistant."},
                {"role": "user", "content": "Health status: $healthStatus why is that and give recommendations in short manner"}
            ]
        }
    """

    return try {
        val response: HttpResponse = httpClient.post(apiUrl) {
            header(HttpHeaders.Authorization, "Bearer $apiKey")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(requestBody)
        }
        response.bodyAsText()
    } catch (e: Exception) {
        "Error fetching AI response: ${e.message}"
    }
}
*/