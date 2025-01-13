package org.myapp.mymeal

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
//import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import androidx.compose.ui.res.painterResource
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.components.CustomButton
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.controller.analyzeImages
import org.myapp.mymeal.model.Order
import org.myapp.mymeal.navigation.Screen
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AndroidImagePicker() {
    val context = LocalContext.current
    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var nutritionalInfo by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val sharedViewModel=SharedViewModel();
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()

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
                val rows = nutritionalInfo.split("\n")

// Create an array of key-value pairs (nutrient and value) by splitting rows
                val nutritionArray = rows.mapNotNull { row ->
                    val parts = row.split(":")
                    if (parts.size == 2) {
                        val nutrient = parts[0].trim()
                        val value = parts[1].trim()
                        nutrient to value // Create a key-value pair
                    } else {
                        null // Skip invalid rows
                    }
                }.toTypedArray()

// Debugging: print the array to see the contents
                println("Nutritional Information: ")
                nutritionArray.forEach { println("Nutrient: ${it.first}, Value: ${it.second}") }

                // Create a function to find value by nutrient name
                fun getNutrientValue(nutrient: String): Double {
                    // Debugging: check if we find the nutrient
                    val found = nutritionArray.firstOrNull { it.first.equals(nutrient, ignoreCase = true) }
                    if (found != null) {
                        println("Found $nutrient: ${found.second}")
                    } else {
                        println("Nutrient $nutrient not found!")
                    }
                    return found?.second?.toDoubleOrNull() ?: 0.0
                }

// Save the data using the nutritionArray values
                val firestoreRepository = FirestoreRepository()

                firestoreRepository.saveOrder(
                    Order(
                        name = "",
                        calories = getNutrientValue("Calories"),
                        carbohydrates = getNutrientValue("Carbohydrates"),
                        proteins = getNutrientValue("Protein"),
                        fats = getNutrientValue("Total Fat"),
                        price = 0.0,
                        photo = "", // Replace with the actual photo URL if available
                        email = currentUserEmail?:"", // Replace with the user's email from your app context
                        day = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                        description = "",
                        type = ""
                    )
                )



                /* val order = Order(
                     name = "",
                     calories = nutritionMap["Calories"]?.toDoubleOrNull() ?: 0.0,
                     carbohydrates = nutritionMap["Carbohydrates"]?.toDoubleOrNull() ?: 0.0,
                     proteins = nutritionMap["Protein"]?.toDoubleOrNull() ?: 0.0,
                     fats = nutritionMap["Total Fat"]?.toDoubleOrNull() ?: 0.0,
                     price = 0.0,
                     photo = "", // Replace with the actual photo URL if available
                     email = "user@example.com", // Replace with the user's email from your app context
                     day = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                     description = "",
                     type = "" // Replace with a more specific type if applicable
                 )

                 // Save the order to Firestore
                 saveOrderToFirestore(order)*/

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
        CustomButton(
            text = "Insert External Nutrition",
            onClick = onPickImage,
            modifier = Modifier.width((screenWidthDp * 0.8).dp),
            backgroundColor = Color(0xFF002945),
            textColor = Color.White,
            icon = R.drawable.ic_gallery,
            iconTint = Color.White
        )
        //Spacer(modifier = Modifier.height(5.dp)) // Add spacing between image and button

        CustomButton(
            text = "Skip for Now",
            onClick={
                navigationManager.navigateTo(Screen.MealList)
            },
            modifier = Modifier.width((screenWidthDp * 0.8).dp),
            backgroundColor = Color.White,
            textColor = Color(0xFF002945),
            borderColor = Color(0xFF002945) // Add border
        )

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




