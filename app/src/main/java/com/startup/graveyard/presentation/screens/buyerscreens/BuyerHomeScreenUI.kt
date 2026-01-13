package com.startup.graveyard.presentation.screens.buyerscreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.startup.graveyard.presentation.navigation.Routes
import com.startup.graveyard.presentation.screens.buyerscreens.components.AssetCard
import com.startup.graveyard.presentation.screens.buyerscreens.components.ErrorItem
import com.startup.graveyard.presentation.screens.buyerscreens.components.ErrorScreen
import com.startup.graveyard.presentation.viewmodels.assets.AssetViewModel
import kotlinx.coroutines.launch

enum class BuyerHomeTab {
    ASSETS,
    PIVOT
}

enum class AssetTypeFilter(val value: String?) {
    ALL(null),
    CODEBASE("codebase"),
    DOMAIN("domain"),
    PRODUCT("product"),
    DATA("data"),
    RESEARCH("research")
}

@Composable
fun BuyerHomeScreenUI(
    assetViewModel: AssetViewModel,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(BuyerHomeTab.ASSETS) }
    var selectedFilter by remember { mutableStateOf(AssetTypeFilter.ALL) }

    LaunchedEffect(selectedFilter) {
        assetViewModel.filterByAssetType(selectedFilter.value)
    }

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

            if (selectedTab == BuyerHomeTab.ASSETS) {
                Spacer(Modifier.height(12.dp))

                AssetFilterBar(
                    selected = selectedFilter,
                    onSelected = { selectedFilter = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    BuyerHomeTab.ASSETS -> BuyerAssetsContent(
                        viewModel = assetViewModel,
                        onAssetClick = { id ->
                            navController.navigate(
                                Routes.BuyerSpecificAssetScreen(id.toString())
                            )
                        },
                        onChatClick = { receiverId ->
                            navController.navigate(Routes.ChatScreen(receiverId))
                        }
                    )

                    BuyerHomeTab.PIVOT -> BuyerPivotContent(
                        onStartupClick = {}
                    )
                }
            }
        }
    }
}

@Composable
fun SlidingSegmentedControl(
    selectedTab: BuyerHomeTab,
    onTabSelected: (BuyerHomeTab) -> Unit
) {
    val density = LocalDensity.current
    var tabWidth by remember { mutableStateOf(0.dp) }

    val indicatorOffset by animateDpAsState(
        targetValue = if (selectedTab == BuyerHomeTab.ASSETS) 0.dp else tabWidth,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "indicator"
    )

    val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val indicatorColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(56.dp)
            .clip(CircleShape)
            .background(containerColor)
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
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                .background(indicatorColor, CircleShape)
        )

        Row(Modifier.fillMaxSize()) {
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
    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
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
            color = textColor
        )
    }
}