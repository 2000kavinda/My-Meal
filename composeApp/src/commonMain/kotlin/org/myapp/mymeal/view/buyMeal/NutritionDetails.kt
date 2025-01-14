package org.myapp.mymeal.view.buyMeal

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.myapp.mymeal.controller.NutritionResponse
import org.myapp.mymeal.components.NutriCard
import org.myapp.mymeal.components.NutrientCard
import org.myapp.mymeal.ui.theme.ColorThemes


@Composable
fun NutritionDetails(nutritionData: NutritionResponse) {
    val nutritionItem = nutritionData.items.firstOrNull()
    nutritionItem?.let {
        val totalCalories = it.calories
        val proteinCalories = it.protein_g
        val carbsCalories = it.carbohydrates_total_g
        val fatCalories = it.fat_total_g

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Meal Nutrition",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = ColorThemes.PrimaryBlackColor,
                modifier = Modifier
                    .padding(start = 0.dp)
                    .align(Alignment.Start) )
            Spacer(modifier = Modifier.height(8.dp))


            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                NutriCard(value = totalCalories.toInt(),  color = ColorThemes.CalorieColor)

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutrientCard(
                        label = "PROTEIN",
                        value = proteinCalories.toInt(),
                        color = ColorThemes.ProteinColor
                    )
                    NutrientCard(
                        label = "CARBS",
                        value = carbsCalories.toInt(),
                        color = ColorThemes.CarbColor
                    )
                    NutrientCard(
                        label = "FAT",
                        value = fatCalories.toInt(),
                        color = ColorThemes.FatColor
                    )
                }
            }
        }
    } ?: Text(text = "Nutrition data is not available.")
}