package org.myapp.mymeal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val totalCalories: Double,
    val carbPercentage: Double,
    val proteinPercentage: Double,
    val fatPercentage: Double,
    val healthStatus: String
)

@Composable
fun MealDetailsScreen(meal: Meal,sharedViewModel: SharedViewModel, onBack: () -> Unit) {
    val nutritionRepository = remember { NutritionRepository() }
    val firestoreRepository = remember { FirestoreRepository() }
    val httpClient = remember { HttpClient() }

    var nutritionData by remember { mutableStateOf<NutritionResponse?>(null) }
    var healthMetrics by remember { mutableStateOf<HealthMetrics?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var apiResponse by remember { mutableStateOf<String?>(null) }
    var isApiLoading by remember { mutableStateOf(false) } // Track OpenAI API loading state
    var showCardDetailsDialog by remember { mutableStateOf(false) } // State to control the dialog visibility

    val coroutineScope = rememberCoroutineScope()
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                isLoading = true
                // Fetch nutrition data for the selected meal
                nutritionData = nutritionRepository.getNutritionData(meal.name)

                // Fetch all orders for the given email
                val meals = firestoreRepository.fetchNutritionData("kavindaudara75@gmail.com")

                // Calculate the health metrics based on the fetched meals and nutrition data
                healthMetrics = calculateHealthMetrics(meals, nutritionData)

            } catch (e: Exception) {
                errorMessage = "Error fetching data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Trigger OpenAI API call only after health status is calculated
    LaunchedEffect(healthMetrics) {
        if (healthMetrics != null && healthMetrics!!.healthStatus.isNotEmpty()) {
            // Set loading to true while calling the OpenAI API
            isApiLoading = true
            coroutineScope.launch {
                apiResponse = callOpenAIAPI(httpClient, healthMetrics!!.healthStatus)
                isApiLoading = false // Set loading to false after the API call is finished
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Meal Image
            AsyncImage(
                model = meal.photo,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // Meal Name and Price
            Text(text = "Meal: ${meal.name}", style = MaterialTheme.typography.h5)
            Text(text = "Price: $${meal.price}", style = MaterialTheme.typography.body1)

            // Loading, Error, or Data Display
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage.isNotEmpty() -> Text(text = errorMessage, color = MaterialTheme.colors.error)
                nutritionData != null && healthMetrics != null -> {
                    NutritionDetails(nutritionData = nutritionData!!)
                    HealthMetricsDisplay(healthMetrics = healthMetrics!!)
                }
                else -> Text(text = "No nutrition data available.")
            }

            // Display the AI recommendation
            if (isApiLoading) {
                // Show a separate loading spinner for OpenAI API call
                CircularProgressIndicator()
            } else {
                apiResponse?.let {
                    AIResponseDisplay(apiResponse = it)
                }
            }

            // Add a Button to show the Card Details Dialog
            Button(
                onClick = { showCardDetailsDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enter Card Details")
            }

            if (showCardDetailsDialog) {
                CardDetailsDialog(
                    meal = meal,
                    nutritionData = nutritionData,
                    email = "kavindaudara75@gmail.com",
                    onDismiss = { showCardDetailsDialog = false }
                )
            }




            // Show the Dialog when showCardDetailsDialog is true
            if (showCardDetailsDialog) {
                CardDetailsDialog(
                    meal = meal,
                    nutritionData = nutritionData,
                    email = "kavindaudara75@gmail.com",
                    onDismiss = { showCardDetailsDialog = false }
                )
            }

        }
    }
}
@Composable
fun CardDetailsDialog(
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Card Details") },
        text = {
            Column {
                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (MM/YY)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    label = { Text("CVV") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                                    val newBalance = cardDetails.balance - meal.price
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
                                                email = email
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
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
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
    val apiKey = "sk-proj-gMJ2i6pc81rlfAZD9rnKT3BlbkFJic8JE5LUhTfmF45JMhi5" // Replace with your API key
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


fun calculateHealthMetrics(meals: List<Order>, nutritionData: NutritionResponse?): HealthMetrics {
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

    val carbPercentage = (totalCarbs * 4 / totalCalories) * 100
    val proteinPercentage = (totalProteins * 4 / totalCalories) * 100
    val fatPercentage = (totalFats * 9 / totalCalories) * 100

    val healthStatus = when {
        totalCalories > 2000.0 -> "Unhealthy: High Calorie Intake"
        carbPercentage > 50.0 -> "Unhealthy: High Carbohydrate Intake"
        proteinPercentage < 30.0 -> "Unhealthy: Low Protein Intake"
        fatPercentage > 20.0 -> "Unhealthy: High Fat Intake"
        else -> "Healthy"
    }

    return HealthMetrics(
        totalCalories = totalCalories,
        carbPercentage = carbPercentage,
        proteinPercentage = proteinPercentage,
        fatPercentage = fatPercentage,
        healthStatus = healthStatus
    )
}

@Composable
fun HealthMetricsDisplay(healthMetrics: HealthMetrics) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = "Health Metrics", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Total Calories: ${healthMetrics.totalCalories}")
        Text(text = "Carbohydrate Percentage: ${healthMetrics.carbPercentage}%")
        Text(text = "Protein Percentage: ${healthMetrics.proteinPercentage}%")
        Text(text = "Fat Percentage: ${healthMetrics.fatPercentage}%")
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Health Status: ${healthMetrics.healthStatus}",
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
        Column(horizontalAlignment = Alignment.Start) {
            Text(text = "Nutrition Information", style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Calories: ${it.calories}")
            Text(text = "Carbohydrates: ${it.carbohydrates_total_g} g")
            Text(text = "Protein: ${it.protein_g} g")
            Text(text = "Fats: ${it.fat_total_g} g")
        }
    } ?: Text(text = "Nutrition data is not available.")
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