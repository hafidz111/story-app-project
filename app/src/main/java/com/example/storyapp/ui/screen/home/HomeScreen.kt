package com.example.storyapp.ui.screen.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.storyapp.ui.component.RetrySection
import com.example.storyapp.ui.component.StoryItem
import com.example.storyapp.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = ViewModelFactory.getInstance(LocalContext.current)),
    navToDetail: (String) -> Unit,
    navToUpload: () -> Unit
) {
    val user by viewModel.getSession().observeAsState()

    user?.takeIf { it.isLogin && it.token.isNotEmpty() }?.let { loggedUser ->

        val storyItems = remember(loggedUser.token) {
            viewModel.getStories(loggedUser.token)
        }.collectAsLazyPagingItems()

        val isRefreshing = storyItems.loadState.refresh is LoadState.Loading

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Stories") }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = navToUpload) {
                    Icon(Icons.Default.Add, contentDescription = "Upload Story")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
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
                                    onClick = { navToDetail(story.id) }
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
}