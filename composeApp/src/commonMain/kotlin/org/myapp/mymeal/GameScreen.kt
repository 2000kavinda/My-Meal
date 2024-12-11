import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameScreen() {
    var isCollisionDetected by remember { mutableStateOf(false) }
    var playerXPosition by remember { mutableStateOf(0f) } // Horizontal position of the player
    var ballPositionY by remember { mutableStateOf(0f) } // Vertical position of the falling ball
    var ballPositionX by remember { mutableStateOf(Random.nextFloat()) } // Random horizontal position of the ball
    var timeElapsed by remember { mutableStateOf(0f) } // Track the time elapsed

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
        // Background image
        AsyncImage(
            model = "https://firebasestorage.googleapis.com/v0/b/care-cost.appspot.com/o/gifs%2FDALL%C2%B7E%202024-12-11%2003.25.03%20-%20A%20vibrant%2C%20cartoon-style%20background%20showing%20train%20tracks%20in%20a%20city%20setting%20from%20a%20bottom-to-up%20perspective.%20The%20scene%20includes%20detailed%20graffiti%20art%20o.webp?alt=media&token=76fedd2e-8533-4a06-b6a1-427819a5b9f1", // Replace with your image URL
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth() // Fill the whole screen with the background image
                .align(Alignment.Center)
        )

        // Ball falling from the top
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val ballX = canvasWidth * ballPositionX // Calculate ball position horizontally
            val ballY = canvasHeight * ballPositionY // Calculate ball position vertically

            // Draw the falling ball
            drawCircle(
                color = Color.Red,
                radius = ballSize,
                center = Offset(ballX, ballY)
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
                .size(100.dp) // Set size for the GIF
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
            AlertDialog(
                onDismissRequest = { /* Do nothing to prevent closing */ },
                title = { Text("Game Over", fontSize = 20.sp) },
                text = { Text("The ball collided with the player!") },
                confirmButton = {
                    Button(onClick = {
                        isCollisionDetected = false // Restart the game by resetting collision state
                        ballPositionY = -0.1f // Reset the ball position
                        ballPositionX = Random.nextFloat() // Generate a new random position
                        playerXPosition = 0f // Reset the player's position
                        timeElapsed = 0f // Reset time elapsed
                    }) {
                        Text("Restart")
                    }
                }
            )
        }
    }
}
