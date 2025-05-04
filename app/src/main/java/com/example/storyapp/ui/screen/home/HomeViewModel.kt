package com.example.storyapp.ui.screen.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val repository: UserRepository) : ViewModel() {
    private var currentStories: Flow<PagingData<ListStoryItem>>? = null

    fun getStories(token: String): Flow<PagingData<ListStoryItem>> {
        if (currentStories == null) {
            currentStories = repository.getStories(token).cachedIn(viewModelScope)
        }
        return currentStories!!
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getCommentCount(storyId: String): LiveData<Int> {
        return repository.getCommentCount(storyId)
    }
}