package com.startup.graveyard.presentation.screens.loginscreen

import androidx. compose.foundation.background
import androidx.compose.foundation. layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation. verticalScroll
import androidx. compose.material.*
import androidx.compose.material. icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material. icons.filled.Lock
import androidx.compose.material.icons.filled. Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx. compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose. ui.graphics. Brush
import androidx.compose.ui.graphics.Color
import androidx. compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx. compose.ui.text.input. PasswordVisualTransformation
import androidx.compose.ui.text. input. VisualTransformation
import androidx. compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose. ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.startup.graveyard.presentation.viewmodels.AuthViewModel

@Composable
fun LoginScreenUI(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    // Form state
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    // Validation states
    val emailError = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf("") }

    // Auth state
    val loginState by authViewModel.loginState. collectAsState()

    // Colors for startup theme (matching signup screen)
    val primaryColor = Color(0xFF6C5CE7)
    val secondaryColor = Color(0xFFA29BFE)
    val backgroundColor = Color(0xFF1E1E2E)
    val surfaceColor = Color(0xFF2D2D42)
    val textColor = Color.White
    val errorColor = Color(0xFFFF6B6B)

    // Handle auth state changes
    LaunchedEffect(loginState) {
        if (loginState. data != null) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(backgroundColor, Color(0xFF181828))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier. height(60.dp))

            // App Logo/Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 50.dp)
            ) {
                // Logo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(primaryColor, secondaryColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💀",
                        fontSize = 40.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sign in to StartUp GraveYard",
                    fontSize = 16.sp,
                    color = textColor. copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier. padding(top = 8.dp)
                )
            }

            // Login Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                backgroundColor = surfaceColor,
                elevation = 12.dp
            ) {
                Column(
                    modifier = Modifier. padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = {
                            email.value = it
                            emailError.value = ""
                        },
                        label = { Text("Email", color = textColor. copy(alpha = 0.7f)) },
                        placeholder = { Text("Enter your email", color = textColor. copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default. Email,
                                contentDescription = "Email",
                                tint = primaryColor
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = textColor,
                            cursorColor = primaryColor,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textColor.copy(alpha = 0.3f),
                            focusedLabelColor = primaryColor,
                            backgroundColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        isError = emailError.value.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (emailError.value.isNotEmpty()) {
                        Text(
                            text = emailError. value,
                            color = errorColor,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier. height(20.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = {
                            password.value = it
                            passwordError.value = ""
                        },
                        label = { Text("Password", color = textColor.copy(alpha = 0.7f)) },
                        placeholder = { Text("Enter your password", color = textColor.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = primaryColor
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                                Icon(
                                    if (passwordVisible. value) Icons.Default.Visibility
                                    else Icons.Default. VisibilityOff,
                                    contentDescription = if (passwordVisible.value) "Hide password" else "Show password",
                                    tint = textColor. copy(alpha = 0.7f)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = textColor,
                            cursorColor = primaryColor,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = textColor.copy(alpha = 0.3f),
                            focusedLabelColor = primaryColor,
                            backgroundColor = Color. Transparent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        isError = passwordError.value.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (passwordError.value.isNotEmpty()) {
                        Text(
                            text = passwordError.value,
                            color = errorColor,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    // Forgot Password Link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onForgotPassword) {
                            Text(
                                text = "Forgot Password?",
                                color = primaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Error message from API
                    if (loginState.error.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            backgroundColor = errorColor. copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, errorColor. copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = loginState.error,
                                color = errorColor,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier. padding(12.dp)
                            )
                        }
                    }

                    // Login Button
                    Button(
                        onClick = {
                            // Validate inputs
                            var hasErrors = false

                            if (email.value.trim().isEmpty()) {
                                emailError.value = "Email is required"
                                hasErrors = true
                            } else if (! android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                emailError.value = "Please enter a valid email"
                                hasErrors = true
                            }

                            if (password.value.isEmpty()) {
                                passwordError. value = "Password is required"
                                hasErrors = true
                            }

                            if (! hasErrors) {
                                authViewModel.loginUser(
                                    email = email.value.trim(),
                                    password = password.value
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            disabledContainerColor = primaryColor.copy(alpha = 0.5f)
                        ),
                        enabled = !loginState.isLoading,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (loginState. isLoading) {
                            Row(
                                horizontalArrangement = Arrangement. Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Signing In...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                "Sign In",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divider with "OR"
                    Row(
                        modifier = Modifier. fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = textColor.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "  OR  ",
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = textColor.copy(alpha = 0.3f)
                        )
                    }

                    Spacer(modifier = Modifier. height(24.dp))

                    // Sign Up Link
                    Row(
                        horizontalArrangement = Arrangement. Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?  ",
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 16.sp
                        )
                        TextButton(onClick = onNavigateToSignUp) {
                            Text(
                                text = "Sign Up",
                                color = primaryColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Footer text
            Text(
                text = "By signing in, you agree to our Terms of Service\nand Privacy Policy",
                color = textColor.copy(alpha = 0.5f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}