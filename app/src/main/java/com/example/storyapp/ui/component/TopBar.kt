package com.example.storyapp.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.storyapp.R
import com.example.storyapp.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    navController: NavHostController? = null,
    showBackArrow: Boolean = false
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface

    TopAppBar(
        title = {
            Text(
                text = title
            )
        },
        navigationIcon = {
            if (showBackArrow && navController != null) {
                IconButton(onClick = {
                    navController.popBackStack(
                        Screen.Home.route,
                        inclusive = false
                    )
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(cardBg, contentColor),
        windowInsets = WindowInsets(0.dp)
    )
}

@Preview
@Composable
private fun TopBarPreview() {
    TopBar("Home", navController = null, showBackArrow = true)
}