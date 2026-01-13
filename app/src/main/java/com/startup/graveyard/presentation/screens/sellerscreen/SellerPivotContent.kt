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
import com.startup.graveyard.presentation.screens.buyerscreens.components.StartupCard
import com.startup.graveyard.presentation.viewmodels.assets.AssetViewModel
import com.startup.graveyard.presentation.viewmodels.startups.StartUpViewModel

@Composable
fun SellerPivotsContent(
    startUpViewModel: StartUpViewModel
) {
    val startups = startUpViewModel
        .specificUserStartUpsPagingFlow
        .collectAsLazyPagingItems()

    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    val haptic = LocalHapticFeedback.current
    val pullRefreshState = rememberPullToRefreshState()

    val isRefreshing = startups.loadState.refresh is LoadState.Loading

    var showDeleteDialog by remember { mutableStateOf(false) }
    var startupToDeleteId by remember { mutableStateOf<Int?>(null) }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        state = pullRefreshState,
        onRefresh = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            startups.refresh()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            if (startups.itemCount == 0 && startups.loadState.refresh !is LoadState.Loading) {
                item {
                    Box(
                        Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No pivots listed yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(startups.itemCount) { index ->
                startups[index]?.let { startup ->
                    SellerStartupItem(
                        startup = startup,
                        onStartupClick = {
                            // navigate to startup details
                        },
                        onEditClick = {
                            // navigate to update startup
                        },
                        onDeleteClick = {
                            startupToDeleteId = startup.id
                            showDeleteDialog = true
                        }
                    )
                }
            }

            if (startups.loadState.append is LoadState.Loading) {
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

    if (showDeleteDialog && startupToDeleteId != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteDialog = false
                // call delete startup API
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}