package org.myapp.mymeal.components


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.myapp.mymeal.ui.theme.PrimaryButtonColor
import org.myapp.mymeal.ui.theme.PrimaryTextColor

@Composable
fun LoadingButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    buttonText: String,
    backgroundColor: Color = PrimaryButtonColor,
    textColor: Color = PrimaryTextColor
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = textColor
            )
        } else {
            Text(buttonText, color = textColor)
        }
    }
}
