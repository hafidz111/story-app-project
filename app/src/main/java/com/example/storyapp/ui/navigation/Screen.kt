package com.example.storyapp.ui.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Signup : Screen("signup")
    data object Home : Screen("home")
    data object UploadStory : Screen("story")
    data object MapsStory : Screen("maps_story")
    data object Setting : Screen("setting")
    data object Detail : Screen("detail/{storyId}")
}