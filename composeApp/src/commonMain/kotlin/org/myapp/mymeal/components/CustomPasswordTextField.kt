package org.myapp.mymeal.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.myapp.mymeal.components.CustomOutlinedTextField
import org.myapp.mymeal.utils.SecondaryTextColor

@Composable
fun CustomPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isPasswordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 42.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = SecondaryTextColor,
            unfocusedBorderColor = SecondaryTextColor,
            focusedLabelColor = SecondaryTextColor,
            unfocusedLabelColor = SecondaryTextColor
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.Check else Icons.Default.CheckCircle,
                    contentDescription = "Toggle password visibility"
                )
            }
        }
    )
}
