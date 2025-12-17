package com.startup.graveyard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.auth.FirebaseAuth
import com.startup.graveyard.presentation.navigation.AppNavigation
import com.startup.graveyard.presentation.navigation.SubNavigation
import com.startup.graveyard.ui.theme.StartUpGraveYardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val firebaseAuth = FirebaseAuth.getInstance()

        val startDestination = if (firebaseAuth.currentUser == null) {
            SubNavigation.AuthRoutes
        } else {
            SubNavigation.UserSelectionRoutes
        }

        setContent {
            StartUpGraveYardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {innerPaddding->
                    AppNavigation(
                        firebaseAuth = firebaseAuth,
                    )
                    PreloadLottie()
                }
            }
        }
    }
}

@Composable
fun PreloadLottie() {
    rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.splashanimation)
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StartUpGraveYardTheme {
        Greeting("Android")
    }
}