package com.example.storyapp.ui.screen.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.storyapp.common.UiState
import kotlinx.coroutines.flow.first

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    private val _storyWithLocation = MutableStateFlow<UiState<List<ListStoryItem>>>(UiState.Loading)
    val storyWithLocation: LiveData<UiState<List<ListStoryItem>>> = _storyWithLocation.asLiveData()

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            _storyWithLocation.value = UiState.Loading
            try {
                repository.getSession().first { it.token.isNotEmpty() }.let { user ->
                    val result = repository.getStoriesWithLocation(user.token)
                    _storyWithLocation.value = result
                }

            } catch (e: Exception) {
                _storyWithLocation.value = UiState.Error(e.message ?: "Maps gagal")
            }

        }
    }
}