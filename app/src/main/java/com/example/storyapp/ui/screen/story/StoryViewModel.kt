package com.example.storyapp.ui.screen.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.example.storyapp.common.UiState
import com.example.storyapp.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class StoryViewModel(private val repository: UserRepository) : ViewModel() {
    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _uploadUiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uploadUiState: LiveData<UiState<String>> = _uploadUiState.asLiveData()

    fun setImageUri(uri: Uri?) {
        _imageUri.value = uri
    }

    fun uploadStory(file: File, description: String) {
        viewModelScope.launch {
            _uploadUiState.value = UiState.Loading
            try {
                repository.getSession().first { it.token.isNotEmpty() }.let { user ->
                    val result = repository.uploadStory(user.token, file, description)
                    _uploadUiState.value = result
                }
            } catch (e: Exception) {
                _uploadUiState.value = UiState.Error(e.message ?: "Gagal upload cerita")
            }
        }
    }
}