package com.example.storyapp.ui.screen.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.common.UiState
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.pref.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginUiState = MutableStateFlow<UiState<UserModel>>(UiState.Idle)
    val loginUiState: StateFlow<UiState<UserModel>> = _loginUiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginUiState.value = UiState.Loading
            try {
                val result = repository.login(email, password)
                _loginUiState.value = result
            } catch (e: Exception) {
                _loginUiState.value = UiState.Error(e.message ?: "Login gagal")
            }
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "saveSession dipanggil dengan user: $user")
            repository.saveSession(user)

        }
    }

    fun saveUserProfile(user: UserModel) {
        repository.saveUserProfile(user.userId, user.name, user.email, user.imageUrl)
    }
}