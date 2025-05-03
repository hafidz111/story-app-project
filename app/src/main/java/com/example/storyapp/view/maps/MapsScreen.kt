package com.example.storyapp.view.maps

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.storyapp.component.LocationSnackbarHost
import com.example.storyapp.component.StoryMarkers
import com.example.storyapp.component.rememberLocationPermissionLauncher
import com.example.storyapp.view.ViewModelFactory
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.storyapp.data.Result
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.utils.moveToUserLocation
import com.example.storyapp.utils.showLocationPermissionSnackbar
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    viewModel: MapsViewModel = viewModel(factory = ViewModelFactory.getInstance(LocalContext.current)),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val storyResult by viewModel.storyWithLocation.observeAsState(Result.Loading)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-7.5, 110.0), 15f)
    }

    var locationPermissionGranted by remember { mutableStateOf<Boolean?>(null) }
    var permissionDeniedHandled by remember { mutableStateOf(false) }
    var hasMovedToUserLocation by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val launcher = rememberLocationPermissionLauncher { granted ->
        locationPermissionGranted = granted
        if (!granted && !permissionDeniedHandled) {
            permissionDeniedHandled = true
            scope.launch {
                withTimeoutOrNull(5000) {
                    showLocationPermissionSnackbar(snackbarHostState, context)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

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
                val fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Story Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            LocationSnackbarHost(snackbarHostState)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                if (locationPermissionGranted == true && storyResult is Result.Success) {
                    StoryMarkers(
                        stories = (storyResult as Result.Success<List<ListStoryItem>>).data,
                        cameraPositionState = cameraPositionState
                    )
                }

                if (locationPermissionGranted == true && storyResult is Result.Error) {
                    Toast.makeText(context, (storyResult as Result.Error).error, Toast.LENGTH_SHORT).show()
                }
            }

            if (locationPermissionGranted == true && storyResult is Result.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
        }
    }
}