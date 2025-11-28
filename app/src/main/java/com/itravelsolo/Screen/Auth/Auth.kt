package com.itravelsolo.Screen.Auth

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.itravelsolo.R
import kotlinx.coroutines.delay

private enum class AuthState {
    None,
    SignUp,
    SignIn,
    OTP
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
@Composable
fun Auth(navController: NavHostController) {
    var authState by remember { mutableStateOf(AuthState.None) }
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )
    val authResult by authViewModel.authResult.collectAsState()

    LaunchedEffect(authResult) {
        when(val result = authResult) {
            is AuthResult.OTPSent -> {
                Toast.makeText(context, "OTP sent", Toast.LENGTH_SHORT).show()
                authState = AuthState.OTP
                authViewModel.resetResult()
            }
            is AuthResult.AuthenticationSuccess -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                navController.navigate("main") {
                    popUpTo(0) { inclusive = true }
                }
                authViewModel.resetResult()
            }
            is AuthResult.GeneralSuccess -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                navController.navigate("main") {
                    popUpTo(0) { inclusive = true }
                }
                authViewModel.resetResult()
            }
            is AuthResult.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                authViewModel.resetResult()
            }
            else -> {}
        }
    }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 28.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.White)
                    .padding(32.dp),
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
                        AuthState.SignIn -> {
                            AuthForm(
                                formTitle = "Welcome back",
                                buttonText = "Sign In",
                                showNameField = false,
                                isLoading = authResult is AuthResult.Loading,
                                onSubmit = { _, email, password ->
                                    authViewModel.signInUser(email, password)
                                },
                                onDismiss = { authState = AuthState.None }
                            )
                        }
                        AuthState.SignUp -> {
                            AuthForm(
                                formTitle = "Create your account",
                                buttonText = "Sign Up",
                                showNameField = true,
                                isLoading = authResult is AuthResult.Loading,
                                onSubmit = { name, email, password ->
                                    authViewModel.signUpUser(name, email, password)
                                },
                                onDismiss = { authState = AuthState.None }
                            )
                        }
                        AuthState.OTP -> {
                            OTPBox(
                                isLoading = authResult is AuthResult.Loading,
                                onSubmit = { otp ->
                                    authViewModel.verifyOTP(otp, VerificationType.SignUpVerification)
                                },
                                onDismiss = { authState = AuthState.None },
                                onRequest = { authViewModel.requestEmailVerificationOtp() }
                            )
                        }
                        else -> {
                            AuthBox(
                                onSignUpClicked = { authState = AuthState.SignUp },
                                onSignInClicked = { authState = AuthState.SignIn }
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
    isLoading: Boolean,
    onSubmit: (String, String, String) -> Unit,
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
                onClick = {
                    onSubmit(name, email, password)
                },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2c2c2c)),
                modifier = Modifier.height(60.dp),
                enabled = !isLoading
            ) {
                if(isLoading) CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black
                )
                Text(
                    buttonText,
                    color = if(isLoading) Color.Black else Color.White,
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
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = otpText,
        onValueChange = {
            if (it.length <= otpCount) {
                onOtpTextChange.invoke(it, it.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(otpCount) { index ->
                    val char = when {
                        index >= otpText.length -> ""
                        else -> otpText[index].toString()
                    }
                    val isFocused = otpText.length == index
                    Text(
                        modifier = Modifier
                            .width(50.dp)
                            .height(60.dp)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) Color.Black else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(top = 16.dp),
                        text = char,
                        fontSize = 22.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    if (index < otpCount - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun OTPBox(
    isLoading: Boolean,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
    onRequest: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    var isOtpComplete by remember { mutableStateOf(false) }

    var ticks by remember { mutableIntStateOf(90) }
    var isRunning by remember { mutableStateOf(true) }
    LaunchedEffect(isRunning) {
        if(isRunning) {
            while (ticks > 0) {
                delay(1000)
                ticks--
            }
            isRunning = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text(
            text = "Verify Your Email",
            color = Color.Black,
            fontSize = 25.sp,
            fontFamily = FontFamily(Font(R.font.riveruta_medium)),
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Enter the code sent to your email.",
            color = Color.Gray,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.riveruta_medium)),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            textAlign = TextAlign.Center
        )
        OtpTextField(
            otpText = otpValue,
            onOtpTextChange = { value, isComplete ->
                otpValue = value
                isOtpComplete = isComplete
            }
        )
        Spacer(Modifier.height(32.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = {
                    isRunning = !isRunning
                    ticks = 90
                    onRequest()
                },
                enabled = !isRunning
            ) {
                Text(
                    "Resend OTP",
                    fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if(isRunning) Color.Gray else Color.Black
                )
            }
            if(ticks > 0) {
                Text(
                    "${ticks}s",
                    fontFamily = FontFamily(Font(R.font.riveruta_medium)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
        Spacer(Modifier.height(32.dp))
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
                onClick = { onSubmit(otpValue) },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2c2c2c)),
                modifier = Modifier.height(60.dp),
                enabled = !isLoading && isOtpComplete
            ) {
                if (isLoading) CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
                Text(
                    "Verify",
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
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .size(50.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White)
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