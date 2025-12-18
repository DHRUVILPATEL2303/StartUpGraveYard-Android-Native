package com.startup.graveyard.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.R
import com.startup.graveyard.presentation.screens.accountscreen.AccountScreenUI
import com.startup.graveyard.presentation.screens.loginscreen.LoginScreenUI
import com.startup.graveyard.presentation.screens.signupscreen.SignUpScreenUI
import com.startup.graveyard.presentation.screens.splashscreen.SplashScreenUI
import com.startup.graveyard.presentation.screens.userselectionscreen.UserSelectionScreenUI
import com.startup.graveyard.presentation.screens.verificationscreen.EmailVerificationScreenUI
import com.startup.graveyard.presentation.viewmodels.AuthViewModel
import com.startup.graveyard.presentation.viewmodels.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    firebaseAuth: FirebaseAuth,
    splashViewModel: SplashViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>()
) {
    val navController = rememberNavController()

    if (firebaseAuth.currentUser != null) {
        LaunchedEffect(Unit) {
            authViewModel.getUserAccountDetails()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {

        composable<Routes.Splash> {

            val isAnimationDone by splashViewModel.isAnimationDone.collectAsState()

            LaunchedEffect(isAnimationDone) {
                if (isAnimationDone) {
                    val nextRoute =
                        if (firebaseAuth.currentUser == null)
                            SubNavigation.AuthRoutes
                        else
                            SubNavigation.UserSelectionRoutes

                    navController.navigate(nextRoute) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            }

            SplashScreenUI(
                onAnimationFinished = {
                    splashViewModel.markAnimationDone()
                }
            )
        }

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
                    },
                    navController = navController
                )
            }

            composable<Routes.VerificationScreen> {
                EmailVerificationScreenUI(
                    navController = navController,
                    firebaseAuth = firebaseAuth
                )
            }

            composable<Routes.AccountScreen> {
                AccountScreenUI(
                    navController = navController
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
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Seller Add Product")
    }
}

@Composable
fun SellerDashboardScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Seller Dashboard")
    }
}

@Composable
fun BuyerProductDetailsScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Buyer Product Detaisl")
    }
}


@Composable
fun BuyerHomeScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Buyer Home Screen")
    }
}
