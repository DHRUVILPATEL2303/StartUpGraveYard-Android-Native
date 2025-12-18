package com.startup.graveyard.presentation.screens.verificationscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.domain.models.VerifyOTPRequestModel
import com.startup.graveyard.presentation.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreenUI(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
    firebaseAuth: FirebaseAuth,
) {
    val sendOtpState by authViewModel.sendOtpVerificationState.collectAsState()
    val verifyOtpState by authViewModel.verifyOtpState.collectAsState()

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val otp = remember { mutableStateOf("") }
    val otpError = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authViewModel.sendOtpForVerificationRequest()
    }

    LaunchedEffect(verifyOtpState.data) {
        if (verifyOtpState.data != null) {
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(padding)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = CardDefaults.shape
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Verify your email",
                        style = typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter the verification code sent to your email.",
                        style = typography.bodyMedium.copy(color = colorScheme.onSurfaceVariant),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = otp.value,
                        onValueChange = {
                            val digits = it.filter(Char::isDigit).take(6)
                            otp.value = digits
                            otpError.value = ""
                        },
                        label = { Text("6-digit code") },
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        keyboardOptions = KeyboardOptions.Default,
                        isError = otpError.value.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (otpError.value.isNotEmpty()) {
                        Text(
                            text = otpError.value,
                            color = colorScheme.error,
                            style = typography.labelMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }

                    if (sendOtpState.error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = sendOtpState.error,
                            color = colorScheme.error,
                            style = typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    if (verifyOtpState.error.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = verifyOtpState.error,
                            color = colorScheme.error,
                            style = typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (otp.value.length < 6) {
                                otpError.value = "Enter the 6-digit code"
                            } else {
                                if (firebaseAuth.currentUser != null) {

                                    authViewModel.verifyOtp(
                                        VerifyOTPRequestModel(
                                            code = otp.value,
                                            email = firebaseAuth.currentUser!!.email.toString()
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !verifyOtpState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            disabledContainerColor = colorScheme.primary.copy(alpha = 0.5f)
                        )
                    ) {
                        if (verifyOtpState.isLoading) {
                            CircularProgressIndicator(
                                color = colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Verify",
                                style = typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { authViewModel.sendOtpForVerificationRequest() },
                        enabled = !sendOtpState.isLoading
                    ) {
                        if (sendOtpState.isLoading) {
                            CircularProgressIndicator(
                                color = colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Resend code",
                                style = typography.labelLarge.copy(color = colorScheme.primary)
                            )
                        }
                    }

                    if (sendOtpState.data != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Verification code sent.",
                            style = typography.bodySmall.copy(color = colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
}