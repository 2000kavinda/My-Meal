package org.myapp.mymeal.controller

import androidx.compose.ui.graphics.ImageBitmap
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
import org.myapp.mymeal.toJpegByteArray
import org.myapp.mymeal.utils.Constants

suspend fun analyzeImages(imageBitmap: ImageBitmap?): String {
    if (imageBitmap == null) {
        return "Error: No image selected."
    }

    return withContext(Dispatchers.IO) {
        val client = HttpClient(CIO)
        val apiUserToken = Constants.tokenValue
        val headers = mapOf("Authorization" to "Bearer $apiUserToken")

        try {
            val imageBytes = imageBitmap.toJpegByteArray()

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

            val nutritionalResponse = client.post("https://api.logmeal.com/v2/recipe/nutritionalInfo") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiUserToken")
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody("""{"imageId":"$imageId"}""")
            }

            val nutritionalBody = nutritionalResponse.bodyAsText()

            val jsonResponse = Json.parseToJsonElement(nutritionalBody).jsonObject
            val nutritionalInfo = jsonResponse["nutritional_info"]?.jsonObject ?: return@withContext "Error: Nutritional info not found."

            val calories = nutritionalInfo["calories"]?.jsonPrimitive?.doubleOrNull ?: 0.0
            val dailyIntake = nutritionalInfo["dailyIntakeReference"]?.jsonObject

            val carbsPercent = dailyIntake?.get("CHOCDF")?.jsonObject?.get("percent")?.jsonPrimitive?.doubleOrNull ?: 0.0
            val fatPercent = dailyIntake?.get("FAT")?.jsonObject?.get("percent")?.jsonPrimitive?.doubleOrNull ?: 0.0
            val proteinPercent = dailyIntake?.get("PROCNT")?.jsonObject?.get("percent")?.jsonPrimitive?.doubleOrNull ?: 0.0
            val sugarPercent = dailyIntake?.get("SUGAR")?.jsonObject?.get("percent")?.jsonPrimitive?.doubleOrNull ?: 0.0

            val carbsWeight = (carbsPercent * calories) / (100 * 4)
            val fatWeight = (fatPercent * calories) / (100 * 9)
            val proteinWeight = (proteinPercent * calories) / (100 * 4)
            val sugarWeight = (sugarPercent * calories) / (100 * 4)


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





