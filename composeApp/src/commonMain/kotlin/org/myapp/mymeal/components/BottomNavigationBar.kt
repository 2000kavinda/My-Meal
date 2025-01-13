package org.myapp.mymeal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
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
import org.myapp.mymeal.NavigationProvider.navigationManager
import org.myapp.mymeal.Screen
import org.myapp.mymeal.controller.FirestoreRepository


@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    onProfileClick: () -> Unit
) {
    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            //.align(Alignment.BottomCenter), // Anchors the bar at the bottom
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
            selected = selectedItem == 0,
            onClick = { navigationManager.navigateTo(Screen.MealList) }
        )
        BottomNavigationItem(
            icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.White)
                    Text("Play", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            selected = selectedItem == 1,
            onClick = { navigationManager.navigateTo(Screen.PlayScreen) }
        )
        BottomNavigationItem(
            icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Menu, contentDescription = "History", tint = Color.White)
                    Text("History", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            selected = selectedItem == 2,
            onClick = { navigationManager.navigateTo(Screen.History) }
        )
        BottomNavigationItem(
            icon = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Profile", tint = Color.White)
                    Text("Profile", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            selected = selectedItem == 3,
            onClick = { onProfileClick() }
        )
    }
}
