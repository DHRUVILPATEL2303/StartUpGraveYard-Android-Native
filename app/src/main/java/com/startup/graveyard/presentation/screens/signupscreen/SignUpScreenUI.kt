package com.startup.graveyard.presentation.screens.signupscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.domain.models.CreateAccountModel
import com.startup.graveyard.presentation.viewmodels.AuthViewModel

@Composable
fun SignUpScreenUI(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    firebaseAuth: FirebaseAuth,
    onNavigateToLogin: () -> Unit = {},
    onSignUpSuccess: () -> Unit = {}
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val confirmPasswordVisible = remember { mutableStateOf(false) }

    val emailError = remember { mutableStateOf("") }
    val passwordError = remember { mutableStateOf("") }
    val confirmPasswordError = remember { mutableStateOf("") }
    val nameError = remember { mutableStateOf("") }

    val createAccountState by authViewModel.createAccountState.collectAsState()

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


    LaunchedEffect(createAccountState) {
        if (createAccountState.data != null) {
            onSignUpSuccess()
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
            Spacer(modifier = Modifier.height(40.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(primaryColor, secondaryColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💀",
                        fontSize = 32.sp,
                        color = colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "StartUp GraveYard",
                    style = typography.headlineSmall.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Where ideas find new life",
                    style = typography.bodyMedium.copy(color = secondaryTextColor),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
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
                    Text(
                        text = "Create Account",
                        style = typography.titleLarge.copy(
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 24.dp, top = 20.dp)
                    )

                    OutlinedTextField(
                        value = fullName.value,
                        onValueChange = {
                            fullName.value = it
                            nameError.value = ""
                        },
                        label = { Text("Full Name", color = secondaryTextColor) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Name",
                                tint = primaryColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = textColor,
                            cursorColor = primaryColor,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = borderColor,
                            focusedLabelColor = primaryColor,
                            backgroundColor = inputBackgroundColor,
                            unfocusedLabelColor = secondaryTextColor
                        ),
                        singleLine = true,
                        isError = nameError.value.isNotEmpty(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (nameError.value.isNotEmpty()) {
                        Text(
                            text = nameError.value,
                            color = errorColor,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email.value,
                        onValueChange = {
                            email.value = it
                            emailError.value = ""
                        },
                        label = { Text("Email", color = secondaryTextColor) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email",
                                tint = primaryColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
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
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = confirmPassword.value,
                        onValueChange = {
                            confirmPassword.value = it
                            confirmPasswordError.value = ""
                        },
                        label = { Text("Confirm Password", color = secondaryTextColor) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Confirm Password",
                                tint = primaryColor
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }) {
                                Icon(
                                    if (confirmPasswordVisible.value) Icons.Default.Visibility
                                    else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible.value) "Hide password" else "Show password",
                                    tint = secondaryTextColor
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
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
                        isError = confirmPasswordError.value.isNotEmpty(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    if (confirmPasswordError.value.isNotEmpty()) {
                        Text(
                            text = confirmPasswordError.value,
                            color = errorColor,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (createAccountState.error.isNotEmpty()) {
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
                                text = createAccountState.error,
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

                            if (fullName.value.trim().isEmpty()) {
                                nameError.value = "Name is required"
                                hasErrors = true
                            }

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
                            } else if (password.value.length < 6) {
                                passwordError.value = "Password must be at least 6 characters"
                                hasErrors = true
                            }

                            if (confirmPassword.value.isEmpty()) {
                                confirmPasswordError.value = "Please confirm your password"
                                hasErrors = true
                            } else if (password.value != confirmPassword.value) {
                                confirmPasswordError.value = "Passwords don't match"
                                hasErrors = true
                            }

                            if (!hasErrors) {
                                val createAccountModel = CreateAccountModel(
                                    email = email.value.trim(),
                                    password = password.value,
                                    name = fullName.value.trim(),
                                    profile_pic_url = "hello.png",
                                    role = "buyer",
                                    uuid = ""
                                )
                                authViewModel.createAccount(createAccountModel)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            disabledContainerColor = primaryColor.copy(alpha = 0.5f)
                        ),
                        enabled = !createAccountState.isLoading,
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        if (createAccountState.isLoading) {
                            CircularProgressIndicator(
                                color = colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Already have an account?  ",
                            color = secondaryTextColor,
                            fontSize = 14.sp
                        )
                        androidx.compose.material.TextButton(onClick = onNavigateToLogin) {
                            Text(
                                text = "Sign In",
                                color = primaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}