package com.example.storyapp.ui.screen.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.common.UiState
import com.example.storyapp.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    private val _registerUiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val registerUiState: LiveData<UiState<String>> = _registerUiState.asLiveData()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerUiState.value = UiState.Loading
            try {
                val result = repository.register(name, email, password)
                _registerUiState.value = result

            } catch (e: Exception) {
                _registerUiState.value = UiState.Error(e.message ?: "Register gagal")
            }
        }
    }
}