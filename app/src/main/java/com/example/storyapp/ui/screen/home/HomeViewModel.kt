package com.example.storyapp.ui.screen.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.UserRepository
import com.example.storyapp.data.model.Comment
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.ListStoryItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val repository: UserRepository) : ViewModel() {
    private var currentStories: Flow<PagingData<ListStoryItem>>? = null

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

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
        repository.getUserProfile(userId) { name, _, _ ->
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
}