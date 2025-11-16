package com.itravelsolo.Screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.itravelsolo.R

private enum class AuthState {
    None,
    SignUp,
    SignIn
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
@Composable
fun Auth(navController: NavHostController) {
    var authState by remember { mutableStateOf(AuthState.None) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text =  "Share the \nJourney",
                color = Color(0xFF545928),
                fontSize = 65.sp,
                fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                fontWeight = FontWeight.Bold,
                lineHeight = 60.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Your next great \nconnection is on the same path",
                color = Color(0xFF545928),
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                textAlign = TextAlign.Center
            )
        }
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 28.dp).clip(RoundedCornerShape(40.dp)).background(Color.White).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedContent(
                    targetState = authState,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith fadeOut(animationSpec = tween(90)) using SizeTransform(
                            clip = false,
                            sizeAnimationSpec = { _, targetSize ->
                                tween(400, easing = FastOutSlowInEasing)
                            }
                        )
                    },
                    label = "Auth Form Animation"
                ) { targetSpec ->
                    when(targetSpec) {
                        AuthState.None -> {
                            AuthBox(
                                onSignUpClicked = { authState = AuthState.SignUp },
                                onSignInClicked = { authState = AuthState.SignIn }
                            )
                        }
                        AuthState.SignIn -> {
                            AuthForm(
                                formTitle = "Welcome back",
                                buttonText = "Sign In",
                                showNameField = false,
                                onSubmit = {  },
                                onDismiss = { authState = AuthState.None }
                            )
                        }
                        AuthState.SignUp -> {
                            AuthForm(
                                formTitle = "Create your account",
                                buttonText = "Sign Up",
                                showNameField = true,
                                onSubmit = {  },
                                onDismiss = { authState = AuthState.None }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthForm(
    formTitle: String,
    buttonText: String,
    showNameField: Boolean,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        focusedIndicatorColor = Color.Black,
        unfocusedIndicatorColor = Color.Black,
        cursorColor = Color.Black
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)
    ) {
        Text(
            text = formTitle,
            color = Color.Black,
            fontSize = 25.sp,
            fontFamily = FontFamily(Font(R.font.riveruta_medium)),
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(16.dp))
        if(showNameField) {
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        "Name",
                        fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                    )
                },
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(Modifier.height(16.dp))
        }
        TextField(
            value = email,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { email = it },
            label = {
                Text(
                    "Email",
                    fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                )
            },
            singleLine = true,
            colors = textFieldColors
        )
        Spacer(Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            colors = textFieldColors
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDismiss) {
                Text(
                    "Back",
                    fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2c2c2c)),
                modifier = Modifier.height(60.dp)
            ) {
                Text(
                    buttonText,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
fun AuthBox(
    onSignUpClicked: () -> Unit,
    onSignInClicked: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onSignUpClicked,
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2c2c2c)),
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) {
            Text(
                "Create new account",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                fontWeight = FontWeight.ExtraBold,
            )
        }
        TextButton(
            onClick = onSignInClicked,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                "I already have an account",
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                fontWeight = FontWeight.ExtraBold
            )
        }
        Divider(color = Color.LightGray)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign up with",
                color = Color.Gray,
                fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
            IconButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 50.dp).size(50.dp).clip(RoundedCornerShape(30.dp)).background(Color.White)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Login",
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}