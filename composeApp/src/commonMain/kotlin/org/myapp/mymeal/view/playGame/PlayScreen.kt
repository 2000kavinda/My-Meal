package org.myapp.mymeal.view.playGame
import androidx.compose.foundation.background
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
import org.myapp.mymeal.controller.NutritionResponse
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.components.BottomNavigationBar
import org.myapp.mymeal.controller.BuyMealController
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.controller.GameController
import org.myapp.mymeal.model.HealthMetrics
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.ui.theme.ColorThemes
import org.myapp.mymeal.utils.Constants
import org.myapp.mymeal.view.buyMeal.calculateHealthMetrics

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayScreen(
    repository: HistoryController,
    onMealClick: (Meal) -> Unit,
    sharedViewModel: SharedViewModel,
) {
    val firestoreRepository = remember { HistoryController() }
    var showPopup by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var nutritionData by remember { mutableStateOf<NutritionResponse?>(null) }
    var healthMetrics by remember { mutableStateOf<HealthMetrics?>(null) }
    var coins by remember { mutableStateOf(0.0) }
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    val currentUserGender by sharedViewModel.currentUserGender.collectAsState()
    val currentUserGoal by sharedViewModel.currentUserGoal.collectAsState()
    val currentUserActivityLevel by sharedViewModel.currentUserActivityLevel.collectAsState()
    val selectedItem = remember { mutableStateOf(0) }
    var meal = Meal(
        name = "Default Meal",
        photo = "",
        price = 0.0,
        description = "Default description",
        type = "Default type"
    )
    val buyMealController = BuyMealController()
    val gameController= GameController()

    LaunchedEffect(Unit) {
        try {
            isLoading = true

            val meals = buyMealController.fetchNutritionData(currentUserEmail?:"")
            coins = gameController.fetchCoinCount(currentUserEmail?:"")!!
            val dayCount = buyMealController.fetchUniqueDateCountExcludingToday(currentUserEmail?:"")
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
            .background(ColorThemes.PrimaryTextColor)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(end = 16.dp,
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 56.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = Constants.logoUrl,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )

            Spacer(modifier = Modifier.height(35.dp))

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







