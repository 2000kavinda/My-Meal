import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.myapp.mymeal.NavigationProvider.navigationManager
import org.myapp.mymeal.Screen
import org.myapp.mymeal.analyzeImages
import java.awt.FileDialog
import java.awt.Frame
import javax.imageio.ImageIO

@Composable
fun InitialScreen(
    onPickImage: () -> Unit,
    onSkip: () -> Unit
) {
    val logoPainter: Painter = painterResource("logo.png") // Replace with your logo resource

    // Screen with full background color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCBD5ED)), // Correct orange background color
        contentAlignment = Alignment.Center
    ) {
        // Centered rounded rectangle
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f) // White box width (85% of screen width)
                .fillMaxHeight(0.75f) // White box height (75% of screen height)
                .background(Color.White, shape = RoundedCornerShape(24.dp)) // White rounded rectangle
                .padding(16.dp), // Inner padding inside the rectangle
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Logo
                Image(
                    painter = logoPainter,
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp) // Adjust logo size
                )
                //Spacer(modifier = Modifier.height(32.dp))


                Spacer(modifier = Modifier.height(35.dp)) // Add spacing between image and button

                // Styled Button with Icon and Text
                Button(
                    onClick = onPickImage,
                    modifier = Modifier
                        .fillMaxWidth(0.5f) // Set button width
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
                            painter = painterResource("ic_gallery.png"), // Replace with your gallery icon resource
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
                        .fillMaxWidth(0.5f) // Set button width
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
                // "Insert" Button
                /*Button(
                    onClick = onPickImage,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFCD7F32)),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp)
                ) {
                    Text("Insert", fontSize = 16.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // "Skip" Button
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFCD7F32)),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(50.dp)
                ) {
                    Text("Skip", fontSize = 16.sp, color = Color.White)
                }*/
            }
        }
    }
}

@Composable
fun ImagePickerUI(
    imageBitmap: ImageBitmap?,
    onBack: () -> Unit,
    analysisResult: String?,
    isLoading: Boolean
) {val topAppBarColor = Color(0xFF002945)  // Example hex color for the app bar (Purple)
    val backgroundColor = Color(0xFFFFFFFF)
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",tint = Color.White)
                    }
                }
            )
        }

    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Left Column: Half-screen image display
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp)
                    )
                } ?: Text("No Image Selected", color = Color.Gray)
            }

            // Right Column: Controls and Results
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp),color = Color(0xFF002945))
                } else {
                    // Card for Nutritional Information
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = 8.dp
                    ) {
                        val analysisData = parseAnalysisResult(analysisResult)

                        Column(modifier = Modifier.padding(16.dp)) {
                            if (analysisData.isNotEmpty()) {
                                analysisData.forEachIndexed { index, (label, value) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.body1,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = value,
                                            style = MaterialTheme.typography.body1,
                                            color = Color.Black
                                        )
                                    }

                                    // Add a faded Divider between rows, except after the last item
                                    if (index < analysisData.size - 1) {
                                        Divider(
                                            color = Color.Gray.copy(alpha = 0.3f), // Faded line with reduced opacity
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "No analysis data available.",
                                    style = MaterialTheme.typography.body1,
                                    color = Color.Red
                                )
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick={
                            navigationManager.navigateTo(Screen.MealList)
                        },
                        modifier = Modifier
                            .fillMaxWidth() // Ensure the button fills the available width
                            .height(70.dp)
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF002945) // Set button background color (HEX: #FF0000 - Red)
                        ),
                        shape = MaterialTheme.shapes.medium.copy(
                            CornerSize(10.dp) // Set border radius to 20.dp
                        ),
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
fun parseAnalysisResult(result: String?): List<Pair<String, String>> {
    if (result.isNullOrEmpty()) return emptyList()

    return result.lines()
        .mapNotNull { line ->
            val parts = line.split(":")
            if (parts.size == 2) {
                parts[0].trim() to parts[1].trim()
            } else {
                null
            }
        }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DesktopImagePicker() {
    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }
    var analysisResult by remember { mutableStateOf<String?>(null) }
    var showInitialScreen by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Function to pick an image and start the analysis automatically
    fun pickImage() {
        val fileDialog = FileDialog(Frame(), "Select Image", FileDialog.LOAD)
        fileDialog.isVisible = true
        val file = fileDialog.files.firstOrNull()

        file?.let {
            val bufferedImage = ImageIO.read(it)
            selectedImage = bufferedImage.toComposeImageBitmap()
            showInitialScreen = false // Move to the main screen after picking
            isLoading = true // Show loading indicator

            // Automatically start analyzing the image
            coroutineScope.launch {
                analysisResult = try {
                    analyzeImages(selectedImage) // Run analysis automatically
                } catch (e: Exception) {
                    "Error: ${e.localizedMessage}"
                }
                isLoading = false // Hide loading indicator after analysis is complete
            }
        }
    }

    if (showInitialScreen) {
        // Initial screen with Pick Image and Skip
        InitialScreen(
            onPickImage = { pickImage() },
            onSkip = { showInitialScreen = false }
        )
    } else {
        // Main Image Picker UI
        ImagePickerUI(
            imageBitmap = selectedImage,
            onBack = {
                showInitialScreen = true
                selectedImage = null
                analysisResult = null
                isLoading = false
            },
            analysisResult = analysisResult,
            isLoading = isLoading
        )
    }
}
