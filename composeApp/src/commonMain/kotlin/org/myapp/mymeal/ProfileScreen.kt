package org.myapp.mymeal

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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
//import coil.compose.AsyncImage
import coil3.compose.AsyncImage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.myapp.mymeal.NavigationProvider.navigationManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class HealthMetrics1(
    val calorieAverage: Double,
    val carbAvg: Double,
    val proteinAvg: Double,
    val fatAvg: Double,
    val healthStatus: String
)

@Composable
fun ProfileScreen(
    meal: Meal,
    sharedViewModel: SharedViewModel,
    onBack: () -> Unit,
) {
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
    val selectedItem = remember { mutableStateOf(0) } // State for selected navigation item
    val coroutineScope = rememberCoroutineScope()
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()

    // Fetch data when the screen loads
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoading = true

                val meals = firestoreRepository.fetchNutritionData("kavindaudara75@gmail.com")
                coins = firestoreRepository.fetchCoinCount("kavindaudara75@gmail.com")!!
                val dayCount = firestoreRepository.fetchUniqueDateCountExcludingToday("kavindaudara75@gmail.com")
                healthMetrics = calculateHealthMetrics(
                    meals,
                    nutritionData,
                    dayCount,
                    "Male",
                    "Moderate",
                    "Maintain Weight"
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
        topBar = {
            TopAppBar(
                title = { Text("Meal Details, $currentUserEmail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.primarySurface
            ) {
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    selected = selectedItem.value == 0,
                    onClick = {
                        navigationManager.navigateTo(Screen.MealList)
                    }
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
                    onClick = {
                        navigationManager.navigateTo(Screen.ProfileScreen(meal = meal))
                    }
                )
            }
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
                        .verticalScroll(scrollState)
                ) {
                    ProfileDetails(meal, sharedViewModel)
                }

                // Second Column (Additional Details)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    MealAdditionalDetails1(
                        sharedViewModel, coins,
                        meal, isLoading, errorMessage, nutritionData, healthMetrics, isApiLoading, apiResponse
                    ) { showCardDetailsDialog = true }
                }
            }
        } else {
            // Android Layout: Single Column with Scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                MealImageAndDetails(meal)
                Spacer(modifier = Modifier.height(15.dp))

                MealAdditionalDetails(
                    sharedViewModel, coins,
                    meal, isLoading, errorMessage, nutritionData, healthMetrics, isApiLoading, apiResponse
                ) { showCardDetailsDialog = true }
            }
        }
    }
}


@Composable
fun ProfileDetails(meal: Meal, sharedViewModel: SharedViewModel) {
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    val initials = currentUserEmail?.take(2)?.uppercase() ?: "NA"
    val currentUserActivityLevel by sharedViewModel.currentUserActivityLevel.collectAsState()
    val currentUserGender by sharedViewModel.currentUserGender.collectAsState()
    val currentUserGoal by sharedViewModel.currentUserGoal.collectAsState()

    // Row to center the rounded container horizontally
    Row(
        horizontalArrangement = Arrangement.Center, // Center the content horizontally
        modifier = Modifier.fillMaxWidth() // Make sure the row takes up the full width
    ) {
        Box(
            modifier = Modifier
                .size(60.dp) // Set the size of the container
                .clip(CircleShape) // Make the container rounded
                .background(MaterialTheme.colors.primary), // Set background color
            contentAlignment = Alignment.Center // Center the text inside the Box
        ) {
            Text(
                text = initials, // Set the initials
                color = Color.White, // Text color
                style = MaterialTheme.typography.h6, // Text style
                fontWeight = FontWeight.Bold // Make the text bold
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Other Rows for displaying user info
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Email,
            contentDescription = "User Email Icon",
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = currentUserEmail ?: "No email available",
            fontWeight = FontWeight.Bold,
            fontSize = 27.sp
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Person,
            contentDescription = "User Gender Icon",
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = currentUserGender ?: "No gender available",
            fontWeight = FontWeight.Bold,
            fontSize = 27.sp
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = "User Goal Icon",
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = currentUserGoal ?: "No goal available",
            fontWeight = FontWeight.Bold,
            fontSize = 27.sp
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.DateRange,
            contentDescription = "User Activity Level Icon",
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = currentUserActivityLevel ?: "No activity level available",
            fontWeight = FontWeight.Bold,
            fontSize = 27.sp
        )
    }
    Button(
        onClick = {

        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Logout"
        )
    }
}

@Composable
fun MealAdditionalDetails1(
    sharedViewModel: SharedViewModel,
    coins: Double?,
    meal: Meal,
    isLoading: Boolean,
    errorMessage: String,
    nutritionData: NutritionResponse?,
    healthMetrics: HealthMetrics?,
    isApiLoading: Boolean,
    apiResponse: String?,
    onShowCardDetails: () -> Unit
) {
    when {
        isLoading -> CircularProgressIndicator()
        errorMessage.isNotEmpty() -> Text(text = errorMessage, color = MaterialTheme.colors.error)
         healthMetrics != null -> {
            NutritionDetails1(healthMetrics = healthMetrics)
            // Add additional health metrics or other UI
            HealthMetricsDisplay(healthMetrics = healthMetrics, meal = meal)
        }
        else -> Text(text = "No nutrition data available.")
    }

    if (isApiLoading) {
        CircularProgressIndicator()
    } else {
        /*apiResponse?.let {
            AIResponseDisplay(apiResponse = it)
        }
        Button(
            onClick = {
                sharedViewModel.setPayAmount(meal.price)
                onShowCardDetails() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay $"+meal.price)
        }

        Button(
            onClick = {
                sharedViewModel.setPayAmount(meal.price - (coins ?: 0.0)) // This should be a separate statement
                onShowCardDetails() // Call the function
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Pay $" + (meal.price - (coins ?: 0.0)).toString() + " + $coins Coins"
            )
        }*/
        Text(
            text = "$coins"
        )

    }



}
@Composable
fun NutritionDetails1(healthMetrics: HealthMetrics) {

        Column(modifier = Modifier.fillMaxWidth()) {
            // Align first text "Meal Nutrition" to the start (left)
            Text(
                text = "Meal Nutrition",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF000000),
                modifier = Modifier
                    .padding(start = 0.dp) // Add padding for better spacing
                    .align(Alignment.Start) // Align to start
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Center-aligned content
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Calories ring chart
                NutrientCards(value = healthMetrics.calorieAverage.toInt(), color = Color(0xFFFDB022))

                Spacer(modifier = Modifier.height(4.dp))

                // Nutrient breakdown (Protein, Carbs, Fats)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutrientCard(
                        label = "PROTEIN",
                        value = healthMetrics.proteinAvg.toInt(),
                        color = Color(0xFF12B76A)
                    )
                    NutrientCard(
                        label = "CARBS",
                        value = healthMetrics.carbAvg.toInt(),
                        color = Color(0xFFFD853A)
                    )
                    NutrientCard(
                        label = "FAT",
                        value = healthMetrics.fatAvg.toInt(),
                        color = Color(0xFFF97066)
                    )
                }
            }
        }

}



