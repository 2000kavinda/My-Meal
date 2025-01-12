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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
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

                bottomBar = {
            BottomNavigation(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                .background(color = Color(0xFF002945), shape = RoundedCornerShape(16.dp)), // Set background color and corner radius
                backgroundColor = Color(0xFF002945)
            ) {
                BottomNavigationItem(
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.White)
                            Text("Home", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    //icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.White) }, // Set icon color to black
                    selected = selectedItem.value == 0,
                    onClick = {navigationManager.navigateTo(Screen.MealList) }
                )
                BottomNavigationItem(
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                            Text("Play", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    //icon = { Icon(Icons.Filled.PlayArrow, contentDescription = "Search", tint = Color.White) }, // Set icon color to black
                    selected = selectedItem.value == 1,
                    onClick = { navigationManager.navigateTo(Screen.PlayScreen) }
                )
                BottomNavigationItem(
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Menu, contentDescription = "History", tint = Color.White)
                            Text("History", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    // icon = { Icon(Icons.Filled.Favorite, contentDescription = "Notifications", tint = Color.White) }, // Set icon color to black
                    selected = selectedItem.value == 2,
                    onClick = { navigationManager.navigateTo(Screen.History) }
                )
                BottomNavigationItem(
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = Color.White)
                            Text("Profile", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    },
                    // icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = Color.White) }, // Set icon color to black
                    selected = selectedItem.value == 3,
                    onClick = {
                        navigationManager.navigateTo(Screen.ProfileScreen(meal = meal))
                    },
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

                MealAdditionalDetails1(
                    sharedViewModel, coins,
                    meal, isLoading, errorMessage, nutritionData, healthMetrics, isApiLoading, apiResponse
                ) { showCardDetailsDialog = true }
                Spacer(modifier = Modifier.height(40.dp))
                ProfileDetails(meal, sharedViewModel)
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
    ) {Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(150.dp) // Set the size of the container
                .clip(CircleShape) // Make the container rounded
                .background(Color(0xFF002945)), // Set background color
            contentAlignment = Alignment.Center // Center the text inside the Box
        ) {
            Text(
                text = initials, // Set the initials
                color = Color.White, // Text color
                style = MaterialTheme.typography.h2, // Text style
                fontWeight = FontWeight.Bold // Make the text bold
            )
        }
    }

    Spacer(modifier = Modifier.height(40.dp))

    // Other Rows for displaying user info
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Email,
            contentDescription = "User Email Icon",
            modifier = Modifier.size(30.dp),
            tint = Color(0xFF002945)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currentUserEmail ?: "No email available",
            //fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Person,
            contentDescription = "User Gender Icon",
            modifier = Modifier.size(30.dp),
            tint = Color(0xFF002945)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currentUserGender ?: "No gender available",
            //fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = "User Goal Icon",
            modifier = Modifier.size(30.dp),
            tint = Color(0xFF002945)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currentUserGoal ?: "No goal available",
            //fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.DateRange,
            contentDescription = "User Activity Level Icon",
            modifier = Modifier.size(30.dp),
            tint = Color(0xFF002945)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currentUserActivityLevel ?: "No activity level available",
            //fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Button(
        onClick = {

        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))

    ) {
        Text(
            text = "Log out", color = Color.White,fontWeight = FontWeight.Bold,
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
            HealthMetricsDisplay1(healthMetrics = healthMetrics, meal = meal)
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
        Spacer(modifier = Modifier.height(60.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            elevation = 4.dp,
            backgroundColor = Color(0xFFCBD5ED),
            shape = RoundedCornerShape(12.dp) // Rounded corners
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Coins Balance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF000000),

                )
                //Spacer(modifier = Modifier.height(8.dp))
                // 1st Column: Text "Coins"
                /*Text(
                    text = "Coins",
                    style = MaterialTheme.typography.body1,
                    color = Color.White // Text color for contrast
                )*/

                // 2nd Column: Coin Count
                Text(
                    text = coins?.toString() ?: "0",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF000000),

                    )

            }}

    }



}


@Composable
fun HealthMetricsDisplay1(healthMetrics: HealthMetrics,meal: Meal) {
    Column(horizontalAlignment = Alignment.Start) {
        //Text(text = "Health Metrics", style = MaterialTheme.typography.h6)
        //Spacer(modifier = Modifier.height(8.dp))
        //Text(text = "Total Calories: ${"%.2f".format(healthMetrics.calorieAverage)}")
        //Text(text = "Carbohydrate Percentage: ${"%.2f".format(healthMetrics.carbAvg)}g")
        //Text(text = "Protein Percentage: ${"%.2f".format(healthMetrics.proteinAvg)}g")
        //Text(text = "Fat Percentage: ${"%.2f".format(healthMetrics.fatAvg)}g")
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${healthMetrics.healthStatus}",
            style = MaterialTheme.typography.body1.copy(
                fontWeight = FontWeight.Bold,
                color = if (healthMetrics.healthStatus.contains("Healthy"))
                    MaterialTheme.colors.primary else MaterialTheme.colors.error
            )
        )
    }
}
@Composable
fun NutritionDetails1(healthMetrics: HealthMetrics) {

        Column(modifier = Modifier.fillMaxWidth()) {
            // Align first text "Meal Nutrition" to the start (left)
            Text(
                text = "User Body Average Nutrition Level",
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



