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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.controller.analyzeImages
import org.myapp.mymeal.ui.theme.ColorThemes
import java.awt.FileDialog
import java.awt.Frame
import javax.imageio.ImageIO

@Composable
fun InitialScreen(
    onPickImage: () -> Unit,
    onSkip: () -> Unit
) {
    val logoPainter: Painter = painterResource("logo.png")


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorThemes.PrimaryBgColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.75f)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = logoPainter,
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(35.dp))
                Button(
                    onClick = onPickImage,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(70.dp)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorThemes.PrimaryButtonColor
                    ),
                    shape = MaterialTheme.shapes.medium.copy(
                        CornerSize(12.dp)
                    ),

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource("ic_gallery.png"),
                            contentDescription = "Gallery Icon",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            "Insert External Nutrition",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }

                Button(
                    onClick={
                        navigationManager.navigateTo(Screen.MealList)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(70.dp)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorThemes.PrimaryWhiteColor
                    ),
                    shape = MaterialTheme.shapes.medium.copy(
                        CornerSize(12.dp)
                    ),
                    border = BorderStroke(2.dp, ColorThemes.PrimaryButtonColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Text(
                            "Skip for Now",
                            color = ColorThemes.PrimaryButtonColor,
                            fontSize = 18.sp
                        )
                    }
                }
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
) {val topAppBarColor = ColorThemes.PrimaryButtonColor
    val backgroundColor = ColorThemes.PrimaryWhiteColor
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nutrition Report",
                        color = backgroundColor,
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

                                    if (index < analysisData.size - 1) {
                                        Divider(
                                            color = Color.Gray.copy(alpha = 0.3f),
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
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorThemes.PrimaryButtonColor
                        ),
                        shape = MaterialTheme.shapes.medium.copy(
                            CornerSize(10.dp)
                        ),
                    ) {
                        Text(
                            "Home",
                            color = Color.White,
                            fontSize = 18.sp
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


    fun pickImage() {
        val fileDialog = FileDialog(Frame(), "Select Image", FileDialog.LOAD)
        fileDialog.isVisible = true
        val file = fileDialog.files.firstOrNull()

        file?.let {
            val bufferedImage = ImageIO.read(it)
            selectedImage = bufferedImage.toComposeImageBitmap()
            showInitialScreen = false
            isLoading = true

            coroutineScope.launch {
                analysisResult = try {
                    analyzeImages(selectedImage)
                } catch (e: Exception) {
                    "Error: ${e.localizedMessage}"
                }
                isLoading = false
            }
        }
    }

    if (showInitialScreen) {

        InitialScreen(
            onPickImage = { pickImage() },
            onSkip = { showInitialScreen = false }
        )
    } else {

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
