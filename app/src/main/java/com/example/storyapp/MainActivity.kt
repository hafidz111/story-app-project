package com.example.storyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.di.Injection
import com.example.storyapp.ui.screen.setting.SettingPreferences
import com.example.storyapp.ui.screen.setting.SettingViewModel
import com.example.storyapp.ui.screen.setting.SettingViewModelFactory
import com.example.storyapp.ui.screen.setting.dataStore
import com.example.storyapp.ui.theme.StoryAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val userPreference = UserPreference.getInstance(applicationContext.dataStore)

        val repository = Injection.provideRepository(this)
        val settingPreferences = SettingPreferences.getInstance(applicationContext.dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(repository, settingPreferences)
        )[SettingViewModel::class.java]

        var isSessionLoaded = false
        splashScreen.setKeepOnScreenCondition { !isSessionLoaded }

        lifecycleScope.launch {
            userPreference.getSession().first()
            isSessionLoaded = true

            setContent {
                val sessionFlow = userPreference.getSession().collectAsState(
                    initial = null
                )

                val userSession = sessionFlow.value ?: return@setContent

                isSessionLoaded = true
                val isDarkMode by settingViewModel.isDarkMode.collectAsState()

                StoryAppTheme(darkTheme = isDarkMode) {
                    StoryApp(
                        isLoggedIn = userSession.isLogin,
                        onLogout = {
                            settingViewModel.logout {}
                        },
                        settingViewModel = settingViewModel
                    )
                }
            }
        }
    }
}