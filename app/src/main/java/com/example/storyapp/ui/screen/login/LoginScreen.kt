package com.example.storyapp.ui.screen.login

import android.annotation.SuppressLint
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyapp.R
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.common.UiState
import androidx.core.content.edit
import com.example.storyapp.ui.component.InputField
import com.example.storyapp.ui.component.PasswordInputField

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(factory = ViewModelFactory.getInstance(LocalContext.current)),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val loginResult by viewModel.loginUiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }
    val isButtonEnabled =
        email.isNotBlank() && password.isNotBlank() && emailError == null && passwordError == null

    val animatedOffset = rememberInfiniteTransition()
        .animateFloat(
            initialValue = -20f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(6000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

    LaunchedEffect(loginResult) {
        when (val result = loginResult) {
            is UiState.Success -> {
                viewModel.saveSession(result.data)
                viewModel.saveUserProfile(result.data)
                Toast.makeText(
                    context,
                    context.getString(R.string.succes_login_as, result.data.email),
                    Toast.LENGTH_SHORT
                ).show()
                onLoginSuccess()
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                prefs.edit { putBoolean("is_logged_in", true) }
            }

            is UiState.Error -> {
                Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        if (loginResult is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.image_login),
                contentDescription = stringResource(R.string.image_login),
                modifier = Modifier
                    .offset(x = animatedOffset.value.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.title_login_page),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.message_login_page),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            InputField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (Patterns.EMAIL_ADDRESS.matcher(it).matches()) null
                    else context.getString(R.string.error_invalid_email)
                },
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.enter_your_email),
                leadingIcon = Icons.Default.Email,
                isError = emailError != null,
                errorMessage = emailError,
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(8.dp))

            PasswordInputField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (it.length >= 8) null
                    else context.getString(R.string.error_invalid_password)
                },
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.enter_your_password),
                isError = passwordError != null,
                errorMessage = passwordError
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.navy_500)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenView() {
    LoginScreen {

    }
}