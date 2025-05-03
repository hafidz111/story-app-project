package com.example.storyapp.view.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.storyapp.R
import com.example.storyapp.component.CommentItem
import com.example.storyapp.component.PostComment
import com.example.storyapp.component.StoryDetail
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
                    StoryDetail(story = story, commentCount = commentCount)

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
                        .padding(16.dp)
                )
            }
        }
    }
}