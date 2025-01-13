package org.myapp.mymeal.components

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
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.myapp.mymeal.Meal
import org.myapp.mymeal.NutritionRepository
import org.myapp.mymeal.NutritionResponse
import org.myapp.mymeal.Order
import org.myapp.mymeal.SharedViewModel
import org.myapp.mymeal.components.NutrientCard
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.currentPlatform
import org.myapp.mymeal.model.HealthMetrics

@Composable
fun NutriCard(value: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(8.dp)
            .width(100.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(
                    width = 4.dp, // Border thickness
                    color = color, // Border color
                    shape = CircleShape
                )
                .background(Color.White)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$value",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "CALORIES",
                    color = Color.Black,
                    fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                )
            }
        }
    }
}