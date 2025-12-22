@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.startup.graveyard.presentation.screens.accountscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.startup.graveyard.domain.models.UpdateUserAccountRequestModel
import com.startup.graveyard.presentation.navigation.Routes
import com.startup.graveyard.presentation.viewmodels.AuthViewModel

@Composable
fun AccountScreenUI(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val accountState by viewModel.accountState.collectAsState()
    val updateState by viewModel.updateAccountState.collectAsState()
    val deleteAccountState by viewModel.deleteAccountState.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isEditMode by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAccountDeleteDialog by remember { mutableStateOf(false) }

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    LaunchedEffect(accountState.data) {
        accountState.data?.data?.name?.let { editedName = it }
    }

    LaunchedEffect(Unit) {
        viewModel.getUserAccountDetails()
    }

    LaunchedEffect(deleteAccountState.data) {
        if (deleteAccountState.data != null && !deleteAccountState.isLoading) {
            viewModel.logOut()
            navController.navigate(Routes.LoginScreen) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Account",
                        style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (navController.previousBackStackEntry != null) {
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIos,
                            contentDescription = "Back",
                            tint = colorScheme.onSurface
                        )
                    }
                }
            )
        },
        containerColor = colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = 24.dp, horizontal = 20.dp)
            ) {
                if (accountState.isLoading) {
                    item {
                        ContainedLoadingIndicator(
                            modifier = Modifier.size(96.dp)
                        )
                    }
                }

                if (accountState.error.isNotEmpty()) {
                    item {
                        ErrorCard(message = accountState.error)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                accountState.data?.let { response ->
                    val user = response.data

                    item {
                        ProfileSection(
                            isEditMode = isEditMode,
                            name = editedName,
                            onNameChange = { editedName = it },
                            profilePic = user.profile_pic_url,
                            selectedImageUri = selectedImageUri,
                            onImageClick = { imagePickerLauncher.launch("image/*") },
                            onEditClick = { isEditMode = !isEditMode }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    item { InfoCard(title = "Email", value = user.email) }
                    item { InfoCard(title = "Account Created", value = user.created_at) }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    if (updateState.error.isNotEmpty()) {
                        item {
                            ErrorCard(message = updateState.error)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (updateState.data != null && !updateState.isLoading) {
                        item {
                            SuccessCard(message = "Profile updated successfully")
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    if (deleteAccountState.error.isNotEmpty()) {
                        item {
                            ErrorCard(message = deleteAccountState.error)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    item {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditMode && !updateState.isLoading && !deleteAccountState.isLoading,
                            onClick = {
                                viewModel.updateAccount(
                                    UpdateUserAccountRequestModel(
                                        name = editedName,
                                        role = user.role,
                                        uuid = user.uuid,
                                        profile_pic_url = selectedImageUri?.toString()
                                            ?: user.profile_pic_url
                                    )
                                )

                                viewModel.getUserAccountDetails(forceRefresh = true)
                                isEditMode = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                disabledContainerColor = colorScheme.primary.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (updateState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    "Save Changes",
                                    style = typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error),
                            onClick = { showLogoutDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            enabled = !deleteAccountState.isLoading
                        ) {
                            Text(
                                text = "Log Out",
                                style = typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = colorScheme.onError
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error.copy(alpha = 0.9f)),
                            onClick = { showAccountDeleteDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            enabled = !deleteAccountState.isLoading
                        ) {
                            Text(
                                text = "Delete Account",
                                style = typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = colorScheme.onError
                            )
                        }
                    }
                }
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.logOut()
                            navController.navigate(Routes.LoginScreen) {
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Text("Yes", color = colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel", color = colorScheme.onSurfaceVariant)
                        }
                    },
                    title = { Text("Log out") },
                    text = { Text("Are you sure you want to log out?") }
                )
            }

            if (showAccountDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showAccountDeleteDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showAccountDeleteDialog = false
                            viewModel.deleteAccount()
                        }) {
                            Text("Delete", color = colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAccountDeleteDialog = false }) {
                            Text("Cancel", color = colorScheme.onSurfaceVariant)
                        }
                    },
                    title = { Text("Delete Account") },
                    text = { Text("This action cannot be undone. Do you want to delete your account?") }
                )
            }

            if (deleteAccountState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.scrim.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    ContainedLoadingIndicator(
                        modifier = Modifier.size(120.dp)
                    )
                }
            }

            if (deleteAccountState.data != null && !deleteAccountState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.scrim.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    SuccessCard(message = "Account deleted. Redirecting to login...")
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(
    isEditMode: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    profilePic: String,
    selectedImageUri: Uri?,
    onImageClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    color = colorScheme.primary,
                    shape = CircleShape
                )
                .clickable { onImageClick() },
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedImageUri != null -> {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(102.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                profilePic.isNotBlank() -> {
                    AsyncImage(
                        model = profilePic,
                        contentDescription = null,
                        modifier = Modifier
                            .size(102.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isEditMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    singleLine = true,
                    modifier = Modifier.width(220.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            } else {
                Text(
                    text = name,
                    style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Name",
                    tint = colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.error.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder(enabled = true)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            color = colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SuccessCard(message: String) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.primary.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder(enabled = true)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            color = colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}