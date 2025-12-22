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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
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

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    SellerDashboardTab.MY_ASSETS -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            if (myAssets.loadState.refresh is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier.fillParentMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            if (myAssets.itemCount == 0 && myAssets.loadState.refresh !is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier.fillParentMaxSize(),
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
                                    AssetCard(
                                        asset = asset,
                                        onClick = { /* Navigate to Details/Edit */ }
                                    )
                                }
                            }

                            if (myAssets.loadState.append is LoadState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
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

                    SellerDashboardTab.MY_PIVOTS -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "My Pivots Coming Soon",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Premium Sliding Tab Bar for Seller Dashboard
 */
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