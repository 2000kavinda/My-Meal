package org.myapp.mymeal.view.HomeAndBuyMeal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.NutritionResponse
import org.myapp.mymeal.SharedViewModel
import org.myapp.mymeal.model.HealthMetrics
import org.myapp.mymeal.ui.theme.PrimaryButtonColor

@Composable
fun MealAdditionalDetails(
    sharedViewModel: SharedViewModel,
    coins: Double?,
    meal: Meal,
    isLoading: Boolean,
    errorMessage: String,
    nutritionData: NutritionResponse?,
    healthMetrics: HealthMetrics?,
    isApiLoading: Boolean,
    apiResponse: String?,
    onShowCardDetails: () -> Unit
) {
    when {
        isLoading -> CircularProgressIndicator()
        errorMessage.isNotEmpty() -> Text(text = errorMessage, color = MaterialTheme.colors.error)
        nutritionData != null && healthMetrics != null -> {
            NutritionDetails(nutritionData = nutritionData)
            // Add additional health metrics or other UI
            HealthMetricsDisplay(healthMetrics = healthMetrics, meal = meal)
        }
        else -> Text(text = "No nutrition data available.")
    }

    if (isApiLoading) {
        CircularProgressIndicator()
    } else {
        apiResponse?.let {
            AIResponseDisplay(apiResponse = it)
        }
        Button(
            onClick = {sharedViewModel.setPayAmount(meal.price)
                onShowCardDetails()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryButtonColor)
        ) {
            Text("Pay $"+meal.price, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        /*Button(
            onClick = {
                sharedViewModel.setPayAmount(meal.price)
                onShowCardDetails() },
            modifier = Modifier.fillMaxWidth()
        ) {

        }*/
        Button(
            onClick = {sharedViewModel.setPayAmount(meal.price - (coins ?: 0.0)) // This should be a separate statement
                if(coins!=null){
                    if(coins <= meal.price){
                        sharedViewModel.setCoinAmount(coins)
                    }
                    else{
                        sharedViewModel.setCoinAmount(meal.price)
                    }
                }
                else{
                    sharedViewModel.setCoinAmount(0.0)
                }
                onShowCardDetails() // Call the function

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryButtonColor)
        ) {
            Text(
                text = if (coins != null && coins <= meal.price) {
                    "Pay $" + (meal.price - coins).toString() + " + $coins Coins"
                } else {
                    "Pay $" + (0.0 ).toString() + " + ${meal.price} Coins"
                },
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            //Text("Pay $"+meal.price, color = Color.White)
        }

        /*Button(
            onClick = {
                sharedViewModel.setPayAmount(meal.price - (coins ?: 0.0)) // This should be a separate statement
                onShowCardDetails() // Call the function
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Pay $" + (meal.price - (coins ?: 0.0)).toString() + " + $coins Coins"
            )
        }*/

    }



}