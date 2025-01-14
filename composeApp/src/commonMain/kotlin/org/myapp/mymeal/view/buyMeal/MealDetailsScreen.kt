package org.myapp.mymeal.view.buyMeal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import kotlinx.coroutines.launch
import org.myapp.mymeal.controller.BuyMealController
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.controller.NutritionRepository
import org.myapp.mymeal.controller.NutritionResponse
import org.myapp.mymeal.model.Order
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.controller.GameController
import org.myapp.mymeal.currentPlatform
import org.myapp.mymeal.model.HealthMetrics
import org.myapp.mymeal.ui.theme.ColorThemes


@Composable
fun MealDetailsScreen(meal: Meal, sharedViewModel: SharedViewModel, onBack: () -> Unit,) {
    val nutritionRepository = remember { NutritionRepository() }
    val firestoreRepository = remember { HistoryController() }
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
    val currentUserGender by sharedViewModel.currentUserGender.collectAsState()
    val currentUserGoal by sharedViewModel.currentUserGoal.collectAsState()
    val currentUserActivityLevel by sharedViewModel.currentUserActivityLevel.collectAsState()
    val gameController= GameController()
    val buyMealController=BuyMealController()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val  buyMealController=BuyMealController()
                isLoading = true
                nutritionData = nutritionRepository.getNutritionData(meal.name)
                val meals = buyMealController.fetchNutritionData(currentUserEmail?:"")
                 coins = gameController.fetchCoinCount(currentUserEmail?:"")!!
                val dayCount = buyMealController.fetchUniqueDateCountExcludingToday(currentUserEmail?:"")
                healthMetrics = calculateHealthMetrics(meals, nutritionData, dayCount, currentUserGender?:"", currentUserActivityLevel?:"", currentUserGoal?:"")
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
                apiResponse = buyMealController.callOpenAIAPI(httpClient, healthMetrics!!.healthStatus)
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
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                        .verticalScroll(scrollState)
                ) {
                    MealImageAndDetails(meal)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    MealAdditionalDetails(sharedViewModel,coins,
                        meal, isLoading, errorMessage, nutritionData, healthMetrics, isApiLoading, apiResponse
                    ) { showCardDetailsDialog = true }
                }
            }
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                MealImageAndDetails(meal)
                Spacer(modifier = Modifier.height(15.dp))

                MealAdditionalDetails(sharedViewModel,coins,
                    meal,isLoading, errorMessage, nutritionData, healthMetrics, isApiLoading, apiResponse
                ) { showCardDetailsDialog = true }
            }
        }

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



fun calculateHealthMetrics(
    meals: List<Order>,
    nutritionData: NutritionResponse?,
    dayCount: Int,
    gender: String,
    activityLevel: String,
    goal: String
): HealthMetrics {
    var totalCalories = 0.0
    var totalCarbs = 0.0
    var totalProteins = 0.0
    var totalFats = 0.0

    meals.forEach { meal ->
        totalCalories += meal.calories
        totalCarbs += meal.carbohydrates
        totalProteins += meal.proteins
        totalFats += meal.fats
    }

    nutritionData?.items?.firstOrNull()?.let { nutritionItem ->
        totalCalories += nutritionItem.calories
        totalCarbs += nutritionItem.carbohydrates_total_g
        totalProteins += nutritionItem.protein_g
        totalFats += nutritionItem.fat_total_g
    }

    val calorieAverage = totalCalories / (dayCount + 1)
    val carbAverage = totalCarbs / (dayCount + 1)
    val proteinAverage = totalProteins / (dayCount + 1)
    val fatAverage = totalFats / (dayCount + 1)
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

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${meal.name} is ${healthMetrics.healthStatus}",
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
                color = if (healthMetrics.healthStatus.contains("Healthy"))
                    ColorThemes.PrimaryGreenColor else MaterialTheme.colors.error
            )
        )
    }
}




