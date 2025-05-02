package com.example.storyapp.view.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import com.example.storyapp.view.ViewModelFactory
import androidx.activity.compose.setContent

class DetailActivity : ComponentActivity() {
    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MaterialTheme {
                Surface {
                    DetailScreen(
                        viewModel = viewModel,
                        storyId = storyId ?: ""
                    )
                }
            }
        }

        storyId?.let { viewModel.getDetailStory(it) }
    }

    companion object {
        const val EXTRA_STORY_ID = "story_id"
    }
}