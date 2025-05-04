package com.example.storyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.screen.setting.SettingPreferences
import com.example.storyapp.ui.screen.setting.SettingViewModel
import com.example.storyapp.ui.screen.setting.SettingViewModelFactory
import com.example.storyapp.ui.screen.setting.dataStore
import com.example.storyapp.ui.theme.StoryAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreference = UserPreference.getInstance(applicationContext.dataStore)

        val repository = Injection.provideRepository(this)
        val settingPreferences = SettingPreferences.getInstance(applicationContext.dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(repository, settingPreferences)
        )[SettingViewModel::class.java]

        setContent {
            val userSession by userPreference.getSession().collectAsState(
                initial = UserModel(
                    userId = "",
                    name = "",
                    imageUrl = "",
                    email = "",
                    token = "",
                    isLogin = false
                )
            )

            val isLoggedIn = userSession.isLogin
            val isDarkMode by settingViewModel.isDarkMode.collectAsState()
            StoryAppTheme(darkTheme = isDarkMode) {
                StoryApp(
                    isLoggedIn = isLoggedIn,
                    onLogout = {
                        settingViewModel.logout {}
                    },
                    settingViewModel = settingViewModel
                )
            }
        }
    }
}