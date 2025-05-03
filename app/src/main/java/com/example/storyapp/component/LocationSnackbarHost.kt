package com.example.storyapp.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.storyapp.R

@Composable
fun LocationSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
            action = {
                TextButton(onClick = { snackbarHostState.currentSnackbarData?.performAction() }) {
                    Text(
                        data.visuals.actionLabel ?: "",
                        color = colorResource(id = R.color.navy_200)
                    )
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(data.visuals.message)
        }
    }
}