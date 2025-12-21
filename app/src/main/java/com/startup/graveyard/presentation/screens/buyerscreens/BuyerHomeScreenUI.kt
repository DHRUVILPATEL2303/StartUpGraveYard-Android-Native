package com.startup.graveyard.presentation.screens.buyerscreens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
    assetViewModel: AssetViewModel
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
            BuyerTabItem(
                text = "Assets",
                isSelected = selectedTab == BuyerHomeTab.ASSETS,
                modifier = Modifier.weight(1f),
                selectedTextColor = Color.White,
                unselectedTextColor = if (isLight) Color(90, 95, 110) else Color(170, 175, 185),
                onClick = { onTabSelected(BuyerHomeTab.ASSETS) }
            )
            BuyerTabItem(
                text = "Pivot",
                isSelected = selectedTab == BuyerHomeTab.PIVOT,
                modifier = Modifier.weight(1f),
                selectedTextColor = Color.White,
                unselectedTextColor = if (isLight) Color(90, 95, 110) else Color(170, 175, 185),
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