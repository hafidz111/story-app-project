package com.example.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    private val _registerResult = MutableStateFlow<Result<String>>(Result.Idle)
    val registerResult: LiveData<Result<String>> = _registerResult.asLiveData()

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = Result.Loading
            try {
                val result = repository.register(name, email, password)
                _registerResult.value = result

            } catch (e: Exception) {
                _registerResult.value = Result.Error(e.message ?: "Register gagal")
            }
        }
    }
}