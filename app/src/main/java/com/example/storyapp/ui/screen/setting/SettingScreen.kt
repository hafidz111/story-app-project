package com.example.storyapp.ui.screen.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.storyapp.R
import com.example.storyapp.ui.component.ProfileCard
import com.example.storyapp.ui.component.SettingListItem
import com.example.storyapp.ui.component.SettingListItemWithSwitch

@Composable
fun SettingScreen(
    onLogout: () -> Unit,
    viewModel: SettingViewModel
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val session by viewModel.getSession().observeAsState()

    LaunchedEffect(session) {
        session?.userId?.let { userId ->
            viewModel.getUserProfile(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        userProfile?.let { profile ->
            ProfileCard(
                name = profile.name,
                email = profile.email,
                imageUrl = profile.imageUrl
            )
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.padding(16.dp))

        SettingListItemWithSwitch(
            icon = Icons.Default.DarkMode,
            title = stringResource(R.string.dark_mode),
            isChecked = isDarkMode,
            onCheckedChange = { viewModel.saveThemeSetting(it) }
        )

        HorizontalDivider(thickness = 2.dp)

        SettingListItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = stringResource(R.string.logout),
            onClick = { viewModel.logout(onLogout) }
        )
    }
}