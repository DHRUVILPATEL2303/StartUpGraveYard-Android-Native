@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.startup.graveyard.presentation.screens.sellerscreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.domain.models.CreateAssetRequestModel
import com.startup.graveyard.presentation.viewmodels.assets.AssetViewModel
import com.startup.graveyard.utils.uploadImageToFirebase
import kotlinx.coroutines.launch

enum class SellerTab {
    ASSET,
    PIVOT
}

@Composable
fun SellerAddScreenUI(
    assetViewModel: AssetViewModel,
    firebaseAuth: FirebaseAuth
) {
    var selectedTab by remember { mutableStateOf(SellerTab.ASSET) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 80.dp)
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SellerSegmentedControl(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    SellerTab.ASSET -> CreateAssetForm(
                        assetViewModel = assetViewModel,
                        firebaseAuth = firebaseAuth,
                        snackbarHostState = snackbarHostState
                    )
                    SellerTab.PIVOT -> PivotPlaceholder()
                }
            }
        }
    }
}

@Composable
fun CreateAssetForm(
    assetViewModel: AssetViewModel,
    firebaseAuth: FirebaseAuth,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val state by assetViewModel.createAssetState.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isNegotiable by remember { mutableStateOf(false) }
    var selectedAssetType by remember { mutableStateOf("codebase") } // Default

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }
    var isImageUploading by remember { mutableStateOf(false) }

    LaunchedEffect(state.data) {
        if (state.data != null) {
            keyboardController?.hide()
            snackbarHostState.showSnackbar("Asset listed successfully!")

            title = ""; description = ""; price = ""; imageUri = null; uploadedImageUrl = null; selectedAssetType = "codebase"
            assetViewModel.resetCreateAssetState()
        }
    }

    LaunchedEffect(state.error) {
        if (state.error.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.error, withDismissAction = true)
            assetViewModel.resetCreateAssetState()
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            isImageUploading = true
            uploadImageToFirebase(firebaseAuth.uid.toString(), context, uri) { url ->
                isImageUploading = false
                uploadedImageUrl = url
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {


        item {
            AssetTypeSelector(
                selectedType = selectedAssetType,
                onTypeSelected = { selectedAssetType = it }
            )
        }

        item {
            CustomTextField(
                value = title,
                onValueChange = { title = it },
                label = "Asset Title",
                placeholder = "e.g., E-commerce App Codebase",
                icon = Icons.Default.Title
            )
        }

        item {
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                placeholder = "Tech stack, key features, reason for selling...",
                icon = Icons.Default.Description,
                singleLine = false,
                modifier = Modifier.height(140.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextField(
                    value = price,
                    onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                    label = "Price",
                    icon = Icons.Default.CurrencyRupee,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1.3f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                        .clickable { isNegotiable = !isNegotiable }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Negotiable",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Switch(
                            checked = isNegotiable,
                            onCheckedChange = { isNegotiable = it },
                            modifier = Modifier.scale(0.8f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        )
                    }
                }
            }
        }

        item {
            DashedUploadBox(
                uri = imageUri,
                isUploading = isImageUploading,
                isUploaded = uploadedImageUrl != null,
                onClick = { launcher.launch("image/*") }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (uploadedImageUrl != null) {
                        assetViewModel.createAsset(
                            CreateAssetRequestModel(
                                asset_type = selectedAssetType,
                                title = title,
                                description = description,
                                price = price.toIntOrNull() ?: 0,
                                image_url = uploadedImageUrl!!,
                                is_negotiable = isNegotiable,
                                is_sold = false,
                                user_uuid = ""
                            )
                        )
                    } else if (imageUri == null) {
                        scope.launch { snackbarHostState.showSnackbar("Please add a cover image") }
                    } else if (isImageUploading) {
                        scope.launch { snackbarHostState.showSnackbar("Waiting for image upload...") }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = if (state.isLoading) 0.dp else 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                shape = RoundedCornerShape(16.dp),
                enabled = !state.isLoading && !isImageUploading && title.isNotEmpty() && price.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                )
            ) {
                if (state.isLoading) {
                  LoadingIndicator(
                      modifier = Modifier.size(50.dp),
                      color = Color(255, 110, 64, 255)
                  )
                } else {
                    Text("Publish Asset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AssetTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val types = listOf(
        "Codebase" to "codebase",
        "Domain" to "domain",
        "Product" to "product",
        "Data" to "data",
        "Research" to "research",
        "Other" to "other"
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Asset Type",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            types.forEach { (label, value) ->
                val isSelected = selectedType == value

                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                    label = "bgColor"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    label = "textColor"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    label = "borderColor"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTypeSelected(value) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun DashedUploadBox(
    uri: Uri?,
    isUploading: Boolean,
    isUploaded: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isUploaded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val bgColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .dashedBorder(
                color = borderColor,
                strokeWidth = 2.dp,
                cornerRadius = 16.dp,
                dashLength = 10.dp,
                gapLength = 10.dp
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isUploading) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LoadingIndicator(
                    modifier = Modifier.size(50.dp),
                    color = Color(255, 110, 64, 255)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Uploading...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (uri != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Image Added",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Tap to replace",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Upload Cover Image",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "PNG, JPG up to 5MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    icon: ImageVector,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@Composable
fun SellerSegmentedControl(
    selectedTab: SellerTab,
    onTabSelected: (SellerTab) -> Unit
) {
    val density = LocalDensity.current
    var tabWidth by remember { mutableStateOf(0.dp) }

    val indicatorOffset by animateDpAsState(
        targetValue = if (selectedTab == SellerTab.ASSET) 0.dp else tabWidth,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "indicator"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .onGloballyPositioned {
                with(density) { tabWidth = it.size.width.toDp() / 2 }
            }
    ) {
        // Floating Pill
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .padding(4.dp)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        )

        Row(Modifier.fillMaxSize()) {
            SellerTabItem(
                text = "Sell Asset",
                isSelected = selectedTab == SellerTab.ASSET,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(SellerTab.ASSET) }
            )
            SellerTabItem(
                text = "Sell Pivot",
                isSelected = selectedTab == SellerTab.PIVOT,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(SellerTab.PIVOT) }
            )
        }
    }
}

@Composable
fun SellerTabItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val targetColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val color by animateColorAsState(targetValue = targetColor, label = "textColor")

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = color,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun PivotPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pivot Selling Coming Soon",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp,
    cornerRadius: Dp,
    dashLength: Dp,
    gapLength: Dp
) = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength.toPx(), gapLength.toPx()), 0f)
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(cornerRadius.toPx())
    )
}

fun Modifier.scale(scale: Float) = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)