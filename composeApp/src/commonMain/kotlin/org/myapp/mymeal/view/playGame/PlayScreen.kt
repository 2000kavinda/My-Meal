package org.myapp.mymeal.view.playGame
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.ktor.client.HttpClient
import org.myapp.mymeal.controller.NutritionRepository
import org.myapp.mymeal.controller.NutritionResponse
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.components.BottomNavigationBar
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.model.HealthMetrics
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.ui.theme.ColorThemes
import org.myapp.mymeal.view.buyMeal.calculateHealthMetrics

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayScreen(
    repository: FirestoreRepository,
    onMealClick: (Meal) -> Unit,
    sharedViewModel: SharedViewModel,
) {
    val nutritionRepository = remember { NutritionRepository() }
    val firestoreRepository = remember { FirestoreRepository() }
    val httpClient = remember { HttpClient() }
    var showPopup by remember { mutableStateOf(false) }

    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var filteredMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var nutritionData by remember { mutableStateOf<NutritionResponse?>(null) }
    var healthMetrics by remember { mutableStateOf<HealthMetrics?>(null) }
    var coins by remember { mutableStateOf(0.0) }
    var aiRecommendations by remember { mutableStateOf("") }
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    val currentUserGender by sharedViewModel.currentUserGender.collectAsState()
    val currentUserGoal by sharedViewModel.currentUserGoal.collectAsState()
    val currentUserActivityLevel by sharedViewModel.currentUserActivityLevel.collectAsState()
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
                    if (healthMetrics?.healthStatus?.contains("Healthy") == true) {
                        navigationManager.navigateTo(Screen.GameScreen)
                    }
                    else{showPopup = true}
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
    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text("Be Healthy to Play") },
            text = { Text("Maintain healthy status to play the game and earn coins.") },
            confirmButton = {
                TextButton(onClick = { showPopup = false }) {
                    Text("OK", color = ColorThemes.PrimaryButtonColor)
                }
            }
        )
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








