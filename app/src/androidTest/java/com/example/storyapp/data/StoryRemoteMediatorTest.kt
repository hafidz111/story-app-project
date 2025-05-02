package com.example.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyapp.data.database.StoryDatabase
import com.example.storyapp.data.remote.response.DetailStoryResponse
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.data.remote.response.LoginResponse
import com.example.storyapp.data.remote.response.RegisterResponse
import com.example.storyapp.data.remote.response.StoryResponse
import com.example.storyapp.data.remote.response.UploadStoryResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
            token = "dummy_token"
        )
        val pagingState = PagingState<Int, ListStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}
class FakeApiService : ApiService {

    override suspend fun register(name: String, email: String, password: String): RegisterResponse {
        throw NotImplementedError("Not used in this test")
    }

    override suspend fun login(email: String, password: String): LoginResponse {
        throw NotImplementedError("Not used in this test")
    }

    override suspend fun getStories(token: String, page: Int, size: Int): StoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "2022-01-08T06:34:18.598Z",
                "User $i",
                "Description $i",
                lon = 0.0,
                id = "0",
                lat = 0.0
            )
            items.add(story)
        }
        return StoryResponse(items, false, "Stories fetched successfully")
    }

    override suspend fun getDetailStory(token: String, id: String): DetailStoryResponse {
        throw NotImplementedError("Not used in this test")
    }

    override suspend fun uploadStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): UploadStoryResponse {
        throw NotImplementedError("Not used in this test")
    }

    override suspend fun getStoriesWithLocation(token: String, location: Int): StoryResponse {
        throw NotImplementedError("Not used in this test")
    }
}