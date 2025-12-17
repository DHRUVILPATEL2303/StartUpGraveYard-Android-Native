package com.startup.graveyard.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    object SignUpScreen

    @Serializable
    object LoginScreen

    @Serializable
    object UserSelectionScreen


    @Serializable
    object BuyerHome

    @Serializable
    object SellerDashboard


    @Serializable
    object BuyerProductDetails

    @Serializable
    object SellerAddProduct
}


sealed class SubNavigation {

    @Serializable
    object AuthRoutes


    @Serializable
    object HomeRoutes

    @Serializable
    object UserSelectionRoutes


    @Serializable
    object BuyerRoutes

    @Serializable
    object SellerRoutes


}