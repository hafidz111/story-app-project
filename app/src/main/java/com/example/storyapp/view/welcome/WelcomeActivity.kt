package com.example.storyapp.view.welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.signup.SignupActivity

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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