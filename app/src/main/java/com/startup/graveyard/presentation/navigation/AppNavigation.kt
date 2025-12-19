package com.startup.graveyard.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.presentation.screens.accountscreen.AccountScreenUI
import com.startup.graveyard.presentation.screens.loginscreen.LoginScreenUI
import com.startup.graveyard.presentation.screens.signupscreen.SignUpScreenUI
import com.startup.graveyard.presentation.screens.splashscreen.SplashScreenUI
import com.startup.graveyard.presentation.screens.userselectionscreen.UserSelectionScreenUI
import com.startup.graveyard.presentation.screens.verificationscreen.EmailVerificationScreenUI
import com.startup.graveyard.presentation.viewmodels.AuthViewModel
import com.startup.graveyard.presentation.viewmodels.SplashViewModel

private val BarBackgroundColor = Color(0xFF273646)
private val ActiveIndicatorColor = Color(0xFFFFFFFF)
private val ActiveIconColor = Color(0xFF1E1E1E)
private val InactiveIconColor = Color(0xFF9CA3AF)

fun NavDestination?.isBuyerDestination(): Boolean {
    return this?.hasRoute<Routes.BuyerHome>() == true ||
            this?.hasRoute<Routes.BuyerProductDetails>() == true
}

fun NavDestination?.isSellerDestination(): Boolean {
    return this?.hasRoute<Routes.SellerDashboard>() == true ||
            this?.hasRoute<Routes.SellerAddProduct>() == true
}

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

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            when {
                destination.isBuyerDestination() -> BuyerBottomBar(navController)
                destination.isSellerDestination() -> SellerBottomBar(navController)
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(bottom = 0.dp)
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
}


//@Composable
//fun BuyerBottomBar(navController: NavController) {
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val destination = backStackEntry?.destination
//
//    ModernFloatingBarContainer {
//        ModernBottomBarItem(
//            icon = Icons.Default.Home,
//            label = "Home",
//            selected = destination?.hasRoute<Routes.BuyerHome>() == true,
//            onClick = {
//                navController.navigate(Routes.BuyerHome) {
//                    popUpTo(SubNavigation.BuyerRoutes)
//                    launchSingleTop = true
//                }
//            }
//        )
//
//        ModernBottomBarItem(
//            icon = Icons.Default.ShoppingCart,
//            label = "Cart",
//            selected = destination?.hasRoute<Routes.BuyerProductDetails>() == true,
//            onClick = {
//                navController.navigate(Routes.BuyerProductDetails)
//            }
//        )
//
//        ModernBottomBarItem(
//            icon = Icons.Default.SwapHoriz,
//            label = "Switch",
//            selected = false,
//            isActionItem = true,
//            onClick = {
//                navController.goToUserSelection()
//            }
//        )
//    }
//}

//@Composable
//fun SellerBottomBar(navController: NavController) {
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val destination = backStackEntry?.destination
//
//    ModernFloatingBarContainer {
//        ModernBottomBarItem(
//            icon = Icons.Default.Dashboard,
//            label = "Dash",
//            selected = destination?.hasRoute<Routes.SellerDashboard>() == true,
//            onClick = {
//                navController.navigate(Routes.SellerDashboard) {
//                    popUpTo(SubNavigation.SellerRoutes)
//                    launchSingleTop = true
//                }
//            }
//        )
//
//        ModernBottomBarItem(
//            icon = Icons.Default.Add,
//            label = "Add",
//            selected = destination?.hasRoute<Routes.SellerAddProduct>() == true,
//            onClick = {
//                navController.navigate(Routes.SellerAddProduct)
//            }
//        )
//
//        ModernBottomBarItem(
//            icon = Icons.Default.ArrowBackIosNew,
//            label = "Switch",
//            selected = false,
//            isActionItem = true,
//            onClick = {
//                navController.goToUserSelection()
//            }
//        )
//    }
//}


@Composable
fun ModernFloatingBarContainer(
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(40.dp),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
                .background(
                    color = BarBackgroundColor,
                    shape = RoundedCornerShape(40.dp)
                )
                .height(72.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content
        )
    }
}

@Composable
fun RowScope.ModernBottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    isActionItem: Boolean = false,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    // Animations
    val animatedConfig = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    val scale by animateFloatAsState(
        targetValue = if (selected) 1.1f else 1.0f,
        animationSpec = animatedConfig,
        label = "scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (selected) ActiveIconColor else InactiveIconColor,
        label = "color"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(if (selected) 48.dp else 24.dp)
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = selected,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(ActiveIndicatorColor)
                    )
                }

                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActionItem && !selected) Color(0xFFFF5252) else iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            AnimatedVisibility(visible = !selected) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    color = InactiveIconColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


fun NavController.goToUserSelection() {
    navigate(SubNavigation.UserSelectionRoutes) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
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
        Text("Buyer Product Details")
    }
}

@Composable
fun BuyerBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    SplitFloatingBottomBar(
        onSwitchClick = { navController.goToUserSelection() }
    ) {
        ModernBottomBarItem(
            icon = Icons.Default.Home,
            label = "Home",
            selected = destination?.hasRoute<Routes.BuyerHome>() == true,
            onClick = {
                navController.navigate(Routes.BuyerHome) {
                    popUpTo(SubNavigation.BuyerRoutes)
                    launchSingleTop = true
                }
            }
        )

        ModernBottomBarItem(
            icon = Icons.Default.ShoppingCart,
            label = "Cart",
            selected = destination?.hasRoute<Routes.BuyerProductDetails>() == true,
            onClick = {
                navController.navigate(Routes.BuyerProductDetails)
            }
        )
    }
}

@Composable
fun FloatingBarWithLeftAction(
    onSwitchClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(56.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.3f)
                )
                .background(
                    color = BarBackgroundColor,
                    shape = CircleShape
                )
                .clickable { onSwitchClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Switch role",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Row(
            modifier = Modifier
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(40.dp),
                    spotColor = Color.Black.copy(alpha = 0.35f)
                )
                .background(
                    color = BarBackgroundColor,
                    shape = RoundedCornerShape(40.dp)
                )
                .height(72.dp)
                .fillMaxWidth(0.75f)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            content = content
        )
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


@Composable
fun SellerBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    SplitFloatingBottomBar(
        onSwitchClick = { navController.goToUserSelection() }
    ) {
        ModernBottomBarItem(
            icon = Icons.Default.Dashboard,
            label = "Dash",
            selected = destination?.hasRoute<Routes.SellerDashboard>() == true,
            onClick = {
                navController.navigate(Routes.SellerDashboard) {
                    popUpTo(SubNavigation.SellerRoutes)
                    launchSingleTop = true
                }
            }
        )

        ModernBottomBarItem(
            icon = Icons.Default.Add,
            label = "Add",
            selected = destination?.hasRoute<Routes.SellerAddProduct>() == true,
            onClick = {
                navController.navigate(Routes.SellerAddProduct)
            }
        )
    }
}


@Composable
fun SplitFloatingBottomBar(
    onSwitchClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        spotColor = Color.Black.copy(alpha = 0.35f)
                    )
                    .background(BarBackgroundColor, CircleShape)
                    .clip(CircleShape)
                    .clickable { onSwitchClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Switch Role",
                    tint = Color(0xFFFF5252),
                    modifier = Modifier.size(24.dp)
                )
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(40.dp),
                        spotColor = Color.Black.copy(alpha = 0.35f)
                    )
                    .background(
                        color = BarBackgroundColor,
                        shape = RoundedCornerShape(40.dp)
                    )
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                content = content
            )
        }
    }
}