package com.startup.graveyard.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    object SignUpScreen

    @Serializable
    object LoginScreen

    @Serializable
    object UserSelectionScreen
}


sealed class SubNavigation {

    @Serializable
    object AuthRoutes


    @Serializable
    object HomeRoutes

    @Serializable
    object UserSelectionRoutes

}