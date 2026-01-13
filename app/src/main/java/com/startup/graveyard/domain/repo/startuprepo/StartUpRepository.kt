package com.startup.graveyard.domain.repo.startuprepo

import androidx.paging.Pager
import com.startup.graveyard.domain.models.startups.Startup

interface StartUpRepository {

    fun getStartupsPager(): Pager<Int, Startup>

    fun getSpecificUserStartUps() : Pager<Int, Startup>
}