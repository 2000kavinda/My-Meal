package org.myapp.mymeal.view.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.myapp.mymeal.model.HealthMetrics
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.ui.theme.ColorThemes
import org.myapp.mymeal.ui.theme.FontSizes

@Composable
fun MealOtherDetails(
    coins: Double?,
    meal: Meal,
    isLoading: Boolean,
    errorMessage: String,
    healthMetrics: HealthMetrics?,
    isApiLoading: Boolean,
    ) {
    when {
        isLoading -> CircularProgressIndicator(color = ColorThemes.PrimaryButtonColor)
        errorMessage.isNotEmpty() -> Text(text = errorMessage, color = MaterialTheme.colors.error)
         healthMetrics != null -> {
            NutritionDetails1(healthMetrics = healthMetrics)
            HealthMetricsDisplay1(healthMetrics = healthMetrics, meal = meal)
        }
        else -> Text(text = "No nutrition data available.")
    }

    if (isApiLoading) {
        CircularProgressIndicator(color =ColorThemes.PrimaryButtonColor)
    } else {
        Spacer(modifier = Modifier.height(60.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp),
            elevation = 4.dp,
            backgroundColor = ColorThemes.PrimaryBgColor,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Coins Balance",
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSizes.font22,
                    color = ColorThemes.PrimaryBlackColor,

                )

                Text(
                    text = coins?.toString() ?: "0",
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSizes.font22,
                    color = ColorThemes.PrimaryBlackColor,

                    )

            }
        }

    }

}