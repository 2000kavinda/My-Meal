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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import org.myapp.mymeal.controller.FirestoreRepository
import org.myapp.mymeal.navigation.NavigationManager
import org.myapp.mymeal.navigation.NavigationProvider.navigationManager
import org.myapp.mymeal.navigation.Screen
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.ui.theme.ColorThemes
import kotlin.random.Random

@Composable
fun GameScreen(sharedViewModel: SharedViewModel,
               ) {
    val firestoreRepository = remember { FirestoreRepository() }
    var isCollisionDetected by remember { mutableStateOf(false) }
    var playerXPosition by remember { mutableStateOf(0f) } // Horizontal position of the player
    var ballPositionY by remember { mutableStateOf(0f) } // Vertical position of the falling ball
    var ballPositionX by remember { mutableStateOf(Random.nextFloat()) } // Random horizontal position of the ball
    var timeElapsed by remember { mutableStateOf(0f) } // Track the time elapsed
    val currentUserEmail by sharedViewModel.currentUserEmail.collectAsState()
    // Constants for ball size and player size
    val ballSize = 50f // Radius of the falling ball
    val playerBallWidth = 100f // Approximate width of the GIF for collision detection
    val playerBallHeight = 100f // Approximate height of the GIF for collision detection

    // Ball speed increases over time
    val initialBallSpeedY = 5f // Initial speed at which the ball moves down
    val speedIncreaseFactor = 0.01f // Factor by which the speed increases over time

    // Ball falling animation (the ball falls continuously)
    LaunchedEffect(Unit) {
        while (true) {
            if (!isCollisionDetected) {
                // Increase speed over time
                val ballSpeedY = initialBallSpeedY + (timeElapsed * speedIncreaseFactor)
                ballPositionY += ballSpeedY / 1000f // Adjust to control the speed of fall
                timeElapsed += 1f // Increase the elapsed time

                // Reset ball when it goes past the bottom
                if (ballPositionY > 1f) {
                    ballPositionY = -0.1f // Reset position to top
                    ballPositionX = Random.nextFloat() // Generate a new random horizontal position
                }
            }

            // Delay to control animation speed
            delay(20)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan) // Background color
    ) {
        Button(
            onClick = { navigationManager.navigateTo(Screen.PlayScreen) },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .zIndex(1f), // Ensure the button is above other elements
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ColorThemes.PrimaryButtonColor // Set the background color
            ),
            contentPadding = PaddingValues(0.dp) // Optional, remove extra padding around the icon
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Use the back arrow icon
                contentDescription = "Back",
                tint = Color.White // Set icon color
            )
        }

        // Background image
        AsyncImage(
            model = "https://firebasestorage.googleapis.com/v0/b/care-cost.appspot.com/o/gifs%2FDALL%C2%B7E%202024-12-11%2003.25.03%20-%20A%20vibrant%2C%20cartoon-style%20background%20showing%20train%20tracks%20in%20a%20city%20setting%20from%20a%20bottom-to-up%20perspective.%20The%20scene%20includes%20detailed%20graffiti%20art%20o.webp?alt=media&token=76fedd2e-8533-4a06-b6a1-427819a5b9f1", // Replace with your image URL
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize() // Fill the whole screen with the background image
                .align(Alignment.Center),
            contentScale = ContentScale.Crop // Ensures the image covers the full screen, cropping excess parts if necessary
        )


        // Ball falling from the top
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val ballX = canvasWidth * ballPositionX // Calculate ball position horizontally
            val ballY = canvasHeight * ballPositionY // Calculate ball position vertically

            val diamondWidth = ballSize * 0.7f  // Reduced width (50% of the original size)
            val diamondHeight = ballSize * 1.5f
            val diamondPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(ballX, ballY - diamondHeight / 2) // Top point of the diamond
                lineTo(ballX + diamondWidth / 2, ballY) // Right point of the diamond
                lineTo(ballX, ballY + diamondHeight / 2) // Bottom point of the diamond
                lineTo(ballX - diamondWidth / 2, ballY) // Left point of the diamond
                close() // Close the path to form the diamond
            }


            // Draw the diamond shape
            drawPath(
                path = diamondPath,
                color = ColorThemes.UvColor // Set the diamond color to blue (or any other color)
            )
            // Player GIF position
            val playerY = canvasHeight - 120.dp.toPx() // Player's vertical position
            val playerX = canvasWidth / 2 + playerXPosition.dp.toPx() // Adjust for player's horizontal movement

            // Detect collision between the falling ball and the player GIF
            val isBallWithinPlayerHorizontally =
                ballX >= playerX - playerBallWidth / 2 && ballX <= playerX + playerBallWidth / 2
            val isBallWithinPlayerVertically =
                ballY >= playerY - playerBallHeight / 2 && ballY <= playerY + playerBallHeight / 2

            if (!isCollisionDetected && isBallWithinPlayerHorizontally && isBallWithinPlayerVertically) {
                isCollisionDetected = true // Collision detected
            }
        }

        // Player as GIF
        AsyncImage(
            model = "https://firebasestorage.googleapis.com/v0/b/care-cost.appspot.com/o/gifs%2F471559259023201-unscreen.gif?alt=media&token=f48b0cc2-71f9-419e-a181-dc15692855d9", // Replace with your GIF URL
            contentDescription = null,
            modifier = Modifier
                .size(150.dp) // Set size for the GIF
                .align(Alignment.BottomCenter)
                .offset(x = playerXPosition.dp)
                .padding(bottom = 20.dp)
        )

        // Detect horizontal dragging for player movement
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        playerXPosition += dragAmount // Update horizontal position
                    }
                }
        )

        // Show popup when collision is detected
        if (isCollisionDetected) {
           // firestoreRepository.increaseCoinAmountByEmail(email = currentUserEmail?:"", increaseAmount = (timeElapsed/100).toInt().toDouble())

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
                                    navigationManager
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
    firestoreRepository: FirestoreRepository,
    currentUserEmail: String?,
    resetCollision: () -> Unit,
    resetBallY: () -> Unit,
    resetBallX: () -> Unit,
    resetPlayerX: () -> Unit,
    resetTime: () -> Unit,
    navigationManager: NavigationManager
) {
    resetCollision()
    resetBallY()
    resetBallX()
    resetPlayerX()
    resetTime()

    firestoreRepository.increaseCoinAmountByEmail(
        email = currentUserEmail ?: "",
        increaseAmount = 10.0
    )

    withContext(Dispatchers.Main) {
        navigationManager.navigateTo(Screen.PlayScreen)
    }
}

