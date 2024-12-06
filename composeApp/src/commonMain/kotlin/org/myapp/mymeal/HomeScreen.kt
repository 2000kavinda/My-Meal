package org.myapp.mymeal

import androidx.compose.runtime.Composable
import androidx.compose.material.*

@Composable
fun HomeScreen(onNavigateToDetails: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Home") }) },
        content = {
            Button(onClick = onNavigateToDetails) {
                Text("Go to Details")
            }
        }
    )
}

@Composable
fun DetailsScreen(onNavigateToProfile: (String) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Details") }) },
        content = {
            Button(onClick = { onNavigateToProfile("123") }) {
                Text("Go to Profile")
            }
        }
    )
}

@Composable
fun ProfileScreen(userId: String) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) },
        content = {
            Text("User ID: $userId")
        }
    )
}
