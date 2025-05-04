package com.example.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.storyapp.common.Result
import com.example.storyapp.data.database.StoryDatabase
import com.example.storyapp.data.model.Comment
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.response.Story
import com.example.storyapp.data.remote.retrofit.ApiService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {
    suspend fun login(email: String, password: String): Result<UserModel> {
        return try {
            val response = apiService.login(email, password)
            if (response.error == false) {
                val loginResult = response.loginResult
                val user = UserModel(
                    name = loginResult?.name ?: "",
                    email = email,
                    token = loginResult?.token ?: "",
                    isLogin = true,
                    userId = loginResult?.userId ?: ""
                )
                Log.d("UserRepository", "Login berhasil, user: $user")
                saveSession(user)
                Result.Success(user)
            } else {
                Result.Error(response.message ?: "Login gagal")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorBody, LoginResponse::class.java).message
            Result.Error(errorMessage.toString())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan")
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<String> {
        return try {
            val response = apiService.register(name, email, password)
            if (response.error == false) {
                Result.Success(response.message ?: "Berhasil daftar")
            } else {
                Result.Error(response.message ?: "Gagal daftar")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorBody, RegisterResponse::class.java).message
            Result.Error(errorMessage.toString())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Terjadi kesalahan")
        }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> = userPreference.getSession()


    suspend fun logout() {
        userPreference.logout()
    }

    fun getStories(token: String): Flow<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = { storyDatabase.storyDao().getAllStory() }
        ).flow
    }

    suspend fun getDetailStory(token: String, id: String): Result<Story> {
        return try {
            val response = apiService.getDetailStory("Bearer $token", id)
            if (response.error == false) {
                val story = response.story ?: return Result.Error("Cerita tidak ditemukan")
                Result.Success(story)
            } else {
                Result.Error(response.message ?: "Gagal mengambil detail cerita")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorBody, DetailStoryResponse::class.java).message
            Result.Error(errorMessage.toString())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Gagal mengambil detail cerita")
        }
    }

    suspend fun uploadStory(token: String, file: File, description: String): Result<String> {
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", file.name, requestImageFile
        )
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())

        return try {
            val response =
                apiService.uploadStories("Bearer $token", imageMultipart, descriptionBody)

            if (response.error == false) {
                Result.Success(response.message ?: "Upload berhasil")
            } else {
                Result.Error(response.message ?: "Upload gagal")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = Gson().fromJson(errorBody, RegisterResponse::class.java).message
            Result.Error(errorMessage.toString())
        }
    }

    suspend fun getStoriesWithLocation(token: String): Result<List<ListStoryItem>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStoriesWithLocation("Bearer $token")

                if (response.error == false) {
                    Result.Success(response.listStory)
                } else {
                    Result.Error(response.message ?: "Gagal memuat cerita dengan lokasi")
                }
            } catch (e: Exception) {
                Log.e("Repository", "Error: ${e.message}")
                Result.Error(e.message ?: "Gagal memuat cerita dengan lokasi")
            }
        }

    fun saveUserProfile(userId: String, name: String) {
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
        val userMap = mapOf(
            "name" to name
        )
        databaseRef.setValue(userMap)
            .addOnSuccessListener {
                Log.d("Firebase", "User profile saved successfully.")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to save user profile", e)
            }
    }

    fun getUserProfile(userId: String, callback: (String?) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("users").child(userId)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("getUserProfile", "Snapshot: ${snapshot.value}")
                val name = snapshot.child("name").getValue(String::class.java)
                callback(name)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getComments(storyId: String): DatabaseReference {
        return FirebaseDatabase.getInstance()
            .getReference("comments")
            .child(storyId)
    }

    fun postComment(storyId: String, comment: Comment) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("comments")
            .child(storyId)
            .push()
        val commentId = ref.key ?: return
        val commentWithId = comment.copy(commentId = commentId)
        ref.setValue(commentWithId)
    }

    fun deleteComment(storyId: String, comment: Comment) {
        val commentId = comment.commentId
        if (commentId.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference("comments")
                .child(storyId)
                .child(commentId)
                .removeValue()
                .addOnSuccessListener {
                    Log.d("Firebase", "Comment deleted successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Failed to delete comment", e)
                }
        }
    }

    fun getCommentCount(storyId: String): LiveData<Int> {
        return object : LiveData<Int>() {
            private val ref = FirebaseDatabase.getInstance()
                .getReference("comments")
                .child(storyId)

            private val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    value = snapshot.childrenCount.toInt()
                }

                override fun onCancelled(error: DatabaseError) {
                    value = 0
                }
            }

            override fun onActive() {
                ref.addValueEventListener(listener)
            }

            override fun onInactive() {
                ref.removeEventListener(listener)
            }
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService, storyDatabase)
            }.also { instance = it }
    }
}