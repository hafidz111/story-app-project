package com.example.storyapp.ui.screen.story

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.common.UiState
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.component.DescriptionInputField
import com.example.storyapp.ui.component.ImagePickerButtons
import com.example.storyapp.ui.component.ImagePreview
import com.example.storyapp.ui.component.UploadButton
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile

@Composable
fun StoryScreen(
    viewModel: StoryViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val context = LocalContext.current
    val imageUri by viewModel.imageUri.collectAsState()
    val description = rememberSaveable { mutableStateOf("") }
    val navy = Color(ContextCompat.getColor(context, R.color.navy_500))

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.setImageUri(uri) }
    )

    val tempCameraUri = rememberSaveable { mutableStateOf<Uri?>(null) }

    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.setImageUri(tempCameraUri.value)
    }

    val uiState by viewModel.uploadUiState.observeAsState(UiState.Idle)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ImagePreview(imageUri)

        ImagePickerButtons(
            onPickGallery = {
                launcherGallery.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onTakePhoto = {
                val uri = getImageUri(context)
                tempCameraUri.value = uri
                launcherCamera.launch(uri)
            },
            navy = navy
        )

        DescriptionInputField(description)

        UploadButton(
            enabled = uiState !is UiState.Loading,
            onClick = {
                when {
                    imageUri == null && description.value.isBlank() -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.fill_description_and_picture),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    imageUri == null -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.choose_image_first),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    description.value.isBlank() -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.fill_description_first),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        val file = uriToFile(imageUri!!, context).reduceFileImage()
                        viewModel.uploadStory(file, description.value)
                    }
                }
            },
            navy = navy
        )

        if (uiState is UiState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        LaunchedEffect(uiState) {
            when (uiState) {
                is UiState.Success -> {
                    Toast.makeText(
                        context,
                        (uiState as UiState.Success<String>).data,
                        Toast.LENGTH_SHORT
                    ).show()
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as? Activity)?.finish()
                }

                is UiState.Error -> {
                    Toast.makeText(
                        context,
                        context.getString(
                            R.string.upload_failed,
                            (uiState as UiState.Error).error
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }
    }
}