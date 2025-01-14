package org.myapp.mymeal.view.buyMeal

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.controller.NutritionResponse
import org.myapp.mymeal.model.Order
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.components.CustomOutlinedTextField
import org.myapp.mymeal.controller.BuyMealController
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.controller.GameController
import org.myapp.mymeal.ui.theme.ColorThemes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CardDetailsDialog(
    sharedViewModel: SharedViewModel,
    //value: Meal,
    meal: Meal,
    nutritionData: NutritionResponse?,
    email: String,
    onDismiss: () -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var paymentStatus by remember { mutableStateOf("") }
    val firestoreRepository = remember { HistoryController() }
    val coroutineScope = rememberCoroutineScope()

    val currentDate = SimpleDateFormat("MM/yy", Locale.getDefault()).format(Date())

    val nutritionItem = nutritionData?.items?.firstOrNull()
    val calories = nutritionItem?.calories ?: 0.0
    val carbohydrates = nutritionItem?.carbohydrates_total_g ?: 0.0
    val proteins = nutritionItem?.protein_g ?: 0.0
    val fats = nutritionItem?.fat_total_g ?: 0.0
    val payAmount by sharedViewModel.payAmount.collectAsState()
    val deductCoin by sharedViewModel.coinAmount.collectAsState()
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    val buyMealController =BuyMealController()
    val gameController=GameController()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Card Details",fontWeight = FontWeight.Bold ) },

        text = {
            Column {
                CustomOutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number") },
                )

                Spacer(modifier = Modifier.height(8.dp))
                CustomOutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (MM/YY)") },
                )

                Spacer(modifier = Modifier.height(8.dp))
                CustomOutlinedTextField(
                    value = cvv,
                    onValueChange = { cvv = it },
                    label = { Text("CVV") },
                )

                if (paymentStatus.isNotEmpty()) {
                    Text(
                        text = paymentStatus,
                        color = if (paymentStatus.contains("Success")) MaterialTheme.colors.primary else MaterialTheme.colors.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                coroutineScope.launch {

                    val cardDetails = buyMealController.fetchCardDetails(cardNumber)
                    if (cardDetails != null) {

                        if (cardDetails.card == cardNumber && cardDetails.cvv == cvv) {
                            val expiry = SimpleDateFormat("MM/yy", Locale.getDefault()).parse(expiryDate)
                            val current = SimpleDateFormat("MM/yy", Locale.getDefault()).parse(currentDate)
                            if (expiry != null && expiry >= current) {
                                if (cardDetails.balance >= meal.price) {
                                    val newBalance = cardDetails.balance - payAmount!!
                                    val paymentSuccessful = buyMealController.deductBalance(cardNumber, newBalance)
                                    if (paymentSuccessful) {
                                        paymentStatus = "Payment Successful"
                                        sharedViewModel.setCoinAmount(meal.price)
                                        gameController.reduceCoinAmountByEmail(currentUserEmail?:"",deductCoin?:0.0)

                                        buyMealController.saveOrder(
                                            Order(
                                                name = meal.name,
                                                calories = calories,
                                                carbohydrates = carbohydrates,
                                                proteins = proteins,
                                                fats = fats,
                                                price = meal.price,
                                                photo = meal.photo,
                                                email = email,
                                                description = meal.description,
                                                type = meal.type
                                            )
                                        )
                                        navigationManager.navigateTo(Screen.MealList)
                                    } else {
                                        paymentStatus = "Payment Failed"
                                    }
                                } else {
                                    paymentStatus = "Insufficient Balance"
                                }
                            } else {
                                paymentStatus = "Card Expired"
                            }
                        } else {
                            paymentStatus = "Invalid Card Details"
                        }
                    } else {
                        paymentStatus = "Card Not Found"
                    }
                }
            },colors = ButtonDefaults.buttonColors(
                backgroundColor = ColorThemes.PrimaryButtonColor,
                contentColor = Color.White
            )) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ColorThemes.PrimaryButtonColor,
                    contentColor = Color.White
                )
            ) {
                Text("Cancel")
            }
        }

    )
}

