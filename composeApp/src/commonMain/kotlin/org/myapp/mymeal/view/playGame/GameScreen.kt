import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.myapp.mymeal.controller.HistoryController
import org.myapp.mymeal.controller.GameController
import org.myapp.mymeal.navigation.NavigationManager
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.ui.theme.ColorThemes
import org.myapp.mymeal.utils.Constants
import kotlin.random.Random

@Composable
fun GameScreen(sharedViewModel: SharedViewModel,
               ) {
    val firestoreRepository = remember { HistoryController() }
    var isCollisionDetected by remember { mutableStateOf(false) }
    var playerXPosition by remember { mutableStateOf(0f) }
    var ballPositionY by remember { mutableStateOf(0f) }
    var ballPositionX by remember { mutableStateOf(Random.nextFloat()) }
    var timeElapsed by remember { mutableStateOf(0f) }
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    val ballSize = 50f
    val playerBallWidth = 100f
    val playerBallHeight = 100f

    val initialBallSpeedY = 5f
    val speedIncreaseFactor = 0.01f


    LaunchedEffect(Unit) {
        while (true) {
            if (!isCollisionDetected) {
                val ballSpeedY = initialBallSpeedY + (timeElapsed * speedIncreaseFactor)
                ballPositionY += ballSpeedY / 1000f
                timeElapsed += 1f

                if (ballPositionY > 1f) {
                    ballPositionY = -0.1f
                    ballPositionX = Random.nextFloat()
                }
            }

            delay(20)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan)
    ) {
        Button(
            onClick = { navigationManager.navigateTo(Screen.PlayScreen) },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .zIndex(1f),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ColorThemes.PrimaryButtonColor
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        AsyncImage(
            model = Constants.gameBgUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )


        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val ballX = canvasWidth * ballPositionX
            val ballY = canvasHeight * ballPositionY

            val diamondWidth = ballSize * 0.8f
            val diamondHeight = ballSize * 2f
            val diamondPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(ballX, ballY - diamondHeight / 2)
                lineTo(ballX + diamondWidth / 2, ballY)
                lineTo(ballX, ballY + diamondHeight / 2)
                lineTo(ballX - diamondWidth / 2, ballY)
                close()
            }


            drawPath(
                path = diamondPath,
                color = ColorThemes.UvColor
            )
            val playerY = canvasHeight - 120.dp.toPx()
            val playerX = canvasWidth / 2 + playerXPosition.dp.toPx()

            val isBallWithinPlayerHorizontally =
                ballX >= playerX - playerBallWidth / 2 && ballX <= playerX + playerBallWidth / 2
            val isBallWithinPlayerVertically =
                ballY >= playerY - playerBallHeight / 2 && ballY <= playerY + playerBallHeight / 2

            if (!isCollisionDetected && isBallWithinPlayerHorizontally && isBallWithinPlayerVertically) {
                isCollisionDetected = true
            }
        }

         AsyncImage(
            model = Constants.gameCharUrl,
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.BottomCenter)
                .offset(x = playerXPosition.dp)
                .padding(bottom = 20.dp)
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        playerXPosition += dragAmount
                    }
                }
        )


        if (isCollisionDetected) {

            AlertDialog(
                onDismissRequest = { /* Do nothing to prevent closing */ },
                title = { Text("Game Over", fontSize = 20.sp) },
                text = {
                    Text("You are uncovered to the UVs\nEarned Coins: ${(timeElapsed/100).toInt()}")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                restartGameLogic(
                                    firestoreRepository,
                                    currentUserEmail,
                                    { isCollisionDetected = false },
                                    { ballPositionY = -0.1f },
                                    { ballPositionX = Random.nextFloat() },
                                    { playerXPosition = 0f },
                                    { timeElapsed = 0f },
                                    navigationManager,
                                    (timeElapsed/100).toDouble()
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorThemes.PrimaryButtonColor,
                            contentColor = ColorThemes.PrimaryTextColor
                        )
                    ) {
                        Text("Restart")
                    }
                }

            )
        }
    }
}
suspend fun restartGameLogic(
    firestoreRepository: HistoryController,
    currentUserEmail: String?,
    resetCollision: () -> Unit,
    resetBallY: () -> Unit,
    resetBallX: () -> Unit,
    resetPlayerX: () -> Unit,
    resetTime: () -> Unit,
    navigationManager: NavigationManager,
    count: Double
) {
    resetCollision()
    resetBallY()
    resetBallX()
    resetPlayerX()
    resetTime()
    val gameController= GameController()
    gameController.increaseCoinAmountByEmail(
        email = currentUserEmail ?: "",
        increaseAmount = count
    )

    withContext(Dispatchers.Main) {
        navigationManager.navigateTo(Screen.PlayScreen)
    }
}

