package org.myapp.mymeal.view.HomeAndBuyMeal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
import coil3.compose.AsyncImage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.myapp.mymeal.Meal
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.model.HealthMetrics
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MealImageAndDetails(meal: Meal) {
    Spacer(modifier = Modifier.height(24.dp))
    AsyncImage(
        model = meal.photo,
        contentDescription = "Meal Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
    Spacer(modifier = Modifier.height(20.dp))
    Text(
        text = meal.name,
        fontWeight = FontWeight.Bold, // Bold text
        fontSize = 27.sp,

        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Add vertical spacing
        horizontalArrangement = Arrangement.SpaceBetween, // Position elements at opposite corners
        verticalAlignment = Alignment.CenterVertically // Align elements vertically to the center
    ) {
        // "Price" Text
        Text(
            text = "$${meal.price}",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF000000) // Black for "Price"
        )

        // Box for "Type"
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp)) // Apply rounded corners first
                .background(
                    color = if (meal.type.equals("Non-Veg", ignoreCase = true)) Color(0xFFFF0000) // Light Red
                    else Color(0xFF008000) // Light Green
                )
                .padding(horizontal = 8.dp, vertical = 4.dp) // Inner padding
        ) {
            Text(
                text = meal.type,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White // Text color for contrast
            )
        }
    }



    Spacer(modifier = Modifier.height(12.dp))
    Text(text = meal.description, style = MaterialTheme.typography.body1)
    Spacer(modifier = Modifier.height(8.dp))
   }