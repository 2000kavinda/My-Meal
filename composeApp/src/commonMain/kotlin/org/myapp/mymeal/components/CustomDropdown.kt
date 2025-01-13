package org.myapp.mymeal.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String,
    isLoading: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)) // Add border with rounded corners
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp) // Match the border shape
                )
        ) {
            TextField(
                value = selectedOption,
                onValueChange = { /* Do nothing, read-only */ },
                label = { Text(label) },
                enabled = !isLoading,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown Icon"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent, // Transparent to avoid overlapping
                    focusedIndicatorColor = Color.Transparent, // Remove focused underline
                    unfocusedIndicatorColor = Color.Transparent, // Remove unfocused underline
                    disabledIndicatorColor = Color.Transparent // Remove disabled underline
                )
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = option)
                }
            }
        }
    }
}
