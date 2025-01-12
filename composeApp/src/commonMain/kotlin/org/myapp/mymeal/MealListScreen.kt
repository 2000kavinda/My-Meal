package org.myapp.mymeal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.myapp.mymeal.NavigationProvider.navigationManager

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealListScreen(
    repository: FirestoreRepository,
    onMealClick: (Meal) -> Unit,
) {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var filteredMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // State for Bottom Navigation Bar
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
            meals = repository.getMeals()
            filteredMeals = meals
            errorMessage = if (meals.isEmpty()) "No meals found" else ""
        } catch (e: Exception) {
            errorMessage = "Error fetching data: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Enable scrolling
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    filteredMeals = if (query.isEmpty()) {
                        meals
                    } else {
                        meals.filter { it.name.contains(query, ignoreCase = true) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp), // Added padding here
                placeholder = { Text(text = "Search meals...") },
                singleLine = true
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage)
            } else {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    filteredMeals.forEach { meal ->
                        MealCard(meal = meal, onMealClick = onMealClick)
                    }
                }
            }
        }

        // Bottom Navigation Bar positioned at the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp) // Added padding around the bottom navigation bar
        ) {
            BottomNavigation(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFF002945), shape = RoundedCornerShape(16.dp)), // Set background color and corner radius
                backgroundColor = Color(0xFF002945) // Make the default background color transparent
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
                    onClick = { selectedItem.value = 0 }
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
                    onClick = { selectedItem.value = 1 }
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
                    onClick = { selectedItem.value = 2 }
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
    }
}

@Composable
fun MealCard(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .width(600.dp)
            .padding(8.dp)
            .clickable { onMealClick(meal) },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = meal.photo,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${meal.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "${meal.type}")
            Text(text = "$${meal.price}",fontWeight = FontWeight.Bold,fontSize = 18.sp)
        }
    }
}
