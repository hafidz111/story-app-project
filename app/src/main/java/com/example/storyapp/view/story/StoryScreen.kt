package com.example.storyapp.view.story

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.component.DescriptionInputField
import com.example.storyapp.component.ImagePickerButtons
import com.example.storyapp.component.ImagePreview
import com.example.storyapp.component.UploadButton
import com.example.storyapp.data.Result
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.uriToFile
import com.example.storyapp.view.main.MainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    viewModel: StoryViewModel
) {
    val context = LocalContext.current
    val imageUri by viewModel.imageUri.collectAsState()
    val description = remember { mutableStateOf("") }
    val navy = Color(ContextCompat.getColor(context, R.color.navy_500))

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.setImageUri(uri) }
    )

    val tempCameraUri = remember { mutableStateOf<Uri?>(null) }

    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.setImageUri(tempCameraUri.value)
    }

    val result by viewModel.uploadResult.observeAsState(Result.Idle)
    val activity = (context as? Activity)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_story)) },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.back
                            )
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
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
                enabled = result !is Result.Loading,
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
                            val file = uriToFile(imageUri!!, context)
                            viewModel.uploadStory(file, description.value)
                        }
                    }
                },
                navy = navy
            )

            if (result is Result.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LaunchedEffect(result) {
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(
                            context,
                            (result as Result.Success<String>).data,
                            Toast.LENGTH_SHORT
                        ).show()
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as? Activity)?.finish()
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            context,
                            context.getString(
                                R.string.upload_failed,
                                (result as Result.Error).error
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }
}