package com.example.storyapp.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.Result
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.model.Comment
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.Story
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    private val _storyResult = MutableStateFlow<Result<Story>>(Result.Idle)
    val storyResult: StateFlow<Result<Story>> = _storyResult

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            _storyResult.value = Result.Loading
            try {
                repository.getSession().first { it.token.isNotEmpty() }.let { user ->
                    val detail = repository.getDetailStory(user.token, id)
                    _storyResult.value = detail
                }
            } catch (e: Exception) {
                _storyResult.value = Result.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun observeComments(storyId: String) {
        repository.getComments(storyId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Firebase", "Comment snapshot: ${snapshot.value}")
                val commentList = snapshot.children.mapNotNull {
                    it.getValue(Comment::class.java)
                }
                _comments.value = commentList
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendComment(storyId: String, userId: String, message: String) {
        repository.getUserProfile(userId) { name ->
            val commenterName = name ?: "User"
            val comment = Comment(
                userId = userId,
                name = commenterName,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            repository.postComment(storyId, comment)
        }
    }

    fun deleteComment(storyId: String, comment: Comment) {
        repository.deleteComment(storyId, comment)
    }

    fun getCommentCount(storyId: String): LiveData<Int> {
        return repository.getCommentCount(storyId)
    }
}