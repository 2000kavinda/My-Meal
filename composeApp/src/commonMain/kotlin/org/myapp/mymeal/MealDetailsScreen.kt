package org.myapp.mymeal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HealthMetrics(
    val calorieAverage: Double,
    val carbAvg: Double,
    val proteinAvg: Double,
    val fatAvg: Double,
    val healthStatus: String
)


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
                val meals = firestoreRepository.fetchNutritionData("kavindaudara75@gmail.com")
                 coins = firestoreRepository.fetchCoinCount("kavindaudara75@gmail.com")!!
                val dayCount = firestoreRepository.fetchUniqueDateCountExcludingToday("kavindaudara75@gmail.com")
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
                email = "kavindaudara75@gmail.com",
                onDismiss = { showCardDetailsDialog = false }
            )
        }
    }
}



@Composable
fun MealImageAndDetails(meal: Meal) {
    Spacer(modifier = Modifier.height(24.dp))
    AsyncImage(
        model = meal.photo,
        contentDescription = "Meal Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = meal.name,
        fontWeight = FontWeight.Bold, // Bold text
        fontSize = 27.sp,

        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Add vertical spacing
        horizontalArrangement = Arrangement.SpaceBetween, // Position elements at opposite corners
        verticalAlignment = Alignment.CenterVertically // Align elements vertically to the center
    ) {
        // "Price" Text
        Text(
            text = "$${meal.price}",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF000000) // Black for "Price"
        )

        // Box for "Type"
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp)) // Apply rounded corners first
                .background(
                    color = if (meal.type.equals("Non-Veg", ignoreCase = true)) Color(0xFFFF0000) // Light Red
                    else Color(0xFF008000) // Light Green
                )
                .padding(horizontal = 8.dp, vertical = 4.dp) // Inner padding
        ) {
            Text(
                text = meal.type,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White // Text color for contrast
            )
        }
    }



    Spacer(modifier = Modifier.height(12.dp))
    Text(text = meal.description, style = MaterialTheme.typography.body1)
    Spacer(modifier = Modifier.height(8.dp))
   }

@Composable
fun MealAdditionalDetails(
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
        nutritionData != null && healthMetrics != null -> {
            NutritionDetails(nutritionData = nutritionData)
            // Add additional health metrics or other UI
            HealthMetricsDisplay(healthMetrics = healthMetrics, meal = meal)
        }
        else -> Text(text = "No nutrition data available.")
    }

    if (isApiLoading) {
        CircularProgressIndicator()
    } else {
        apiResponse?.let {
            AIResponseDisplay(apiResponse = it)
        }
        Button(
            onClick = {sharedViewModel.setPayAmount(meal.price)
                onShowCardDetails()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))
        ) {
            Text("Pay $"+meal.price, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        /*Button(
            onClick = {
                sharedViewModel.setPayAmount(meal.price)
                onShowCardDetails() },
            modifier = Modifier.fillMaxWidth()
        ) {

        }*/
        Button(
            onClick = {sharedViewModel.setPayAmount(meal.price - (coins ?: 0.0)) // This should be a separate statement
                onShowCardDetails() // Call the function

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))
        ) {
            Text(
                text = "Pay $" + (meal.price - (coins ?: 0.0)).toString() + " + $coins Coins", color = Color.White, fontWeight = FontWeight.Bold
            )
            //Text("Pay $"+meal.price, color = Color.White)
        }

        /*Button(
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

    }



}

@Composable
fun CardDetailsDialog(
    sharedViewModel: SharedViewModel,
    //value: Meal,
    meal: Meal,
    nutritionData: NutritionResponse?,
    email: String,
    onDismiss: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var paymentStatus by remember { mutableStateOf("") }
    val firestoreRepository = remember { FirestoreRepository() }
    val coroutineScope = rememberCoroutineScope()

    val currentDate = SimpleDateFormat("MM/yy", Locale.getDefault()).format(Date())

    // Extract nutrition details from NutritionResponse
    val nutritionItem = nutritionData?.items?.firstOrNull()
    val calories = nutritionItem?.calories ?: 0.0
    val carbohydrates = nutritionItem?.carbohydrates_total_g ?: 0.0
    val proteins = nutritionItem?.protein_g ?: 0.0
    val fats = nutritionItem?.fat_total_g ?: 0.0
    val payAmount by sharedViewModel.payAmount.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Card Details",fontWeight = FontWeight.Bold ) },

        text = {
            Column {

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (MM/YY)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    label = { Text("CVV") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    ),
                )
                if (paymentStatus.isNotEmpty()) {
                    Text(
                        text = paymentStatus,
                        color = if (paymentStatus.contains("Success")) MaterialTheme.colors.primary else MaterialTheme.colors.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                coroutineScope.launch {
                    val cardDetails = firestoreRepository.fetchCardDetails(cardNumber)
                    if (cardDetails != null) {
                        // Validate card details and process payment
                        if (cardDetails.card == cardNumber && cardDetails.cvv == cvv) {
                            val expiry = SimpleDateFormat("MM/yy", Locale.getDefault()).parse(expiryDate)
                            val current = SimpleDateFormat("MM/yy", Locale.getDefault()).parse(currentDate)
                            if (expiry != null && expiry >= current) {
                                if (cardDetails.balance >= meal.price) {
                                    val newBalance = cardDetails.balance - payAmount!!
                                    val paymentSuccessful = firestoreRepository.deductBalance(cardNumber, newBalance)
                                    if (paymentSuccessful) {
                                        paymentStatus = "Payment Successful"

                                        // Save the order details to Firestore
                                        firestoreRepository.saveOrder(
                                            Order(
                                                name = meal.name,
                                                calories = calories,
                                                carbohydrates = carbohydrates,
                                                proteins = proteins,
                                                fats = fats,
                                                price = meal.price,
                                                photo = meal.photo,
                                                email = email,
                                                description = meal.description,
                                                type = meal.type
                                            )
                                        )
                                    } else {
                                        paymentStatus = "Payment Failed"
                                    }
                                } else {
                                    paymentStatus = "Insufficient Balance"
                                }
                            } else {
                                paymentStatus = "Card Expired"
                            }
                        } else {
                            paymentStatus = "Invalid Card Details"
                        }
                    } else {
                        paymentStatus = "Card Not Found"
                    }
                }
            },colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF002945), // Replace with your desired color
                contentColor = Color.White // Text color
            )) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF002945), // Replace with your desired color
                    contentColor = Color.White // Text color
                )
            ) {
                Text("Cancel")
            }
        }

    )
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



suspend fun callOpenAIAPI(httpClient: HttpClient, healthStatus: String): String {
    val apiKey = "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" // Replace with your API key
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
@Composable
fun NutritionDetails(nutritionData: NutritionResponse) {
    val nutritionItem = nutritionData.items.firstOrNull()
    nutritionItem?.let {
        val totalCalories = it.calories
        val proteinCalories = it.protein_g   // Protein has 4 calories per gram
        val carbsCalories = it.carbohydrates_total_g   // Carbs have 4 calories per gram
        val fatCalories = it.fat_total_g   // Fats have 9 calories per gram

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
                NutrientCards(value = totalCalories.toInt(), color = Color(0xFFFDB022))

                Spacer(modifier = Modifier.height(4.dp))

                // Nutrient breakdown (Protein, Carbs, Fats)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutrientCard(
                        label = "PROTEIN",
                        value = proteinCalories.toInt(),
                        color = Color(0xFF12B76A)
                    )
                    NutrientCard(
                        label = "CARBS",
                        value = carbsCalories.toInt(),
                        color = Color(0xFFFD853A)
                    )
                    NutrientCard(
                        label = "FAT",
                        value = fatCalories.toInt(),
                        color = Color(0xFFF97066)
                    )
                }
            }
        }
    } ?: Text(text = "Nutrition data is not available.")
}

@Composable
fun CircularProgressBars(
    progress: Float,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(150.dp)
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier
                .size(150.dp)
                .border(8.dp, Color.Red, shape = CircleShape),
            strokeWidth = 8.dp,
            color = when {
                progress < 0.5f -> Color.Green
                progress < 0.8f -> Color.Yellow
                else -> Color.Red
            }
        )
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black
        )
    }
}

@Composable
fun NutrientCard(label: String, value: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(8.dp)
            .width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .border(
                    width = 4.dp, // Border thickness
                    color = color, // Border color
                    shape = CircleShape
                )
                .background(Color.White)
        ) {
            Text(
                text = "$value g",
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.body2,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NutrientCards(value: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(8.dp)
            .width(100.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(
                    width = 4.dp, // Border thickness
                    color = color, // Border color
                    shape = CircleShape
                )
                .background(Color.White)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$value",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "CALORIES",
                    color = Color.Black,
                    fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                )
            }
        }
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