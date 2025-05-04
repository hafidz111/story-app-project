package com.example.storyapp.ui.screen.maps

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyapp.ui.component.StoryMarkers
import com.example.storyapp.ui.ViewModelFactory
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.storyapp.common.UiState
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.utils.moveToUserLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

@Composable
fun MapsScreen(
    viewModel: MapsViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val storyUiState by viewModel.storyWithLocation.observeAsState(UiState.Loading)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-7.5, 110.0), 9f)
    }

    var locationPermissionGranted by remember { mutableStateOf<Boolean?>(null) }
    var hasMovedToUserLocation by remember { mutableStateOf(false) }


    if (locationPermissionGranted == true) {
        LaunchedEffect(Unit) {
            viewModel.getStoriesWithLocation()

            if (!hasMovedToUserLocation) {
                val moved = moveToUserLocation(context, fusedLocationClient, cameraPositionState)
                hasMovedToUserLocation = moved
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val fineGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val coarseGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val granted = fineGranted || coarseGranted
                if (granted && locationPermissionGranted != true) {
                    locationPermissionGranted = true
                    viewModel.getStoriesWithLocation()
                }
            }
        }

        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                mapToolbarEnabled = true
            ),
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionGranted == true
            )
        ) {
            if (locationPermissionGranted == true && storyUiState is UiState.Success) {
                StoryMarkers(
                    stories = (storyUiState as UiState.Success<List<ListStoryItem>>).data,
                    cameraPositionState = cameraPositionState
                )
            }

            if (locationPermissionGranted == true && storyUiState is UiState.Error) {
                Toast.makeText(context, (storyUiState as UiState.Error).error, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (locationPermissionGranted == true && storyUiState is UiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }
    }
}