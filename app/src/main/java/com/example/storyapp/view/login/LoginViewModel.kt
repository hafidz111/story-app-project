package com.example.storyapp.view.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.pref.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableStateFlow<Result<UserModel>>(Result.Idle)
    val loginResult: StateFlow<Result<UserModel>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val result = repository.login(email, password)
                _loginResult.value = result
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "Login gagal")
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
        repository.saveUserProfile(user.userId, user.name)
    }
}