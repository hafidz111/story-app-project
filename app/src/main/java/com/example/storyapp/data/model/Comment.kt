package com.example.storyapp.data.model

data class Comment(
    val commentId: String = "",
    val userId: String = "",
    val name: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)