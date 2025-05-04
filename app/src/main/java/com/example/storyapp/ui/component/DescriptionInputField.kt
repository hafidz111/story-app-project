package com.example.storyapp.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.storyapp.R

@Composable
fun DescriptionInputField(description: MutableState<String>) {
    Column {
        Text(
            stringResource(R.string.story_description),
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text(stringResource(R.string.fill_description)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        )
    }
}