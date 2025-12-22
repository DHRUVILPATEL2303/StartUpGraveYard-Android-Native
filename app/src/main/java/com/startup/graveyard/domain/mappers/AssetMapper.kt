package com.startup.graveyard.domain.mappers


import com.startup.graveyard.domain.models.assets.Asset
import com.startup.graveyard.domain.models.getallassets.Item
import com.startup.graveyard.domain.models.getallassets.ItemSpecific

fun Item.toDomain(): Asset {
    return Asset(
        id = id,
        title = title,
        description = description,
        assetType = asset_type,
        price = price,
        imageUrl = image_url,
        isSold = is_sold,
        isActive = is_active,
        isNegotiable = is_negotiable,
        userUuid = user_uuid,
        createdAt = created_at
    )
}

fun ItemSpecific.toDomain(): Asset{
    return Asset(
        id = id,
        title = title,
        description = description,
        assetType = asset_type,
        price = price,
        imageUrl = image_url,
        isSold = is_sold,
        isActive = is_active,
        isNegotiable = is_negotiable,
        userUuid = user_uuid,
        createdAt = created_at
    )

}