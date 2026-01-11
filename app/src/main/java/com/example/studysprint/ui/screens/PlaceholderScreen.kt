package com.example.studysprint.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun PlaceholderScreen(@StringRes titleRes: Int, padding: PaddingValues) {
    Text(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        text = stringResource(titleRes)
    )
}