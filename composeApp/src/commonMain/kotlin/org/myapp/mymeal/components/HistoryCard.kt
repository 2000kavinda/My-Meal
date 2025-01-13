package org.myapp.mymeal.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.myapp.mymeal.model.Meal
import org.myapp.mymeal.ui.theme.ColorThemes

@Composable
fun HistoryCard(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .width(600.dp)
            .padding(8.dp)
            .clickable { onMealClick(meal) },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = meal.photo,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .size(100.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = meal.name, fontWeight = FontWeight.Bold)
                Text(text = meal.type)
                Text(text = "$${meal.price}", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { onMealClick(meal) },
                colors = ButtonDefaults.buttonColors(backgroundColor = ColorThemes.PrimaryButtonColor),
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
            ) {
                Text(text = "Reorder", color = Color.White)
            }

        }
    }
}