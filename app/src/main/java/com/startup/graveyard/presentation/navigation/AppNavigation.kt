package com.startup.graveyard.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwipeLeftAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.startup.graveyard.presentation.screens.buyerscreens.BuyerHomeScreenUI
import com.startup.graveyard.presentation.screens.loginscreen.LoginScreenUI
import com.startup.graveyard.presentation.screens.sellerscreen.SellerAddScreenUI
import com.startup.graveyard.presentation.screens.signupscreen.SignUpScreenUI
import com.startup.graveyard.presentation.screens.splashscreen.SplashScreenUI
import com.startup.graveyard.presentation.screens.userselectionscreen.UserSelectionScreenUI
import com.startup.graveyard.presentation.screens.verificationscreen.EmailVerificationScreenUI
import com.startup.graveyard.presentation.viewmodels.AssetViewModel
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
    authViewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    assetViewModel: AssetViewModel = hiltViewModel<AssetViewModel>()
) {
    val navController = rememberNavController()

    var isBottomBarCollapsed by rememberSaveable { mutableStateOf(false) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < -8f) {
                    isBottomBarCollapsed = true
                } else if (delta > 8f) {
                    isBottomBarCollapsed = false
                }
                return Offset.Zero
            }
        }
    }

    if (firebaseAuth.currentUser != null) {
        LaunchedEffect(Unit) {
            authViewModel.getUserAccountDetails()
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        bottomBar = {
            when {
                destination.isBuyerDestination() ->
                    BuyerBottomBar(
                        navController = navController,
                        isCollapsed = isBottomBarCollapsed,
                        onExpand = { isBottomBarCollapsed = false }
                    )
                destination.isSellerDestination() ->
                    SellerBottomBar(
                        navController = navController,
                        isCollapsed = isBottomBarCollapsed,
                        onExpand = { isBottomBarCollapsed = false }
                    )
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(bottom = 0.dp)
        ) {

            composable<Routes.Splash>(
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(700, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight },
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                }
            ) {
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
                composable<Routes.LoginScreen>(
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    }
                ) {
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

                composable<Routes.SignUpScreen>(
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    }
                ) {
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
                composable<Routes.UserSelectionScreen>(
                    enterTransition = {
                        fadeIn(animationSpec = tween(400))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(400))
                    }
                ) {
                    // CHANGE START: Removed popUpTo to keep UserSelection in stack
                    UserSelectionScreenUI(
                        onBuyerSelected = {
                            navController.navigate(SubNavigation.BuyerRoutes)
                        },
                        onSellerSelected = {
                            navController.navigate(SubNavigation.SellerRoutes)
                        },
                        navController = navController
                    )
                    // CHANGE END
                }

                composable<Routes.VerificationScreen>(
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    }
                ) {
                    EmailVerificationScreenUI(
                        navController = navController,
                        firebaseAuth = firebaseAuth
                    )
                }

                composable<Routes.AccountScreen>(
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(400)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(400)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        )
                    }
                ) {
                    AccountScreenUI(
                        navController = navController
                    )
                }
            }

            navigation<SubNavigation.BuyerRoutes>(
                startDestination = Routes.BuyerHome,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(700, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight },
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                }
            ) {


                composable<Routes.BuyerHome>(
                    enterTransition = { fadeIn(tween(400)) },
                    exitTransition = { fadeOut(tween(400)) }
                ) { backStackEntry ->



                    BuyerHomeScreenUI(
                        assetViewModel = assetViewModel,
                    )
                }

                composable<Routes.BuyerPivotScreen> {

                }

                composable<Routes.BuyerAssetsScreen> {

                }
                composable<Routes.BuyerProductDetails>(
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(400)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(400)
                        )
                    }
                ) {
                    BuyerProductDetailsScreen()
                }
            }

            navigation<SubNavigation.SellerRoutes>(
                startDestination = Routes.SellerDashboard,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(700, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight },
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                }
            ) {
                composable<Routes.SellerDashboard>(
                    enterTransition = { fadeIn(tween(400)) },
                    exitTransition = { fadeOut(tween(400)) }
                ) {
                  SellerDashboardScreen()
                }
                composable<Routes.SellerAddProduct>(
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(400)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(400)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(400)
                        )
                    }
                ) {
                    SellerAddScreenUI(assetViewModel=assetViewModel,firebaseAuth)
                }
            }
        }
    }
}


@Composable
fun BuyerBottomBar(
    navController: NavController,
    isCollapsed: Boolean,
    onExpand: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val isHome = destination?.hasRoute<Routes.BuyerHome>() == true
    val isCart = destination?.hasRoute<Routes.BuyerProductDetails>() == true

    AdaptiveBottomBar(
        isCollapsed = isCollapsed,
        onSwitchClick = { navController.goToUserSelection() },
        selectedIcon = if (isCart) Icons.Default.ShoppingCart else Icons.Default.Home,
        onCompactRightClick = onExpand,
        expandedContent = {
            ModernBottomBarItem(
                icon = Icons.Default.Home,
                label = "Home",
                selected = isHome,
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
                selected = isCart,
                onClick = {
                    navController.navigate(Routes.BuyerProductDetails) {
                        launchSingleTop = true
                    }
                }
            )
        }
    )
}

@Composable
fun SellerBottomBar(
    navController: NavController,
    isCollapsed: Boolean,
    onExpand: () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val isDash = destination?.hasRoute<Routes.SellerDashboard>() == true
    val isAdd = destination?.hasRoute<Routes.SellerAddProduct>() == true

    AdaptiveBottomBar(
        isCollapsed = isCollapsed,
        onSwitchClick = { navController.goToUserSelection() },
        selectedIcon = if (isAdd) Icons.Default.Add else Icons.Default.Dashboard,
        onCompactRightClick = onExpand, // CLICKING EXPANDS THE BAR
        expandedContent = {
            ModernBottomBarItem(
                icon = Icons.Default.Dashboard,
                label = "Dash",
                selected = isDash,
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
                selected = isAdd,
                onClick = {
                    navController.navigate(Routes.SellerAddProduct)
                }
            )
        }
    )
}


/**
 * Animated Wrapper that switches between Compact (Scrolled) and Expanded (Default) views.
 */
@Composable
fun AdaptiveBottomBar(
    isCollapsed: Boolean,
    onSwitchClick: () -> Unit,
    selectedIcon: ImageVector,
    onCompactRightClick: () -> Unit,
    expandedContent: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedContent(
            targetState = isCollapsed,
            transitionSpec = {
                (fadeIn(animationSpec = tween(300)) + slideInVertically { it / 2 }) togetherWith
                        (fadeOut(animationSpec = tween(300)) + slideOutVertically { it / 2 })
            },
            label = "BottomBarModeSwitch"
        ) { collapsed ->
            if (collapsed) {
                CompactFloatingBar(
                    onLeftClick = onSwitchClick,
                    selectedIcon = selectedIcon,
                    onRightClick = onCompactRightClick
                )
            } else {
                SplitFloatingBottomBarLayout(
                    onSwitchClick = onSwitchClick,
                    content = expandedContent
                )
            }
        }
    }
}

/**
 * The Small UI: Switch Button on Left, Active Icon Button on Right.
 */
@Composable
fun CompactFloatingBar(
    onLeftClick: () -> Unit,
    selectedIcon: ImageVector,
    onRightClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .shadow(elevation = 10.dp, shape = CircleShape, spotColor = Color.Black.copy(0.3f))
                .background(BarBackgroundColor, CircleShape)
                .clip(CircleShape)
                .clickable { onLeftClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwipeLeftAlt,
                contentDescription = "Switch",
                tint = Color(0xFFFF6E40),
                modifier = Modifier.size(24.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .shadow(elevation = 10.dp, shape = CircleShape, spotColor = Color.Black.copy(0.3f))
                .background(BarBackgroundColor, CircleShape)
                .clip(CircleShape)
                .clickable { onRightClick() },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(ActiveIndicatorColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = "Selected",
                    tint = ActiveIconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * The Large UI: Switch Button on Left (Big), Navigation Bar on Right.
 */
@Composable
fun SplitFloatingBottomBarLayout(
    onSwitchClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
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
                imageVector = Icons.Default.SwipeLeftAlt,
                contentDescription = "Switch Role",
                tint = Color(0xFFFF6E40),
                modifier = Modifier.size(34.dp)
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
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
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
    popBackStack<Routes.UserSelectionScreen>(inclusive = false)
}

@Composable
fun SellerAddProductScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Seller Add Product")
    }
}

@Composable
fun SellerDashboardScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Seller Dashboard")
    }
}

@Composable
fun BuyerProductDetailsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Buyer Product Details")
    }
}