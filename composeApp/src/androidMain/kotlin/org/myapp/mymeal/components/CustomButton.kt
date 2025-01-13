package org.myapp.mymeal.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
//import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import androidx.compose.ui.res.painterResource
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.painterResource
import org.myapp.mymeal.NavigationProvider.navigationManager
import org.myapp.mymeal.components.CustomButton
import java.io.ByteArrayOutputStream
import java.io.InputStream

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
