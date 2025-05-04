@file:Suppress("DEPRECATION")

package com.example.storyapp.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.Story

@Composable
fun StoryItem(
    story: ListStoryItem,
    commentCount: Int,
    onClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    val description = story.description ?: ""
    val showLoadMore = description.length > 150

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            UserStory(
                story = Story(
                    id = story.id,
                    name = story.name,
                    description = description,
                    photoUrl = story.photoUrl,
                    createdAt = story.createdAt
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
                model = story.photoUrl,
                contentDescription = stringResource(R.string.image_story),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (showLoadMore) {
                val previewText = description.take(150).trimEnd()
                val annotatedString = buildAnnotatedString {
                    append("$previewText... ")
                    pushStringAnnotation(tag = "LOAD_MORE", annotation = "load_more")
                    append(stringResource(R.string.load_more))
                    pop()
                }

                ClickableText(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    onClick = { offset ->
                        annotatedString.getStringAnnotations("LOAD_MORE", offset, offset)
                            .firstOrNull()?.let {
                                onClick()
                            }
                    }
                )
            } else {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = stringResource(R.string.comment),
                    modifier = Modifier.clickable { onCommentClick() }
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("$commentCount", fontSize = 14.sp)
            }
        }
    }
}