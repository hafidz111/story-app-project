@file:Suppress("NAME_SHADOWING")

package com.example.storyapp.ui.screen.signup

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.util.Patterns
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.common.UiState
import android.widget.Toast
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyapp.R
import com.example.storyapp.ui.component.InputField
import com.example.storyapp.ui.component.PasswordInputField

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun SignupScreen(
    viewModel: SignupViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    onSignupSuccess: () -> Unit
) {
    val context = LocalContext.current
    val registerUiState by viewModel.registerUiState.observeAsState(UiState.Idle)

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var nameError by rememberSaveable { mutableStateOf<String?>(null) }
    var emailError by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<String?>(null) }

    val isButtonEnabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()

    val infiniteTransition = rememberInfiniteTransition(label = "imageAnim")
    val imageOffset by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = stringResource(R.string.imageoffset)
    )

    LaunchedEffect(registerUiState) {
        if (registerUiState is UiState.Success) {
            val name = (registerUiState as UiState.Success<String>).data
            AlertDialog.Builder(context).apply {
                setTitle(context.getString(R.string.yeah))
                setMessage(context.getString(R.string.success_registration, name))
                setPositiveButton(context.getString(R.string.next)) { _, _ -> onSignupSuccess() }
                show()
            }
        } else if (registerUiState is UiState.Error) {
            Toast.makeText(
                context,
                (registerUiState as UiState.Error).error,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        if (registerUiState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(R.drawable.image_signup),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = imageOffset.dp)
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                stringResource(R.string.title_signup_page),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(16.dp))

            InputField(
                value = name,
                onValueChange = {
                    name = it
                    nameError =
                        if (it.isBlank()) context.getString(R.string.error_empty_name) else null
                },
                label = stringResource(R.string.name),
                placeholder = stringResource(R.string.enter_your_name),
                leadingIcon = Icons.Default.Person,
                isError = nameError != null,
                errorMessage = nameError
            )

            Spacer(Modifier.height(8.dp))

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

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.register(name, email, password) },
                enabled = isButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.navy_500)),
            ) {
                Text(stringResource(R.string.signup))
            }
        }
    }
}

@Preview
@Composable
private fun SignupScreenScreenPreview() {
    SignupScreen { }
}