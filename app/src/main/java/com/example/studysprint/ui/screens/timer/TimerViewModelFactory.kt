package com.example.studysprint.ui.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studysprint.repository.StudyRepository

class TimerViewModelFactory(private val repo: StudyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TimerViewModel(repo) as T
    }
}