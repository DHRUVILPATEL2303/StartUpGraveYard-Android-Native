package com.startup.graveyard.domain.usecase.startupusecases

import com.startup.graveyard.domain.repo.startuprepo.StartUpRepository
import javax.inject.Inject

class GetAllSpecificUserStartupUseCase @Inject constructor(
    private val startUpRepository: StartUpRepository
) {

    fun getAllSpecificUserStartUpUseCase() = startUpRepository.getSpecificUserStartUps()
}