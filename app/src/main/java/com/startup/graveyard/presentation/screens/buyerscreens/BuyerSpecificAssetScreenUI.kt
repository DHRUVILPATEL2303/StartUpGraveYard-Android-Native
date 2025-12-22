@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.startup.graveyard.presentation.screens.buyerscreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.presentation.screens.buyerscreens.components.ErrorScreen
import com.startup.graveyard.presentation.viewmodels.assets.AssetViewModel

@Composable
fun BuyerSpecificAssetScreenUI(
    assetId: String,
    assetViewModel: AssetViewModel,
    onBack: () -> Unit = {}
) {
    val state by assetViewModel.specificAssetState.collectAsState()

    LaunchedEffect(assetId) {
        assetViewModel.getSpecificAssetDetails(assetId)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            state.isLoading && state.data == null -> {
                CenterAssetLoader()
            }

            state.error.isNotEmpty() && state.data == null -> {
                ErrorScreen(
                    message = state.error,
                    onRetry = { assetViewModel.getSpecificAssetDetails(assetId) }
                )
            }

            state.data != null -> {
                AssetDetailsContent(
                    asset = state.data!!,
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
private fun AssetDetailsContent(
    asset: Asset,
    onBack: () -> Unit
) {

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->


        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {

                item {
                    AssetImageSection(
                        imageUrl = asset.imageUrl,
                        isSold = asset.isSold,
                        onBack = onBack
                    )
                }

                item {
                    AssetInfoSection(asset)
                }

                item {
                    AssetDescriptionSection(asset.description)
                }
            }
        }

    }
}

@Composable
private fun AssetImageSection(
    imageUrl: String,
    isSold: Boolean,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.height(320.dp)) {

        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIos,
                tint = Color.White,
                contentDescription = "Back"
            )
        }

        if (isSold) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(Color.Red, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("SOLD", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AssetInfoSection(asset: Asset) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = asset.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "₹ ${asset.price}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            AssetBadge(asset.assetType)

            if (asset.isNegotiable) {
                AssetBadge("Negotiable")
            }

            if (asset.isActive) {
                AssetBadge("Active")
            }
        }
    }
}

@Composable
private fun AssetBadge(text: String) {
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun AssetDescriptionSection(description: String) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun CenterAssetLoader() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator()
    }
}