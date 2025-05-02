package com.example.storyapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class StoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uploadResult = MutableStateFlow<Result<String>>(Result.Idle)
    val uploadResult: LiveData<Result<String>> = _uploadResult.asLiveData()

    fun uploadStory(file: File, description: String) {
        viewModelScope.launch {
            _uploadResult.value = Result.Loading
            try {
                repository.getSession().first { it.token.isNotEmpty() }.let { user ->
                    val result = repository.uploadStory(user.token, file, description)
                    _uploadResult.value = result
                }
            } catch (e: Exception) {
                _uploadResult.value = Result.Error(e.message ?: "Gagal upload cerita")
            }
        }
    }
}