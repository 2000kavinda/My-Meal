package org.myapp.mymeal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalConfiguration
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
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Composable
fun AndroidImagePicker() {
    val context = LocalContext.current
    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var nutritionalInfo by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            selectedImage = bitmap.asImageBitmap()
            inputStream?.close()

            // Automatically trigger the image analysis after image is picked
            coroutineScope.launch {
                isLoading = true
                nutritionalInfo = analyzeImages(selectedImage)
                isLoading = false
            }
        }
    }

    // If no image is selected, show the logo at the center
    if (selectedImage == null) {
        CenterLogoUI(onPickImage = { launcher.launch("image/*") })
    } else {
        ImagePickerUI(
            onPickImage = { launcher.launch("image/*") },
            imageBitmap = selectedImage,
            nutritionalInfo = nutritionalInfo,
            isLoading = isLoading,
            onBack = {

                selectedImage = null

                isLoading = false
            },
        )
    }
}

@Composable
fun CenterLogoUI(onPickImage: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp // Get screen width in dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Image
        Image(
            painter = painterResource(id = R.drawable.logo), // Replace with your logo resource
            contentDescription = "Logo",
            modifier = Modifier.size((screenWidthDp * 0.6).dp) // Adjust size based on screen width
        )

        Spacer(modifier = Modifier.height(35.dp)) // Add spacing between image and button

        // Styled Button with Icon and Text
        Button(
            onClick = onPickImage,
            modifier = Modifier
                .width((screenWidthDp * 0.8).dp) // Set button width
                .height(70.dp) // Set button height
                .padding(8.dp), // Add padding around the button
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF002945) // Set button background color (HEX: #FF0000 - Red)
            ),
            shape = MaterialTheme.shapes.medium.copy(
                CornerSize(12.dp) // Set border radius to 20.dp
            ),
            //border = BorderStroke(2.dp, Color.Blue) // Set border with color blue
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between icon and text
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gallery), // Replace with your gallery icon resource
                    contentDescription = "Gallery Icon",
                    tint = Color.White, // Set the icon color
                    modifier = Modifier.size(30.dp) // Set the icon size
                )
                Text(
                    "Insert External Nutrition",
                    color = Color.White, // Set text color
                    //fontWeight = FontWeight.Bold, // Bold the text
                    fontSize = 18.sp // Adjust text size
                )
            }
        }
        //Spacer(modifier = Modifier.height(5.dp)) // Add spacing between image and button


        Button(
            onClick={
                navigationManager.navigateTo(Screen.MealList)
            },
            modifier = Modifier
                .width((screenWidthDp * 0.8).dp) // Set button width
                .height(70.dp) // Set button height
                .padding(8.dp), // Add padding around the button
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFFFFFFF) // Set button background color (HEX: #FF0000 - Red)
            ),
            shape = MaterialTheme.shapes.medium.copy(
                CornerSize(12.dp) // Set border radius to 20.dp
            ),
            border = BorderStroke(2.dp, Color(0xFF002945)) // Set border with color blue
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between icon and text
            ) {
                /*Icon(
                    painter = painterResource(id = R.drawable.ic_gallery), // Replace with your gallery icon resource
                    contentDescription = "Gallery Icon",
                    tint = Color(0xFFCB823D), // Set the icon color
                    modifier = Modifier.size(30.dp) // Set the icon size
                )*/
                Text(
                    "Skip for Now",
                    color = Color(0xFF002945), // Set text color
                    //fontWeight = FontWeight.Bold, // Bold the text
                    fontSize = 18.sp // Adjust text size
                )
            }
        }
    }
}



@Composable
fun ImagePickerUI(
    onPickImage: () -> Unit,
    imageBitmap: ImageBitmap?,
    nutritionalInfo: String,
    isLoading: Boolean,
    onBack: () -> Unit,
) {
    val topAppBarColor = Color(0xFF002945)  // Example hex color for the app bar (Purple)
    val backgroundColor = Color(0xFFFFFFFF) // Example hex color for the background (Light Grey)
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nutrition Report",
                        color = backgroundColor, // Set text color here directly
                    ) },
                backgroundColor = topAppBarColor,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Picked Image Display
            imageBitmap?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(250.dp)
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Nutritional Info Display and Loading Indicator
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF002945))
            } else if (nutritionalInfo.isNotEmpty()) {
                // Nutritional Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = 8.dp,
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(10.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Display Nutritional Info in a table-like structure
                        val rows = nutritionalInfo.split("\n")
                        rows.forEachIndexed { index, row ->
                            val parts = row.split(":")
                            if (parts.size == 2) {
                                val nutrient = parts[0].trim()
                                val value = parts[1].trim()
                                NutrientRow(nutrient, value)
                            }

                            // Add Divider except for the last row
                            if (index < rows.size - 1) {
                                Divider(
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Spacer to push the button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Button to pick an image
            if (isLoading) {
                //CircularProgressIndicator(color = Color(0xFF002945))
            } else if (nutritionalInfo.isNotEmpty()) {
                Button(
                    onClick = {
                        navigationManager.navigateTo(Screen.MealList)
                    },
                    modifier = Modifier
                        .width((screenWidthDp * 1).dp) // Set button width
                        .height(70.dp) // Set button height
                        .padding(8.dp), // Add padding around the button
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF002945) // Set button background color (HEX: #FF0000 - Red)
                    ),
                    shape = MaterialTheme.shapes.medium.copy(
                        CornerSize(10.dp) // Set border radius to 20.dp
                    ),
                    //border = BorderStroke(2.dp, Color.Blue) // Set border with color blue
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between icon and text
                    ) {

                        Text(
                            "Home",
                            color = Color.White, // Set text color
                            //fontWeight = FontWeight.Bold, // Bold the text
                            fontSize = 18.sp // Adjust text size
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutrientRow(nutrient: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(nutrient, style = MaterialTheme.typography.body1)
        Text(value, style = MaterialTheme.typography.body1)
    }
}
/*
suspend fun analyzeImage(imageBitmap: ImageBitmap?): String {
    if (imageBitmap == null) return "No image selected!"

    val client = HttpClient(CIO)
    val apiUserToken = "842817cd0d4807a68c87b49b6cd9df67e0feb508"
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

        val nutritionalInfoText = nutritionalInfoResponse.bodyAsText()
        println("Nutritional Info Response: $nutritionalInfoText")

        val nutritionalInfoJson = Json.parseToJsonElement(nutritionalInfoText).jsonObject
        println("Full Nutritional Info Response: $nutritionalInfoJson")

        // Check for any error in the response
        nutritionalInfoJson["error"]?.jsonPrimitive?.let {
            return "Error: ${it.content}"
        }

        // Extract nutritional info
        val nutritionalInfo = nutritionalInfoJson["nutritional_info"]?.jsonObject
        if (nutritionalInfo != null) {
            val nutritionalInfoMap = mutableListOf<String>()

            var calories = 0f
            nutritionalInfo["calories"]?.jsonPrimitive?.let {
                calories = it.content.toFloat()
                nutritionalInfoMap.add("Calories: %.1f Kcal".format(calories))
            }

            val totalNutrients = nutritionalInfo["totalNutrients"]?.jsonObject

            var carbs = 0.0
            totalNutrients?.get("CHOCDF")?.jsonObject?.let {
                carbs = it["quantity"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val unit = it["unit"]?.jsonPrimitive?.content
                nutritionalInfoMap.add("Carbs (CHOCDF): %.1f $unit".format(carbs))
            }

            var fat = 0.0
            totalNutrients?.get("FAT")?.jsonObject?.let {
                fat = it["quantity"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val unit = it["unit"]?.jsonPrimitive?.content
                nutritionalInfoMap.add("Fat (FAT): %.1f $unit".format(fat))
            }

            var protein = 0.0
            totalNutrients?.get("PROCNT")?.jsonObject?.let {
                protein = it["quantity"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val unit = it["unit"]?.jsonPrimitive?.content
                nutritionalInfoMap.add("Protein (PROCNT): %.1f $unit".format(protein))
            }

            var sugar = 0.0
            totalNutrients?.get("SUGAR")?.jsonObject?.let {
                sugar = it["quantity"]?.jsonPrimitive?.doubleOrNull ?: 0.0
                val unit = it["unit"]?.jsonPrimitive?.content
                nutritionalInfoMap.add("Sugar (SUGAR): %.1f $unit".format(sugar))
            }

            // Create an Order object
            val order = Order(
                name = "Sample Meal", // Replace with the actual name of the meal
                calories = calories.toDouble(),
                carbohydrates = carbs,
                proteins = protein,
                fats = fat,
                price = 15.0, // Replace with actual price
                photo = "https://example.com/meal.jpg", // Replace with actual photo URL
                email = "user@example.com" // Replace with actual user email
            )

            // Save to Firestore
            saveOrderToFirestore(order)

            // Return the nutritional information as a string
            return if (nutritionalInfoMap.isNotEmpty()) {
                nutritionalInfoMap.joinToString("\n")
            } else {
                "Error: Nutritional info not found."
            }
        } else {
            return "Error: Nutritional info not found."
        }

    } catch (e: Exception) {
        println("Error occurred: ${e.localizedMessage}")
        e.printStackTrace()
        return "Error: ${e.localizedMessage}"
    } finally {
        client.close()
    }
}*/

// Function to save order to Firestore
suspend fun saveOrderToFirestore(order: Order) {
    try {
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document() // Auto-generate ID
        orderRef.set(order).await() // Save the order data to Firestore
        println("Order saved successfully")
    } catch (e: Exception) {
        println("Error saving order: ${e.message}")
    }
}



/*
// Extension function to convert ImageBitmap to JPEG ByteArray
fun ImageBitmap.toJpegByteArrayh(): ByteArray {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    return outputStream.toByteArray()
}
fun Bitmap.toJpegByteArray(quality: Int = 100): ByteArray {
    val outputStream = ByteArrayOutputStream()
    // Compress the Bitmap into JPEG format and write to the output stream
    this.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return outputStream.toByteArray()
}*//*
fun ImageBitmap.toJpegByteArray(quality: Int = 100): ByteArray {
    val androidBitmap = this.asAndroidBitmap()
    val outputStream = ByteArrayOutputStream()
    androidBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    return outputStream.toByteArray()
}*/

