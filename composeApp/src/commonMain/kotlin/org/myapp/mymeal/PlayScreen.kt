package org.myapp.mymeal
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.myapp.mymeal.NavigationProvider.navigationManager

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayScreen(
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
            AsyncImage(
                model = "https://firebasestorage.googleapis.com/v0/b/care-cost.appspot.com/o/meal%20photos%2FUntitled_design__1_-removebg.png?alt=media&token=0dacbe0d-7fa8-407a-86ab-020832fb83b8",
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
            //Spacer(modifier = Modifier.height(32.dp))


            Spacer(modifier = Modifier.height(35.dp)) // Add spacing between image and button

            Button(
                onClick = {
                    navigationManager.navigateTo(Screen.GameScreen)
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))
            ) {

                    Text("Play & Earn", color = Color.White)

            }

        }

        // Bottom Navigation Bar
        BottomNavigation(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter), // Anchors the bar at the bottom
            backgroundColor = Color(0xFF002945),
            elevation = 8.dp // Add elevation for better visibility
        ) {
            BottomNavigationItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Home, contentDescription = "Home", tint = Color.White)
                        Text("Home", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selected = selectedItem.value == 0,
                onClick = { selectedItem.value = 0 }
            )
            BottomNavigationItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                        Text("Play", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selected = selectedItem.value == 1,
                onClick = { selectedItem.value = 1 }
            )
            BottomNavigationItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Menu, contentDescription = "History", tint = Color.White)
                        Text("History", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selected = selectedItem.value == 2,
                onClick = { selectedItem.value = 2 }
            )
            BottomNavigationItem(
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = Color.White)
                        Text("Profile", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selected = selectedItem.value == 3,
                onClick = { /* Navigate to Profile */ }
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








