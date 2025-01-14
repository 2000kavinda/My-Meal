package org.myapp.mymeal.view.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.myapp.mymeal.state.SharedViewModel
import org.myapp.mymeal.ui.theme.ColorThemes
import org.myapp.mymeal.utils.Constants

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignUpScreen(
    isLoading: Boolean,
    message: String,
    onSave: (String, String, String, String, String, () -> Unit) -> Unit,
    sharedViewModel: SharedViewModel,
) {

    BoxWithConstraints(modifier = Modifier.fillMaxSize()){
        val isWideScreen = maxWidth > 600.dp
        val backgroundColor = if (isWideScreen) ColorThemes.PrimaryBgColor else MaterialTheme.colors.background

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = Constants.logoUrl,
                            contentDescription = "Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier

                                .fillMaxHeight()
                                .padding(16.dp)
                                .background(Color.White, shape = RoundedCornerShape(24.dp))
                                .padding(48.dp)
                            .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {

                            SignUpForm(
                                isLoading = isLoading,
                                message = message,

                                onSave = onSave,
                                sharedViewModel = sharedViewModel
                            )

                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier

                        .fillMaxHeight()
                        .padding(16.dp)
                        .background(Color.White, shape = RoundedCornerShape(24.dp))
                        .padding(48.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    SignUpForm(
                        isLoading = isLoading,
                        message = message,

                        onSave = onSave,
                        sharedViewModel = sharedViewModel
                    )
                }
            }
        }
    }
}




