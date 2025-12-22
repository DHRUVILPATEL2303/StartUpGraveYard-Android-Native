@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
package com.startup.graveyard.presentation.screens.buyerscreens
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.startup.graveyard.presentation.screens.buyerscreens.components.AssetCard
import com.startup.graveyard.presentation.screens.buyerscreens.components.ErrorItem
import com.startup.graveyard.presentation.screens.buyerscreens.components.ErrorScreen
import com.startup.graveyard.presentation.viewmodels.assets.AssetViewModel
import kotlinx.coroutines.launch

@Composable
fun BuyerAssetsContent(
    viewModel: AssetViewModel,
    onAssetClick: (Int) -> Unit
) {
    val assets = remember(viewModel.pagingKey) {
        viewModel.assetsPagingFlow
    }.collectAsLazyPagingItems()
    val listState = rememberSaveable(
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val isRefreshing = assets.loadState.refresh is LoadState.Loading

    val pullRefreshState = rememberPullToRefreshState()

    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 4 }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            state = pullRefreshState,
            onRefresh = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                assets.refresh()
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
                            modifier = Modifier.size(150.dp),
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
                contentPadding = PaddingValues(
                    top = 12.dp,
                    bottom = 120.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = assets.itemCount
                ) { index ->
                    assets[index]?.let { asset ->
                        AssetCard(
                            asset = asset,
                            onClick = { onAssetClick(asset.id) }
                        )
                    }
                }

                when (assets.loadState.append) {

                    is LoadState.Error -> item {
                        ErrorItem(
                            message = "Failed to load more assets",
                            onRetry = { assets.retry() }
                        )
                    }
                    else -> Unit
                }
            }
        }

        if (assets.loadState.refresh is LoadState.Error) {
            ErrorScreen(
                message = "Something went wrong",
                onRetry = { assets.refresh() }
            )
        }



        AnimatedVisibility(
            visible = showScrollToTop,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 20.dp, bottom = 72.dp),
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    scope.launch { listState.animateScrollToItem(0) }
                },
                containerColor = Color(55, 70, 99, 255)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    tint = Color(255, 110, 64, 255),
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}
