package com.startup.graveyard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.presentation.screens.loginscreen.LoginScreenUI
import com.startup.graveyard.presentation.screens.signupscreen.SignUpScreenUI

@Composable
fun AppNavigation(modifier: Modifier = Modifier,firebaseAuth: FirebaseAuth) {
    val navController= rememberNavController()


    NavHost(
        navController=navController,
        startDestination = SubNavigation.AuthRoutes
    ){
        navigation<SubNavigation.AuthRoutes>(startDestination = Routes.SignUpScreen){
            composable<Routes.LoginScreen> {
                LoginScreenUI(
                    onNavigateToSignUp = {
                        navController.navigate(Routes.SignUpScreen)
                    }
                )

            }
            composable<Routes.SignUpScreen> {
                SignUpScreenUI(firebaseAuth = firebaseAuth, onNavigateToLogin = {
                    navController.navigate(Routes.LoginScreen)
                }, onSignUpSuccess = {
                    navController.navigate(SubNavigation.UserSelectionRoutes)
                })

            }
        }

        navigation<SubNavigation.UserSelectionRoutes>(startDestination = Routes.UserSelectionScreen){
            composable<Routes.UserSelectionScreen> {
                UserSelectionScreenUI()
            }
        }
    }
}

@Composable
fun UserSelectionScreenUI() {
    TODO("Not yet implemented")
}