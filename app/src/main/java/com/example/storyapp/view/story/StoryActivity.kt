package com.example.storyapp.view.story

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.storyapp.di.Injection
import com.example.storyapp.view.ViewModelFactory

class StoryActivity : ComponentActivity() {
    private val viewModel: StoryViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    StoryScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}