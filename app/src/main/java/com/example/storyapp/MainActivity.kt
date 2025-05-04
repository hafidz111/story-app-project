package com.example.storyapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.screen.home.HomeActivity
import com.example.storyapp.ui.screen.home.HomeViewModel
import com.example.storyapp.ui.screen.login.LoginActivity
import com.example.storyapp.ui.screen.signup.SignupActivity
import com.example.storyapp.ui.screen.welcome.WelcomeScreen

class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                setContent {
                    MaterialTheme {
                        WelcomeScreen(
                            onLoginClick = {
                                startActivity(Intent(this, LoginActivity::class.java))
                            },
                            onSignupClick = {
                                startActivity(Intent(this, SignupActivity::class.java))
                            }
                        )
                    }
                }
            }
        }
    }
}