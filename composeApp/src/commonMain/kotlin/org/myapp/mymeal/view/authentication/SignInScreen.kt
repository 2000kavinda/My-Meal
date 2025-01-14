package org.myapp.mymeal.view.authentication

import org.myapp.mymeal.controller.AuthService
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun SignInScreen(
    authService: AuthService,
    isLoading: Boolean,
    message: String,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit,
    sharedViewModel: SharedViewModel,
) {


    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 600.dp
        val backgroundColor = if (isWideScreen) ColorThemes.PrimaryBgColor else MaterialTheme.colors.background

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            if (isWideScreen) {
                Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = Constants.logoUrl,
                            contentDescription = "Meal Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )

                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(16.dp)
                            .background(Color.White, shape = RoundedCornerShape(24.dp))
                            .padding(48.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        SignInForm(authService=authService,
                            isLoading=isLoading,
                            message=message,
                            onSignIn=onSignIn,
                            onSignUp=onSignUp,
                            sharedViewModel=sharedViewModel
                        )

                    }

                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SignInForm(authService=authService,
                        isLoading=isLoading,
                        message=message,
                        onSignIn=onSignIn,
                        onSignUp=onSignUp,
                        sharedViewModel=sharedViewModel
                    )
                }
            }
        }
    }
}













