package org.myapp.mymeal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UsersListScreen(repository: FirestoreRepository) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    // Fetch users when the Composable is first launched
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            users = repository.getUsers()  // Fetch users from Firestore
            errorMessage = if (users.isEmpty()) "No users found" else ""
        } catch (e: Exception) {
            errorMessage = "Error fetching users: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage)
        } else {
            // Display list of users
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(users.size) { index ->
                    val user = users[index]
                    UserCard(user = user)
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Email: ${user.email}")
            Text(text = "Password: ${user.password}")
        }
    }
}

@Composable
@Preview
fun PreviewUsersListScreen() {
    UsersListScreen(repository = FirestoreRepository())
}
