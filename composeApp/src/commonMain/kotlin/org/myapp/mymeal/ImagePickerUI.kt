package org.myapp.mymeal
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement // Correct import for Arrangement
@Composable
fun ImagePickerUI(onPickImage: () -> Unit, imageBitmap: ImageBitmap?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Ensure Arrangement is imported
    ) {
        Button(onClick = onPickImage) {
            Text("Pick an Image")
        }
        Spacer(modifier = Modifier.height(16.dp))
        imageBitmap?.let {
            Image(bitmap = it, contentDescription = null, modifier = Modifier.size(200.dp))
        }
    }
}

