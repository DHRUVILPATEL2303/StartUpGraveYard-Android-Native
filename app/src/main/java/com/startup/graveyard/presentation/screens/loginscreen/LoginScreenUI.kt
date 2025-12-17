package com.startup.graveyard.presentation.screens.loginscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.startup.graveyard.presentation.viewmodels.AuthViewModel

@Composable
fun LoginScreenUI(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigateToSignUp: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val emailError = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf("") }

    val loginState by authViewModel.loginState.collectAsState()

    val colorScheme = androidx.compose.material3.MaterialTheme.colorScheme
    val typography = androidx.compose.material3.MaterialTheme.typography
    val primaryColor = colorScheme.primary
    val secondaryColor = colorScheme.secondary
    val backgroundColor = colorScheme.background
    val surfaceColor = colorScheme.surface
    val textColor = colorScheme.onBackground
    val secondaryTextColor = colorScheme.onSurface.copy(alpha = 0.7f)
    val errorColor = colorScheme.error
    val borderColor = colorScheme.outline.copy(alpha = 0.3f)
    val inputBackgroundColor = colorScheme.surfaceVariant.copy(alpha = 0.25f)

    LaunchedEffect(loginState) {
        if (loginState.data != null) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
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
                        color = colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Welcome Back",
                    style = typography.headlineMedium.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Sign in to StartUp GraveYard",
                    style = typography.bodyMedium.copy(color = secondaryTextColor),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                backgroundColor = surfaceColor,
                elevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = {
                            email.value = it
                            emailError.value = ""
                        },
                        label = { Text("Email", color = secondaryTextColor) },
                        placeholder = { Text("Enter your email", color = secondaryTextColor.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
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
                            unfocusedBorderColor = borderColor,
                            focusedLabelColor = primaryColor,
                            backgroundColor = inputBackgroundColor,
                            unfocusedLabelColor = secondaryTextColor
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        isError = emailError.value.isNotEmpty(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (emailError.value.isNotEmpty()) {
                        Text(
                            text = emailError.value,
                            color = errorColor,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password.value,
                        onValueChange = {
                            password.value = it
                            passwordError.value = ""
                        },
                        label = { Text("Password", color = secondaryTextColor) },
                        placeholder = { Text("Enter your password", color = secondaryTextColor.copy(alpha = 0.5f)) },
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
                                    if (passwordVisible.value) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible.value) "Hide password" else "Show password",
                                    tint = secondaryTextColor
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
                            unfocusedBorderColor = borderColor,
                            focusedLabelColor = primaryColor,
                            backgroundColor = inputBackgroundColor,
                            unfocusedLabelColor = secondaryTextColor
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        isError = passwordError.value.isNotEmpty(),
                        shape = RoundedCornerShape(16.dp)
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

                    if (loginState.error.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            backgroundColor = errorColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, errorColor.copy(alpha = 0.3f)),
                            elevation = 0.dp
                        ) {
                            Text(
                                text = loginState.error,
                                color = errorColor,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            var hasErrors = false

                            if (email.value.trim().isEmpty()) {
                                emailError.value = "Email is required"
                                hasErrors = true
                            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                emailError.value = "Please enter a valid email"
                                hasErrors = true
                            }

                            if (password.value.isEmpty()) {
                                passwordError.value = "Password is required"
                                hasErrors = true
                            }

                            if (!hasErrors) {
                                authViewModel.loginUser(
                                    email = email.value.trim(),
                                    password = password.value
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            disabledContainerColor = primaryColor.copy(alpha = 0.5f)
                        ),
                        enabled = !loginState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        if (loginState.isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    color = colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Signing In...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onPrimary
                                )
                            }
                        } else {
                            Text(
                                "Sign In",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = borderColor
                        )
                        Text(
                            text = "  OR  ",
                            color = secondaryTextColor,
                            fontSize = 14.sp
                        )
                        Divider(
                            modifier = Modifier.weight(1f),
                            color = borderColor
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account?  ",
                            color = secondaryTextColor,
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

            Text(
                text = "By signing in, you agree to our Terms of Service\nand Privacy Policy",
                color = secondaryTextColor.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}