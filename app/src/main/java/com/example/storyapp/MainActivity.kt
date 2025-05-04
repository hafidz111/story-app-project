package com.example.storyapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.screen.setting.SettingPreferences
import com.example.storyapp.ui.screen.setting.SettingViewModel
import com.example.storyapp.ui.screen.setting.SettingViewModelFactory
import com.example.storyapp.ui.screen.setting.dataStore
import com.example.storyapp.ui.theme.StoryAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val initiallyLoggedIn = prefs.getBoolean("is_logged_in", false)

        val repository = Injection.provideRepository(this)
        val settingPreferences = SettingPreferences.getInstance(applicationContext.dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(repository, settingPreferences)
        )[SettingViewModel::class.java]

        setContent {
            val isDarkMode by settingViewModel.isDarkMode.collectAsState()
            var isLoggedIn by rememberSaveable { mutableStateOf(initiallyLoggedIn) }
            StoryAppTheme(darkTheme = isDarkMode) {
                StoryApp(
                    isLoggedIn = isLoggedIn,
                    onLogout = {
                        prefs.edit { clear() }
                        isLoggedIn = false
                    },
                    settingViewModel = settingViewModel
                )
            }
        }
    }
}