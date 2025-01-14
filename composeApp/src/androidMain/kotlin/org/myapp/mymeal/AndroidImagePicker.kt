package org.myapp.mymeal

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.myapp.mymeal.controller.BuyMealController
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.controller.analyzeImages
import org.myapp.mymeal.model.Order
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.ui.theme.ColorThemes
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
    val sharedViewModel= SharedViewModel();
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    var buyMealController= BuyMealController()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)


            selectedImage = bitmap.asImageBitmap()
            inputStream?.close()

            coroutineScope.launch {
                isLoading = true
                nutritionalInfo = analyzeImages(selectedImage)
                val rows = nutritionalInfo.split("\n")
                val nutritionArray = rows.mapNotNull { row ->
                    val parts = row.split(":")
                    if (parts.size == 2) {
                        val nutrient = parts[0].trim()
                        val value = parts[1].trim()
                        nutrient to value
                    } else {
                        null
                    }
                }.toTypedArray()
                println("Nutritional Information: ")
                nutritionArray.forEach { println("Nutrient: ${it.first}, Value: ${it.second}") }

                fun getNutrientValue(nutrient: String): Double {
                    val found = nutritionArray.firstOrNull { it.first.equals(nutrient, ignoreCase = true) }
                    if (found != null) {
                        println("Found $nutrient: ${found.second}")
                    } else {
                        println("Nutrient $nutrient not found!")
                    }
                    return found?.second?.toDoubleOrNull() ?: 0.0
                }

                val firestoreRepository = HistoryController()

                buyMealController.saveOrder(
                    Order(
                        name = "",
                        calories = getNutrientValue("Calories"),
                        carbohydrates = getNutrientValue("Carbohydrates"),
                        proteins = getNutrientValue("Protein"),
                        fats = getNutrientValue("Total Fat"),
                        price = 0.0,
                        photo = "",
                        email = currentUserEmail?:"",
                        day = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                        description = "",
                        type = ""
                    )
                )

                isLoading = false
            }
        }
    }

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
    val screenWidthDp = configuration.screenWidthDp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size((screenWidthDp * 0.6).dp)
        )

        Spacer(modifier = Modifier.height(35.dp))


        CustomButton(
            text = "Insert External Nutrition",
            onClick = onPickImage,
            modifier = Modifier.width((screenWidthDp * 0.8).dp),
            backgroundColor = Color(0xFF002945),
            textColor = Color.White,
            icon = R.drawable.ic_gallery,
            iconTint = Color.White
        )


        CustomButton(
            text = "Skip for Now",
            onClick={
                navigationManager.navigateTo(Screen.MealList)
            },
            modifier = Modifier.width((screenWidthDp * 0.8).dp),
            backgroundColor = Color.White,
            textColor = Color(0xFF002945),
            borderColor = Color(0xFF002945)
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
    val topAppBarColor = ColorThemes.PrimaryButtonColor
    val backgroundColor = ColorThemes.PrimaryWhiteColor
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
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


            if (isLoading) {
                CircularProgressIndicator(color = ColorThemes.PrimaryButtonColor)
            } else if (nutritionalInfo.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = 8.dp,
                    shape = MaterialTheme.shapes.medium.copy(CornerSize(10.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val rows = nutritionalInfo.split("\n")
                        rows.forEachIndexed { index, row ->
                            val parts = row.split(":")
                            if (parts.size == 2) {
                                val nutrient = parts[0].trim()
                                val value = parts[1].trim()
                                NutrientRow(nutrient, value)
                            }

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

            Spacer(modifier = Modifier.weight(1f))
            if (isLoading) {

            } else if (nutritionalInfo.isNotEmpty()) {
                Button(
                    onClick = {
                        navigationManager.navigateTo(Screen.MealList)
                    },
                    modifier = Modifier
                        .width((screenWidthDp * 1).dp)
                        .height(70.dp)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorThemes.PrimaryButtonColor
                    ),
                    shape = MaterialTheme.shapes.medium.copy(
                        CornerSize(10.dp)
                    ),

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
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


@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ColorThemes.PrimaryButtonColor,
    textColor: Color = Color.White,
    borderColor: Color? = null,
    icon: Int? = null,
    iconTint: Color = Color.White,
    textSize: TextUnit = 18.sp
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(250.dp)
            .height(70.dp)
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(12.dp)),
        border = borderColor?.let { BorderStroke(2.dp, it) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            icon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = "Icon",
                    tint = iconTint,
                    modifier = Modifier.size(30.dp)
                )
            }

            Text(
                text = text,
                color = textColor,
                fontSize = textSize
            )
        }
    }
}







