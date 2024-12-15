package org.myapp.mymeal

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

actual fun ImageBitmap.toJpegByteArray(quality: Int): ByteArray {
    val bufferedImage = BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB)
    val pixelMap = this.toPixelMap()

    for (x in 0 until this.width) {
        for (y in 0 until this.height) {
            bufferedImage.setRGB(x, y, pixelMap[x, y].toArgb())
        }
    }

    val outputStream = ByteArrayOutputStream()
    ImageIO.write(bufferedImage, "jpeg", outputStream)
    return outputStream.toByteArray()
}
