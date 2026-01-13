package com.startup.graveyard.domain.usecase.startupusecases

import com.startup.graveyard.domain.repo.startuprepo.StartUpRepository
import javax.inject.Inject

class GetAllStartUpUseCase @Inject constructor(
    private val startUpRepository: StartUpRepository
) {

    fun getAllStartupUseCase()=startUpRepository.getStartupsPager()
}