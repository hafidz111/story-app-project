package com.example.storyapp.ui.screen.home

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.storyapp.R
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.component.CommentItem
import com.example.storyapp.ui.component.PostComment
import com.example.storyapp.ui.component.RetrySection
import com.example.storyapp.ui.component.StoryItem
import com.example.storyapp.ui.component.rememberLocationPermissionLauncher
import com.example.storyapp.utils.showLocationPermissionSnackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    navToDetail: (String) -> Unit
) {
    val user by viewModel.getSession().observeAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var locationPermissionGranted by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var permissionDeniedHandled by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val sheetState = rememberModalBottomSheetState()
    val listState = rememberLazyListState()
    var selectedStoryId by rememberSaveable { mutableStateOf<String?>(null) }
    val comments by viewModel.comments.collectAsState()

    val launcher = rememberLocationPermissionLauncher { granted ->
        locationPermissionGranted = granted
        if (!granted && !permissionDeniedHandled) {
            permissionDeniedHandled = true
            scope.launch {
                withTimeoutOrNull(5000) {
                    showLocationPermissionSnackbar(snackbarHostState, context)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(comments.size) {
        if (comments.isNotEmpty()) {
            listState.animateScrollToItem(comments.size - 1)
        }
    }

    user?.takeIf { it.isLogin && it.token.isNotEmpty() }?.let { loggedUser ->

        val storyItems = remember(loggedUser.token) {
            viewModel.getStories(loggedUser.token)
        }.collectAsLazyPagingItems()

        val isRefreshing = storyItems.loadState.refresh is LoadState.Loading

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            selectedStoryId?.let { storyId ->
                val commentCount by viewModel.getCommentCount(selectedStoryId ?: "")
                    .observeAsState(0)
                ModalBottomSheet(
                    onDismissRequest = {
                        selectedStoryId = null
                    },
                    sheetState = sheetState,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.48f)
                            .fillMaxWidth()
                            .imePadding()
                    ) {
                        Text(
                            text = stringResource(R.string.comments_count, commentCount),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .align(Alignment.TopCenter)
                        )

                        if (comments.isEmpty()) {
                            Text(
                                text = stringResource(R.string.be_the_first_to_comment),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                textAlign = TextAlign.Center,
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 32.dp),
                                state = listState,
                            ) {
                                items(comments) { comment ->
                                    CommentItem(
                                        userName = comment.name,
                                        message = comment.message,
                                        timestamp = comment.timestamp,
                                        commentUserId = comment.userId,
                                        currentUserId = user?.userId ?: "",
                                        onDeleteClick = {
                                            viewModel.deleteComment(storyId, comment)
                                        }
                                    )
                                }
                            }
                        }

                        PostComment(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp),
                        ) { message ->
                            val userId = user?.userId ?: return@PostComment
                            viewModel.sendComment(storyId, userId, message)
                        }
                    }
                }
            }

            if (isRefreshing && storyItems.itemCount == 0) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(storyItems.itemCount) { index ->
                        val story = storyItems[index]
                        if (story != null) {
                            val commentCount by viewModel.getCommentCount(story.id)
                                .observeAsState(0)
                            StoryItem(
                                story = story,
                                commentCount = commentCount,
                                onClick = { navToDetail(story.id) },
                                onCommentClick = {
                                    if (selectedStoryId != story.id) {
                                        selectedStoryId = story.id
                                        viewModel.observeComments(story.id)
                                    }
                                    scope.launch { sheetState.show() }
                                }
                            )
                        }
                    }

                    if (storyItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    if (storyItems.loadState.append is LoadState.Error ||
                        storyItems.loadState.refresh is LoadState.Error
                    ) {
                        item {
                            val error =
                                (storyItems.loadState.refresh as? LoadState.Error)?.error
                            Log.e("HomeScreen", "Error loading stories", error)
                            RetrySection { storyItems.retry() }
                        }
                    }
                }
            }
        }
    }
}