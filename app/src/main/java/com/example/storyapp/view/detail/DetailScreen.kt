package com.example.storyapp.view.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.storyapp.R
import com.example.storyapp.component.CommentItem
import com.example.storyapp.component.PostComment
import com.example.storyapp.component.UserStory
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.response.Story

@Composable
fun DetailScreen(
    viewModel: DetailViewModel, storyId: String
) {
    val result by viewModel.storyResult.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val commentCount by viewModel.getCommentCount(storyId).observeAsState(0)

    val user by viewModel.getSession().observeAsState()
    val userId = user?.userId.orEmpty()

    LaunchedEffect(storyId) {
        viewModel.observeComments(storyId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        when (result) {
            is Result.Idle -> {}

            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is Result.Success -> {
                val story = (result as Result.Success<Story>).data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    UserStory(story)

                    Spacer(modifier = Modifier.height(12.dp))

                    AsyncImage(
                        model = story.photoUrl ?: "",
                        contentDescription = stringResource(R.string.image_story),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = story.description ?: "-", style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = stringResource(R.string.comment),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$commentCount", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.comments_count, commentCount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(top = 12.dp))

                    comments.forEach { comment ->
                        CommentItem(
                            commentUserId = comment.userId,
                            currentUserId = userId,
                            userName = comment.name,
                            message = comment.message,
                            timestamp = comment.timestamp,
                            onDeleteClick = {
                                if (comment.userId == userId) {
                                    viewModel.deleteComment(storyId, comment)
                                }
                            })
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp)
                ) {
                    PostComment(
                        onPost = { message ->
                            viewModel.sendComment(storyId, userId, message)
                        })
                }
            }

            is Result.Error -> {
                val error = (result as Result.Error).error
                Text(
                    text = stringResource(R.string.there_is_an_error, error),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}