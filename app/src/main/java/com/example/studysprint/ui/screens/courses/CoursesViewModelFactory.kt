package com.example.studysprint.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysprint.repository.StudyRepository

class CoursesViewModelFactory(private val repo: StudyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CoursesViewModel(repo) as T
    }
}