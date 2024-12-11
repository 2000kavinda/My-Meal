package org.myapp.mymeal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun AndroidImagePicker() {
    val context = LocalContext.current
    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var nutritionalInfo by remember { mutableStateOf("Nutritional info will appear here...") }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage = bitmap.asImageBitmap()
            inputStream?.close()
        }
    }

    ImagePickerUI(
        onPickImage = { launcher.launch("image/*") },
        imageBitmap = selectedImage,
        onAnalyzeImage = {
            coroutineScope.launch {
                nutritionalInfo = analyzeImage(selectedImage)
            }
        },
        nutritionalInfo = nutritionalInfo
    )
}

@Composable
fun ImagePickerUI(
    onPickImage: () -> Unit,
    imageBitmap: ImageBitmap?,
    onAnalyzeImage: () -> Unit,
    nutritionalInfo: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onPickImage) {
            Text("Pick an Image")
        }
        Spacer(modifier = Modifier.height(16.dp))
        imageBitmap?.let {
            Image(bitmap = it, contentDescription = null, modifier = Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAnalyzeImage) {
                Text("Analyze Image")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(nutritionalInfo)
    }
}

suspend fun analyzeImage(imageBitmap: ImageBitmap?): String {
    if (imageBitmap == null) return "No image selected!"

    val client = HttpClient(CIO)
    val apiUserToken = "1fb8bc45ecba3dad4a9825e5669cb489015ec999"
    val headers = mapOf("Authorization" to "Bearer $apiUserToken")

    try {
        // Convert ImageBitmap to JPEG ByteArray
        val byteArray = imageBitmap.toJpegByteArray()

        // Upload Image
        val imageUploadResponse: HttpResponse = client.submitFormWithBinaryData(
            url = "https://api.logmeal.com/v2/image/segmentation/complete",
            formData = formData {
                append(
                    "image",
                    byteArray,
                    Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
                        append(HttpHeaders.ContentDisposition, "form-data; name=\"image\"; filename=\"image.jpg\"")
                    }
                )
            },
            block = {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiUserToken")
                }
            }
        )

        val uploadResponseText = imageUploadResponse.bodyAsText()
        println("Upload Response: $uploadResponseText")

        val jsonResponse = Json.parseToJsonElement(uploadResponseText)
        val imageId = jsonResponse.jsonObject["imageId"]?.jsonPrimitive?.content
            ?: return "Error: Image upload failed, no imageId received."

        // Fetch Nutritional Info
        val nutritionalInfoResponse: HttpResponse = client.post("https://api.logmeal.com/v2/recipe/nutritionalInfo") {
            contentType(ContentType.Application.Json)
            setBody("""{"imageId": "$imageId"}""")
            headers {
                append(HttpHeaders.Authorization, "Bearer $apiUserToken")
            }
        }
        return nutritionalInfoResponse.bodyAsText()
    } catch (e: Exception) {
        println("Error occurred: ${e.localizedMessage}")
        e.printStackTrace()
        return "Error: ${e.localizedMessage}"
    } finally {
        client.close()
    }
}

// Extension function to convert ImageBitmap to JPEG ByteArray
fun ImageBitmap.toJpegByteArray(): ByteArray {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    return outputStream.toByteArray()
}
