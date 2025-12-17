package com.startup.graveyard.presentation.screens.splashscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.startup.graveyard.R

@Composable
fun SplashScreenUI(
    onAnimationFinished: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splashanimation))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = 1)

    val companyName = "Startup Graveyard"
    val textProgress = (progress * 2f).coerceIn(0f, 1f)
    val visibleChars = (companyName.length * textProgress).toInt().coerceIn(0, companyName.length)
    val displayedText = companyName.substring(0, visibleChars)

    LaunchedEffect(progress) {
        if (progress >= 1f) onAnimationFinished()
    }

    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(320.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayedText,
                color = colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                style = typography.headlineSmall
            )
        }
    }
}