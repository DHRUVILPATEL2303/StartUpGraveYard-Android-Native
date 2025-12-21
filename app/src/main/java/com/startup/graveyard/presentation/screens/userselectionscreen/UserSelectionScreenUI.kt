@file:OptIn(ExperimentalMaterial3Api::class)

package com.startup.graveyard.presentation.screens.userselectionscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.startup.graveyard.presentation.navigation.Routes
import com.startup.graveyard.presentation.viewmodels.AuthViewModel




import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*


@Composable
fun UserSelectionScreenUI(
    onBuyerSelected: () -> Unit,
    onSellerSelected: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val accountState by authViewModel.accountState.collectAsState()
    val verificationState by authViewModel.checkVerificationState.collectAsState()

    val isVerified = verificationState.data == true
    var showVerifyDialog by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    LaunchedEffect(Unit) {
        authViewModel.getUserAccountDetails()
        authViewModel.checkVerification()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Welcome",
                            style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = accountState.data?.data?.name.orEmpty(),
                            style = typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = colorScheme.primary
                        )

                        if (isVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .clickable {
                                navController.navigate(Routes.AccountScreen)
                            }
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Choose how you want to continue",
                style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You can switch roles later from profile",
                style = typography.bodyMedium.copy(color = colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            RoleCard(
                icon = Icons.Default.ShoppingCart,
                title = "Buyer",
                containerColor = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer,
                textColor = colorScheme.onPrimaryContainer,
                arrowTint = colorScheme.onSurfaceVariant,
                onClick = {
                    if (isVerified) {
                        onBuyerSelected()
                    } else {
                        showVerifyDialog = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            RoleCard(
                icon = Icons.Default.Store,
                title = "Seller",
                containerColor = colorScheme.secondaryContainer,
                iconTint = colorScheme.onSecondaryContainer,
                textColor = colorScheme.onSecondaryContainer,
                arrowTint = colorScheme.onSurfaceVariant,
                onClick = {
                    if (isVerified) {
                        onSellerSelected()
                    } else {
                        showVerifyDialog = true
                    }
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (!isVerified) {
                TextButton(
                    onClick = {
                        navController.navigate(Routes.VerificationScreen)
                    }
                ) {
                    Text("Verify Account")
                }
            }
        }
    }

    if (showVerifyDialog) {
        VerifyAccountDialog(
            onDismiss = { showVerifyDialog = false },
            onVerifyClick = {
                showVerifyDialog = false
                navController.navigate(Routes.VerificationScreen)
            }
        )
    }
}

@Composable
fun RoleCard(
    icon: ImageVector,
    title: String,
    containerColor: Color,
    onClick: () -> Unit,
    iconTint: Color,
    textColor: Color,
    arrowTint: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(30.dp),
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = arrowTint
            )
        }
    }
}

@Composable
fun VerifyAccountDialog(
    onDismiss: () -> Unit,
    onVerifyClick: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Account Not Verified")
        },
        text = {
            Text("Please verify your account to continue.")
        },
        confirmButton = {
            TextButton(onClick = onVerifyClick) {
                Text("Verify Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}