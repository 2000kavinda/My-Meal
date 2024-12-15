package org.myapp.mymeal

import androidx.compose.ui.graphics.ImageBitmap

expect fun ImageBitmap.toJpegByteArray(quality: Int = 100): ByteArray
