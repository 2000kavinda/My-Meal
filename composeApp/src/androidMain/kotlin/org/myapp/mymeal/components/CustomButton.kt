package org.myapp.mymeal.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
//import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import androidx.compose.ui.res.painterResource
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF002945),
    textColor: Color = Color.White,
    borderColor: Color? = null, // Optional border color
    icon: Int? = null, // Optional icon resource ID
    iconTint: Color = Color.White,
    textSize: TextUnit = 18.sp
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(250.dp) // Default width (can override with modifier)
            .height(70.dp) // Default height
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(12.dp)),
        border = borderColor?.let { BorderStroke(2.dp, it) } // Add border if color is provided
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Show icon if provided
            icon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "Icon",
                    tint = iconTint,
                    modifier = Modifier.size(30.dp)
                )
            }
            // Button text
            Text(
                text = text,
                color = textColor,
                fontSize = textSize
            )
        }
    }
}
