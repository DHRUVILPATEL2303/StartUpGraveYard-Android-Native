package com.startup.graveyard.presentation.viewmodels.assets

import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.domain.models.assets.DataSpecificAsset

object AssetMemoryCache {

    private val cache = LinkedHashMap<Int, Asset>(
        50,
        0.75f,
        true
    )

    fun put(asset: Asset) {
        cache[asset.id] = asset
        trimIfNeeded()
    }

    fun putAll(assets: List<Asset>) {
        assets.forEach { put(it) }
    }

    fun get(id: Int): Asset? = cache[id]

    fun clear() {
        cache.clear()
    }

    private fun trimIfNeeded() {
        if (cache.size > 50) {
            val firstKey = cache.entries.first().key
            cache.remove(firstKey)
        }
    }
}