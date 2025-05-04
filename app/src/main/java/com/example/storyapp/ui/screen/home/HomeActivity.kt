package com.example.storyapp.ui.screen.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.storyapp.ui.screen.detail.DetailActivity
import com.example.storyapp.ui.screen.story.StoryActivity

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    HomeScreen(
                        navToDetail = { storyId ->
                            val intent = Intent(this, DetailActivity::class.java)
                            intent.putExtra("story_id", storyId)
                            startActivity(intent)
                        },
                        navToUpload = {
                            startActivity(Intent(this, StoryActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}