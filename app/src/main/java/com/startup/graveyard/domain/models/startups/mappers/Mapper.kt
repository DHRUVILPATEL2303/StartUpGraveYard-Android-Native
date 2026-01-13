package com.startup.graveyard.domain.models.startups.mappers


import com.startup.graveyard.domain.models.startups.ItemStartUps
import com.startup.graveyard.domain.models.startups.Startup

fun ItemStartUps.toDomain(): Startup {
    return Startup(
        id = id,
        uuid = owner_uuid,
        name = name,
        description = description,
        logoUrl = logo_url,
        status = status,
        createdAt = created_at
    )
}