package com.example.storyapp.view.welcome

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.storyapp.R

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onSignupClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val imageOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val alphaTitle = remember { Animatable(0f) }
    val alphaDesc = remember { Animatable(0f) }
    val alphaButtons = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaTitle.animateTo(1f, animationSpec = tween(100))
        alphaDesc.animateTo(1f, animationSpec = tween(100))
        alphaButtons.animateTo(1f, animationSpec = tween(100))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_welcome),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = imageOffset.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.title_welcome_page),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                modifier = Modifier.alpha(alphaTitle.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.message_welcome_page),
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.navy_500),
                modifier = Modifier.alpha(alphaDesc.value)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.alpha(alphaButtons.value)
            ) {
                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.navy_500)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.login))
                }

                Button(
                    onClick = onSignupClick,
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.navy_500)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.signup))
                }
            }
        }
    }
}