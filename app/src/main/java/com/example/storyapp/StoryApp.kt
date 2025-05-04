package com.example.storyapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.storyapp.ui.component.BottomNavigationBar
import com.example.storyapp.ui.component.LocationSnackbarHost
import com.example.storyapp.ui.component.TopBar
import com.example.storyapp.ui.navigation.Screen
import com.example.storyapp.ui.screen.detail.DetailScreen
import com.example.storyapp.ui.screen.home.HomeScreen
import com.example.storyapp.ui.screen.login.LoginScreen
import com.example.storyapp.ui.screen.maps.MapsScreen
import com.example.storyapp.ui.screen.setting.SettingScreen
import com.example.storyapp.ui.screen.setting.SettingViewModel
import com.example.storyapp.ui.screen.signup.SignupScreen
import com.example.storyapp.ui.screen.story.StoryScreen
import com.example.storyapp.ui.screen.welcome.WelcomeScreen

@Composable
fun StoryApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    settingViewModel: SettingViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            ) {
                when (currentRoute) {
                    Screen.Home.route -> {
                        TopBar(
                            title = stringResource(R.string.app_name),
                        )
                    }

                    Screen.UploadStory.route -> {
                        TopBar(
                            title = stringResource(R.string.new_story),
                            navController = navController,
                            showBackArrow = true
                        )
                    }

                    Screen.Detail.route -> {
                        TopBar(
                            title = stringResource(R.string.title_detail_story),
                            navController = navController,
                            showBackArrow = true
                        )
                    }

                    Screen.MapsStory.route -> {
                        TopBar(
                            title = stringResource(R.string.title_maps),
                            navController = navController
                        )
                    }

                    Screen.Setting.route -> {
                        TopBar(
                            title = stringResource(R.string.title_setting),
                            navController = navController
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (currentRoute == Screen.Home.route || currentRoute == Screen.MapsStory.route || currentRoute == Screen.Setting.route) {
                BottomNavigationBar(navController)
            }
        },
        floatingActionButton = {
            when (currentRoute) {
                Screen.Home.route -> {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("story")
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.new_story)
                        )
                    }
                }
            }
        },
        snackbarHost = { LocationSnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onLoginClick = {
                        navController.navigate("login")
                    },
                    onSignupClick = {
                        navController.navigate("signup")
                    }
                )
            }
            composable(Screen.Signup.route) {
                SignupScreen(
                    onSignupSuccess = {
                        navController.navigate("login") {
                            popUpTo(Screen.Signup.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    navToDetail = { storyId ->
                        navController.navigate("detail/$storyId")
                    }
                )
            }
            composable(Screen.UploadStory.route) {
                StoryScreen()
            }
            composable(
                Screen.Detail.route,
                arguments = listOf(navArgument("storyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
                DetailScreen(storyId = storyId)
            }
            composable(Screen.MapsStory.route) {
                MapsScreen()
            }
            composable(Screen.Setting.route) {
                SettingScreen(
                    onLogout = {
                        onLogout()
                    },
                    viewModel = settingViewModel,
                )
            }
        }
    }
}