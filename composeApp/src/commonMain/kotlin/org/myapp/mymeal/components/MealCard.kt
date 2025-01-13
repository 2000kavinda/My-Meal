package org.myapp.mymeal.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.myapp.mymeal.model.Meal

@Composable
fun MealCard(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .width(600.dp)
            .padding(8.dp)
            .clickable { onMealClick(meal) },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = meal.photo,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${meal.name}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "${meal.type}")
            Text(text = "$${meal.price}",fontWeight = FontWeight.Bold,fontSize = 18.sp)
        }
    }
}