package com.startup.graveyard.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
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

    @Serializable
    object Splash

    @Serializable
    object AccountScreen

    @Serializable
    object VerificationScreen


    @Serializable
    object BuyerHomeScreen


    @Serializable
    object BuyerAssetsScreen

    @Serializable
    object BuyerPivotScreen

    @Serializable
    data class SellerSpecificAssetScreen(
        val id : String
    )

    @Serializable
    data class BuyerSpecificAssetScreen(
        val id : String
    )

}


sealed class BuyerBottomNav(
    val route: Any,
    val title: String,
    val icon: ImageVector
) {
    object Home : BuyerBottomNav(Routes.BuyerHome, "Home", Icons.Default.Home)
    object Cart : BuyerBottomNav(Routes.BuyerProductDetails, "Cart", Icons.Default.ShoppingCart)
}


sealed class SellerBottomNav(
    val route: Any,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : SellerBottomNav(Routes.SellerDashboard, "Dashboard", Icons.Default.Dashboard)
    object AddProduct : SellerBottomNav(Routes.SellerAddProduct, "Add", Icons.Default.Add)
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