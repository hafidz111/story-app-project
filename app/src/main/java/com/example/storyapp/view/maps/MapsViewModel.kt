package com.example.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.storyapp.data.Result
import kotlinx.coroutines.flow.first

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    private val _storyWithLocation = MutableStateFlow<Result<List<ListStoryItem>>>(Result.Loading)
    val storyWithLocation: LiveData<Result<List<ListStoryItem>>> = _storyWithLocation.asLiveData()

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            _storyWithLocation.value = Result.Loading
            try {
                repository.getSession().first { it.token.isNotEmpty() }.let { user ->
                    val result = repository.getStoriesWithLocation(user.token)
                    _storyWithLocation.value = result
                }

            } catch (e: Exception) {
                _storyWithLocation.value = Result.Error(e.message ?: "Maps gagal")
            }

        }
    }
}