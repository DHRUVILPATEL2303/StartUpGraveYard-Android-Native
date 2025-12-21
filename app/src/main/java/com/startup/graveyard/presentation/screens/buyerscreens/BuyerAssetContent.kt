package com.startup.graveyard.presentation.screens.buyerscreens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.startup.graveyard.presentation.screens.buyerscreens.components.AssetCard
import com.startup.graveyard.presentation.screens.buyerscreens.components.CenterLoader
import com.startup.graveyard.presentation.screens.buyerscreens.components.ErrorItem
import com.startup.graveyard.presentation.screens.buyerscreens.components.ErrorScreen
import com.startup.graveyard.presentation.viewmodels.AssetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BuyerAssetsContent(
    viewModel: AssetViewModel,
    onAssetClick: (Int) -> Unit
) {
    val assets = viewModel.assetsPagingFlow.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val scope=rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = 100.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(assets.itemCount) { index ->
                val asset = assets[index]
                asset?.let {
                    AssetCard(
                        asset = it,
                        onClick = { onAssetClick(it.id) }
                    )
                }
            }

            when (assets.loadState.append) {
                is LoadState.Loading -> item { CenterLoader() }
                is LoadState.Error -> item {
                    ErrorItem(
                        message = "Failed to load more assets",
                        onRetry = { assets.retry() }
                    )
                }
                else -> Unit
            }
        }

        when (assets.loadState.refresh) {
            is LoadState.Loading -> CenterLoader()
            is LoadState.Error -> ErrorScreen(
                message = "Something went wrong",
                onRetry = { assets.retry() }
            )
            else -> Unit
        }
    }
}


