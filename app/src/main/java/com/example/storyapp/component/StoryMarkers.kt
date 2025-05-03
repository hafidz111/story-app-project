package com.example.storyapp.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.storyapp.data.remote.response.ListStoryItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun StoryMarkers(
    stories: List<ListStoryItem>,
    cameraPositionState: CameraPositionState
) {
    val boundsBuilder = LatLngBounds.Builder()

    stories.forEach { story ->
        val latLng = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
        Marker(
            state = MarkerState(position = latLng),
            title = story.name,
            snippet = story.description
        )
        boundsBuilder.include(latLng)
    }

    if (stories.isNotEmpty()) {
        val bounds = boundsBuilder.build()
        LaunchedEffect(stories) {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngBounds(bounds, 100)
            )
        }
    }
}