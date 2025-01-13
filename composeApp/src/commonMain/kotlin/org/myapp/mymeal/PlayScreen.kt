package org.myapp.mymeal
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.ktor.client.HttpClient
import org.myapp.mymeal.NavigationProvider.navigationManager
import org.myapp.mymeal.components.BottomNavigationBar
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.model.HealthMetrics

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
    var meal = Meal(
        name = "Default Meal",
        photo = "https://example.com/default-photo.jpg",
        price = 0.0,
        description = "Default description",
        type = "Default type"
    )

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            meals = repository.getOrders("kavindaudara75@gmail.com")
            filteredMeals = meals
            errorMessage = if (meals.isEmpty()) "No meals found" else ""

            val nutrition = firestoreRepository.fetchNutritionData("kavindaudara75@gmail.com")
            coins = firestoreRepository.fetchCoinCount("kavindaudara75@gmail.com") ?: 0.0
            val dayCount = firestoreRepository.fetchUniqueDateCountExcludingToday("kavindaudara75@gmail.com")
            //healthMetrics = calculateHealthMetrics(nutrition, nutritionData, dayCount, "Male", "Moderate", "Maintain Weight")
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








