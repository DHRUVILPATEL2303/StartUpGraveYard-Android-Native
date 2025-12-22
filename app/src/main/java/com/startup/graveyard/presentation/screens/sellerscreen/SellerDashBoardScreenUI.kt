@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.startup.graveyard.presentation.screens.sellerscreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.presentation.screens.buyerscreens.components.AssetCard
import com.startup.graveyard.presentation.viewmodels.assets.AssetViewModel

enum class SellerDashboardTab {
    MY_ASSETS,
    MY_PIVOTS
}

@Composable
fun SellerDashboardScreenUI(
    assetViewModel: AssetViewModel
) {
    var selectedTab by remember { mutableStateOf(SellerDashboardTab.MY_ASSETS) }

    val myAssets = assetViewModel.specificUserAssetFlow.collectAsLazyPagingItems()

    val haptic = LocalHapticFeedback.current
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    val isRefreshing = myAssets.loadState.refresh is LoadState.Loading
    val pullRefreshState = rememberPullToRefreshState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var assetToDeleteId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SellerDashboardSegmentedControl(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    SellerDashboardTab.MY_ASSETS -> {

                        PullToRefreshBox(
                            isRefreshing = isRefreshing,
                            state = pullRefreshState,
                            onRefresh = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                myAssets.refresh()
                            },
                            modifier = Modifier.fillMaxSize(),
                            indicator = {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .graphicsLayer {
                                            alpha = if (isRefreshing) 1f else pullRefreshState.distanceFraction.coerceIn(0f, 1f)
                                            scaleX = if (isRefreshing) 1f else pullRefreshState.distanceFraction.coerceIn(0.5f, 1f)
                                            scaleY = if (isRefreshing) 1f else pullRefreshState.distanceFraction.coerceIn(0.5f, 1f)
                                        }
                                        .padding(top = 16.dp)
                                ) {
                                    if (isRefreshing || pullRefreshState.distanceFraction > 0.1f) {
                                        LoadingIndicator(
                                            modifier = Modifier.size(50.dp), // Adjusted size for better fit
                                            color = Color(255, 110, 64, 255),
                                            polygons = LoadingIndicatorDefaults.IndeterminateIndicatorPolygons
                                        )
                                    }
                                }
                            }
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {

                                if (myAssets.itemCount == 0 && myAssets.loadState.refresh !is LoadState.Loading) {
                                    item {
                                        Box(
                                            Modifier.fillParentMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No assets listed yet.",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                items(myAssets.itemCount) { index ->
                                    val asset = myAssets[index]
                                    if (asset != null) {
                                        SellerAssetItem(
                                            asset = asset,
                                            onAssetClick = { /* Navigate to Details */ },
                                            onEditClick = { },
                                            onDeleteClick = {
                                                assetToDeleteId = asset.id
                                                showDeleteDialog = true
                                            }
                                        )
                                    }
                                }

                                if (myAssets.loadState.append is LoadState.Loading) {
                                    item {
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    SellerDashboardTab.MY_PIVOTS -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "My Pivots Coming Soon",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (showDeleteDialog && assetToDeleteId != null) {
            DeleteConfirmationDialog(
                onConfirm = {
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

/**
 * A Wrapper component that adds "Management Controls" (Edit/Delete)
 * below the standard AssetCard.
 */
@Composable
fun SellerAssetItem(
    asset: Asset,
    onAssetClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column {
        AssetCard(
            asset = asset,
            onClick = onAssetClick
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-12).dp)
                .shadow(4.dp, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                text = "Update",
                icon = Icons.Default.Edit,
                color = MaterialTheme.colorScheme.primary,
                onClick = onEditClick
            )

            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            )

            ActionButton(
                text = "Delete",
                icon = Icons.Outlined.Delete,
                color = MaterialTheme.colorScheme.error,
                onClick = onDeleteClick
            )
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = color)
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Asset?") },
        text = { Text("Are you sure you want to remove this listing? This action cannot be undone.") },
        icon = {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

@Composable
fun SellerDashboardSegmentedControl(
    selectedTab: SellerDashboardTab,
    onTabSelected: (SellerDashboardTab) -> Unit
) {
    val density = LocalDensity.current
    var tabWidth by remember { mutableStateOf(0.dp) }

    val indicatorOffset by animateDpAsState(
        targetValue = if (selectedTab == SellerDashboardTab.MY_ASSETS) 0.dp else tabWidth,
        animationSpec = tween(280, easing = FastOutSlowInEasing),
        label = "indicator"
    )
    val isLight = !isSystemInDarkTheme()

    val selectedPill = Color(36, 48, 70)
    val containerBg = if (isLight) Color(245, 247, 250) else Color(28, 30, 35)
    val borderColor = if (isLight) Color(200, 205, 215) else Color(70, 75, 85)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(52.dp)
            .clip(CircleShape)
            .background(containerBg)
            .border(1.5.dp, borderColor, CircleShape)
            .onGloballyPositioned {
                with(density) { tabWidth = it.size.width.toDp() / 2 }
            }
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .padding(4.dp)
                .clip(CircleShape)
                .background(selectedPill)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = selectedPill.copy(alpha = 0.35f),
                    spotColor = selectedPill.copy(alpha = 0.25f)
                )
        )

        Row(Modifier.fillMaxSize()) {
            SellerTabItem(
                text = "My Assets",
                isSelected = selectedTab == SellerDashboardTab.MY_ASSETS,
                modifier = Modifier.weight(1f),
                selectedTextColor = Color.White,
                unselectedTextColor = if (isLight) Color(90, 95, 110) else Color(170, 175, 185),
                onClick = { onTabSelected(SellerDashboardTab.MY_ASSETS) }
            )
            SellerTabItem(
                text = "My Pivots",
                isSelected = selectedTab == SellerDashboardTab.MY_PIVOTS,
                modifier = Modifier.weight(1f),
                selectedTextColor = Color.White,
                unselectedTextColor = if (isLight) Color(90, 95, 110) else Color(170, 175, 185),
                onClick = { onTabSelected(SellerDashboardTab.MY_PIVOTS) }
            )
        }
    }
}

@Composable
fun SellerTabItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    selectedTextColor: Color,
    unselectedTextColor: Color,
    onClick: () -> Unit
) {
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
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            style = MaterialTheme.typography.titleSmall,
            color = if (isSelected) selectedTextColor else unselectedTextColor
        )
    }
}