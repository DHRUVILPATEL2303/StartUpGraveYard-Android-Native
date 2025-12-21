package com.startup.graveyard.presentation.screens.buyerscreens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.startup.graveyard.presentation.viewmodels.AssetViewModel

enum class BuyerHomeTab {
    ASSETS,
    PIVOT
}

@Composable
fun BuyerHomeScreenUI(
    assetViewModel: AssetViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(BuyerHomeTab.ASSETS) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SlidingSegmentedControl(
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
                    BuyerHomeTab.ASSETS -> BuyerAssetsContent(
                        viewModel = assetViewModel,
                        onAssetClick = { }
                    )
                    BuyerHomeTab.PIVOT -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Pivot Content Coming Soon", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

/**
 * A premium-looking tab bar with a sliding background pill animation.
 */
@Composable
fun SlidingSegmentedControl(
    selectedTab: BuyerHomeTab,
    onTabSelected: (BuyerHomeTab) -> Unit
) {
    val density = LocalDensity.current
    var tabWidth by remember { mutableStateOf(0.dp) }

    val indicatorOffset by animateDpAsState(
        targetValue = if (selectedTab == BuyerHomeTab.ASSETS) 0.dp else tabWidth,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "indicator"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(50.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .onGloballyPositioned { coordinates ->
                with(density) {
                    tabWidth = coordinates.size.width.toDp() / 2
                }
            }
    ) {
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                .zIndex(0f)
        )

        // 2. The Text Labels (Foreground)
        Row(modifier = Modifier.fillMaxSize()) {
            BuyerTabItem(
                text = "Assets",
                isSelected = selectedTab == BuyerHomeTab.ASSETS,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(BuyerHomeTab.ASSETS) }
            )
            BuyerTabItem(
                text = "Pivot",
                isSelected = selectedTab == BuyerHomeTab.PIVOT,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(BuyerHomeTab.PIVOT) }
            )
        }
    }
}

@Composable
fun BuyerTabItem(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
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
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}