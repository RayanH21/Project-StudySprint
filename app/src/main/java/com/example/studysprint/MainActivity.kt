package com.example.studysprint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.studysprint.ui.navigation.AppScaffold
import com.example.studysprint.ui.theme.StudySprintTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repo = (application as StudySprintApplication).repository

        setContent {
            StudySprintTheme {
                AppScaffold(repository = repo)
            }
        }
    }
}