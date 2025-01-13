package org.myapp.mymeal.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.ui.theme.ColorThemes
import org.myapp.mymeal.ui.theme.FontSizes

@Composable
fun ProfileDetails(meal: Meal, sharedViewModel: SharedViewModel) {
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    val initials = currentUserEmail?.take(2)?.uppercase() ?: "NA"
    val currentUserActivityLevel by sharedViewModel.currentUserActivityLevel.collectAsState()
    val currentUserGender by sharedViewModel.currentUserGender.collectAsState()
    val currentUserGoal by sharedViewModel.currentUserGoal.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(ColorThemes.PrimaryButtonColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                style = MaterialTheme.typography.h2,
                fontWeight = FontWeight.Bold
            )
        }
    }

    Spacer(modifier = Modifier.height(40.dp))


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Email,
            contentDescription = "User Email Icon",
            modifier = Modifier.size(30.dp),
            tint = ColorThemes.PrimaryButtonColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currentUserEmail ?: "No email available",
            fontSize = FontSizes.font24
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Person,
            contentDescription = "User Gender Icon",
            modifier = Modifier.size(30.dp),
            tint = ColorThemes.PrimaryButtonColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currentUserGender ?: "No gender available",
            fontSize = FontSizes.font24
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = "User Goal Icon",
            modifier = Modifier.size(30.dp),
            tint = ColorThemes.PrimaryButtonColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currentUserGoal ?: "No goal available",
            fontSize = FontSizes.font24
        )
    }

    Spacer(modifier = Modifier.height(18.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Icon(
            Icons.Filled.DateRange,
            contentDescription = "User Activity Level Icon",
            modifier = Modifier.size(30.dp),
            tint = ColorThemes.PrimaryButtonColor
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = currentUserActivityLevel ?: "No activity level available",
            fontSize = FontSizes.font24
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Button(
        onClick = { showLogoutDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF002945))

    ) {
        Text(
            text = "Log out", color = Color.White,fontWeight = FontWeight.Bold,
        )
    }

    // Show the logout confirmation dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                // Add logout logic here
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }


}@Composable
fun LogoutConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Logout Confirmation",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(),
                style = MaterialTheme.typography.h6,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        text = {
            // Centering the text content within the dialog
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Are you sure you want to log out?",
                    style = MaterialTheme.typography.body1,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        buttons = {
            // Centering the buttons below the text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navigationManager.navigateTo(Screen.SignInScreen)},
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorThemes.PrimaryButtonColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Confirm")
                    }
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorThemes.PrimaryButtonColor,
                            contentColor = Color.White
                        )

                    ) {
                        Text("Cancel")
                    }
                }
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

