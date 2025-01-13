package org.myapp.mymeal.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.myapp.mymeal.ui.theme.ColorThemes

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
            .padding(bottom = 16.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = ColorThemes.SecondaryTextColor,
            unfocusedBorderColor = ColorThemes.SecondaryTextColor,
            focusedLabelColor = ColorThemes.SecondaryTextColor,
            unfocusedLabelColor = ColorThemes.SecondaryTextColor
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
