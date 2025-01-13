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
import org.myapp.mymeal.components.NutriCard
import org.myapp.mymeal.components.NutrientCard
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.currentPlatform
import org.myapp.mymeal.model.HealthMetrics


@Composable
fun NutritionDetails(nutritionData: NutritionResponse) {
    val nutritionItem = nutritionData.items.firstOrNull()
    nutritionItem?.let {
        val totalCalories = it.calories
        val proteinCalories = it.protein_g   // Protein has 4 calories per gram
        val carbsCalories = it.carbohydrates_total_g   // Carbs have 4 calories per gram
        val fatCalories = it.fat_total_g   // Fats have 9 calories per gram

        Column(modifier = Modifier.fillMaxWidth()) {
            // Align first text "Meal Nutrition" to the start (left)
            Text(
                text = "Meal Nutrition",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF000000),
                modifier = Modifier
                    .padding(start = 0.dp) // Add padding for better spacing
                    .align(Alignment.Start) // Align to start
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Center-aligned content
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Calories ring chart
                NutriCard(value = totalCalories.toInt(), color = Color(0xFFFDB022))

                Spacer(modifier = Modifier.height(4.dp))

                // Nutrient breakdown (Protein, Carbs, Fats)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutrientCard(
                        label = "PROTEIN",
                        value = proteinCalories.toInt(),
                        color = Color(0xFF12B76A)
                    )
                    NutrientCard(
                        label = "CARBS",
                        value = carbsCalories.toInt(),
                        color = Color(0xFFFD853A)
                    )
                    NutrientCard(
                        label = "FAT",
                        value = fatCalories.toInt(),
                        color = Color(0xFFF97066)
                    )
                }
            }
        }
    } ?: Text(text = "Nutrition data is not available.")
}