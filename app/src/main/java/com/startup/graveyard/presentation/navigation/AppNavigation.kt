package com.startup.graveyard.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.presentation.screens.loginscreen.LoginScreenUI
import com.startup.graveyard.presentation.screens.signupscreen.SignUpScreenUI
import com.startup.graveyard.presentation.screens.userselectionscreen.UserSelectionScreenUI

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    firebaseAuth: FirebaseAuth,
    startDestination: Any
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        navigation<SubNavigation.AuthRoutes>(
            startDestination = Routes.SignUpScreen
        ) {
            composable<Routes.LoginScreen> {
                LoginScreenUI(
                    onNavigateToSignUp = {
                        navController.navigate(Routes.SignUpScreen)
                    },
                    onLoginSuccess = {
                        navController.navigate(SubNavigation.UserSelectionRoutes) {
                            popUpTo(SubNavigation.AuthRoutes) { inclusive = true }
                        }
                    }
                )
            }

            composable<Routes.SignUpScreen> {
                SignUpScreenUI(
                    firebaseAuth = firebaseAuth,
                    onNavigateToLogin = {
                        navController.navigate(Routes.LoginScreen)
                    },
                    onSignUpSuccess = {
                        navController.navigate(SubNavigation.UserSelectionRoutes) {
                            popUpTo(SubNavigation.AuthRoutes) { inclusive = true }
                        }
                    }
                )
            }
        }

        navigation<SubNavigation.UserSelectionRoutes>(
            startDestination = Routes.UserSelectionScreen
        ) {
            composable<Routes.UserSelectionScreen> {
                UserSelectionScreenUI(
                    onBuyerSelected = {
                        navController.navigate(SubNavigation.BuyerRoutes) {
                            popUpTo(SubNavigation.UserSelectionRoutes) { inclusive = true }
                        }
                    },
                    onSellerSelected = {
                        navController.navigate(SubNavigation.SellerRoutes) {
                            popUpTo(SubNavigation.UserSelectionRoutes) { inclusive = true }
                        }
                    }
                )
            }
        }

        navigation<SubNavigation.BuyerRoutes>(
            startDestination = Routes.BuyerHome
        ) {
            composable<Routes.BuyerHome> {
                BuyerHomeScreen()
            }
            composable<Routes.BuyerProductDetails> {
                BuyerProductDetailsScreen()
            }
        }

        navigation<SubNavigation.SellerRoutes>(
            startDestination = Routes.SellerDashboard
        ) {
            composable<Routes.SellerDashboard> {
                SellerDashboardScreen()
            }
            composable<Routes.SellerAddProduct> {
                SellerAddProductScreen()
            }
        }
    }
}

@Composable
fun SellerAddProductScreen() {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text("Seller Add Product")
    }
}

@Composable
fun SellerDashboardScreen() {

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text("Seller Dashboard")
    }
}

@Composable
fun BuyerProductDetailsScreen(){

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text("Buyer Product Detaisl")
    }
}

@Composable
fun BuyerHomeScreen() {

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Text("Buyer Home Screen")
    }
}
