package com.example.storyapp.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.utils.withDateFormat

@Composable
fun UserStory(story: Story) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = stringResource(R.string.photo_user),
            modifier = Modifier
                .size(64.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = story.name ?: "-",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = story.createdAt?.withDateFormat() ?: "-",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}