package org.myapp.mymeal.view.buyMeal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.ui.theme.ColorThemes

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
        fontWeight = FontWeight.Bold,
        fontSize = 27.sp,

        )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$${meal.price}",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ColorThemes.PrimaryBlackColor
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    color = if (meal.type.equals("Non-Veg", ignoreCase = true)) ColorThemes.PrimaryRedColor
                    else ColorThemes.PrimaryGreenColor
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = meal.type,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }



    Spacer(modifier = Modifier.height(12.dp))
    Text(text = meal.description, style = MaterialTheme.typography.body1)
    Spacer(modifier = Modifier.height(8.dp))
   }