package org.myapp.mymeal

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

suspend fun analyzeImages(imageBitmap: ImageBitmap?): String {
    if (imageBitmap == null) {
        return "Error: No image selected."
    }

    return withContext(Dispatchers.IO) {
        val client = HttpClient(CIO)
        val apiUserToken = "07034037979061f548f175ff21f1a5db734182e9"
        val headers = mapOf("Authorization" to "Bearer $apiUserToken")

        try {
            // Convert ImageBitmap to ByteArray
            val imageBytes = imageBitmap.toJpegByteArray()

            // Step 1: Upload Image
            val segmentationResponse = client.submitFormWithBinaryData(
                url = "https://api.logmeal.com/v2/image/segmentation/complete",
                formData = formData {
                    append("image", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                    })
                }
            ) {
                headers.forEach { (key, value) -> header(key, value) }
            }

            val segmentationBody = segmentationResponse.bodyAsText()
            val imageId = Json.parseToJsonElement(segmentationBody)
                .jsonObject["imageId"]?.jsonPrimitive?.content
                ?: return@withContext "Error: ImageId not received."

            // Step 2: Fetch Nutritional Information
            val nutritionalResponse = client.post("https://api.logmeal.com/v2/recipe/nutritionalInfo") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiUserToken")
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody("""{"imageId":"$imageId"}""")
            }

            val nutritionalBody = nutritionalResponse.bodyAsText()

            // Parse JSON and extract required fields
            val jsonResponse = Json.parseToJsonElement(nutritionalBody).jsonObject
            val nutritionalInfo = jsonResponse["nutritional_info"]?.jsonObject ?: return@withContext "Error: Nutritional info not found."

            val calories = nutritionalInfo["calories"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val dailyIntake = nutritionalInfo["dailyIntakeReference"]?.jsonObject

            val carbsPercent = dailyIntake?.get("CHOCDF")?.jsonObject?.get("percent")?.jsonPrimitive?.doubleOrNull ?: 0.0
            val fatPercent = dailyIntake?.get("FAT")?.jsonObject?.get("percent")?.jsonPrimitive?.doubleOrNull ?: 0.0
            val proteinPercent = dailyIntake?.get("PROCNT")?.jsonObject?.get("percent")?.jsonPrimitive?.doubleOrNull ?: 0.0
            val sugarPercent = dailyIntake?.get("SUGAR")?.jsonObject?.get("percent")?.jsonPrimitive?.doubleOrNull ?: 0.0

            // Convert percentages to weights (grams)
            val carbsWeight = (carbsPercent * calories) / (100 * 4) // 4 calories per gram of carbs
            val fatWeight = (fatPercent * calories) / (100 * 9) // 9 calories per gram of fat
            val proteinWeight = (proteinPercent * calories) / (100 * 4) // 4 calories per gram of protein
            val sugarWeight = (sugarPercent * calories) / (100 * 4) // 4 calories per gram of sugar

            // Format the result with weights instead of percentages
            """
                Calories: ${"%.2f".format(calories)}
                Carbohydrates: ${"%.2f".format(carbsWeight)} g
                Total Fat: ${"%.2f".format(fatWeight)} g
                Protein: ${"%.2f".format(proteinWeight)} g
                Sugar: ${"%.2f".format(sugarWeight)} g
            """.trimIndent()
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        } finally {
            client.close()
        }
    }
}


/*
fun ImageBitmap.toJpegByteArrays(): ByteArray {
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
*/


