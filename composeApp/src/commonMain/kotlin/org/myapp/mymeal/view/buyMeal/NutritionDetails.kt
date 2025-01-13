package org.myapp.mymeal.view.buyMeal

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
import org.myapp.mymeal.controller.NutritionResponse
import org.myapp.mymeal.components.NutriCard
import org.myapp.mymeal.components.NutrientCard


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