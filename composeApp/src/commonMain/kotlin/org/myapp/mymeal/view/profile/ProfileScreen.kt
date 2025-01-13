package org.myapp.mymeal.view.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import kotlinx.coroutines.launch
import org.myapp.mymeal.controller.NutritionResponse
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.components.BottomNavigationBar
import org.myapp.mymeal.components.NutriCard
import org.myapp.mymeal.components.NutrientCard
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.currentPlatform
import org.myapp.mymeal.model.HealthMetrics
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.ui.theme.ColorThemes
import org.myapp.mymeal.ui.theme.FontSizes
import org.myapp.mymeal.view.buyMeal.calculateHealthMetrics
import org.myapp.mymeal.view.buyMeal.callOpenAIAPI

@Composable
fun ProfileScreen(
    meal: Meal,
    sharedViewModel: SharedViewModel,
) {

    val firestoreRepository = remember { FirestoreRepository() }
    val httpClient = remember { HttpClient() }
    val nutritionData by remember { mutableStateOf<NutritionResponse?>(null) }
    var healthMetrics by remember { mutableStateOf<HealthMetrics?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var apiResponse by remember { mutableStateOf<String?>(null) }
    var isApiLoading by remember { mutableStateOf(false) }
    var coins by remember { mutableStateOf(0.0) }
    val selectedItem = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    val currentUserGender by sharedViewModel.currentUserGender.collectAsState()
    val currentUserGoal by sharedViewModel.currentUserGoal.collectAsState()
    val currentUserActivityLevel by sharedViewModel.currentUserActivityLevel.collectAsState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoading = true

                val meals = firestoreRepository.fetchNutritionData(currentUserEmail?:"")
                coins = firestoreRepository.fetchCoinCount(currentUserEmail?:"")!!
                val dayCount = firestoreRepository.fetchUniqueDateCountExcludingToday(currentUserEmail?:"")
                healthMetrics = calculateHealthMetrics(
                    meals,
                    nutritionData,
                    dayCount,
                    currentUserGender?:"",
                    currentUserActivityLevel?:"",
                    currentUserGoal?:""
                )
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

                bottomBar = {
                    BottomNavigationBar(
                        selectedItem = selectedItem.value,
                        onItemSelected = { selectedItem.value = it },
                        onProfileClick = {
                            navigationManager.navigateTo(Screen.ProfileScreen(meal = meal))
                        }
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
                    ProfileDetails(meal, sharedViewModel)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    MealOtherDetails(
                         coins,
                        meal, isLoading, errorMessage, healthMetrics, isApiLoading,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {

                MealOtherDetails(
                    coins,
                    meal, isLoading, errorMessage, healthMetrics, isApiLoading,
                )
                Spacer(modifier = Modifier.height(40.dp))
                ProfileDetails(meal, sharedViewModel)
            }
        }
    }
}

@Composable
fun HealthMetricsDisplay1(healthMetrics: HealthMetrics, meal: Meal) {
    Column(horizontalAlignment = Alignment.Start) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = healthMetrics.healthStatus,
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
                color = if (healthMetrics.healthStatus.contains("Healthy"))
                    ColorThemes.PrimaryGreenColor else MaterialTheme.colors.error
            )
        )
    }
}

@Composable
fun NutritionDetails1(healthMetrics: HealthMetrics) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "User Body Average Nutrition Level",
                fontWeight = FontWeight.Bold,
                fontSize = FontSizes.font22,
                color = ColorThemes.PrimaryBlackColor,
                modifier = Modifier
                    .padding(start = 0.dp)
                    .align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))


            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                NutriCard(value = healthMetrics.calorieAverage.toInt(), color = ColorThemes.CalorieColor)

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutrientCard(
                        label = "PROTEIN",
                        value = healthMetrics.proteinAvg.toInt(),
                        color = ColorThemes.ProteinColor
                    )
                    NutrientCard(
                        label = "CARBS",
                        value = healthMetrics.carbAvg.toInt(),
                        color = ColorThemes.CarbColor
                    )
                    NutrientCard(
                        label = "FAT",
                        value = healthMetrics.fatAvg.toInt(),
                        color = ColorThemes.FatColor
                    )
                }
            }
        }

}



