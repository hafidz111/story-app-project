package com.example.storyapp.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyapp.R
import com.example.storyapp.common.UiState
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.component.CommentItem
import com.example.storyapp.ui.component.PostComment
import com.example.storyapp.ui.component.StoryDetail

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    storyId: String
) {
    val result by viewModel.storyUiState.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val commentCount by viewModel.getCommentCount(storyId).observeAsState(0)

    val user by viewModel.getSession().observeAsState()
    val userId = user?.userId.orEmpty()

    LaunchedEffect(storyId) {
        viewModel.getDetailStory(storyId)
        viewModel.observeComments(storyId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        when (result) {
            is UiState.Idle -> {}

            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is UiState.Success -> {
                val story = (result as UiState.Success<Story>).data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    StoryDetail(story = story, commentCount = commentCount)

                    Spacer(modifier = Modifier.height(12.dp))

                    if (comments.isEmpty()) {
                        Text(
                            text = stringResource(R.string.be_the_first_to_comment),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(vertical = 50.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
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
                                }
                            )
                        }
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

            is UiState.Error -> {
                val error = (result as UiState.Error).error
                Text(
                    text = stringResource(R.string.there_is_an_error, error),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}