package org.myapp.mymeal

import androidx.compose.runtime.*
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.imageio.ImageIO
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap

@Composable
fun DesktopImagePicker() {
    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }

    ImagePickerUI(
        onPickImage = {
            val fileDialog = FileDialog(Frame(), "Select Image", FileDialog.LOAD)
            fileDialog.isVisible = true
            val file = fileDialog.files.firstOrNull()
            file?.let {
                val bufferedImage = ImageIO.read(it)
                selectedImage = bufferedImage.toComposeImageBitmap()
            }
        },
        imageBitmap = selectedImage
    )
}
