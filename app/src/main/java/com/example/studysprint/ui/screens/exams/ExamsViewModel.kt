package com.example.studysprint.ui.screens.exams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.data.local.projections.ExamWithCourseName
import com.example.studysprint.repository.StudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamsViewModel(private val repo: StudyRepository) : ViewModel() {

    val courses: StateFlow<List<CourseEntity>> =
        repo.observeCourses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val exams: StateFlow<List<ExamWithCourseName>> =
        repo.observeExamsWithCourseName()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addExam(title: String, dateEpochDay: Long, courseId: Long) =
        viewModelScope.launch { repo.addExam(title, dateEpochDay, courseId) }

    fun deleteExam(examId: Long) =
        viewModelScope.launch { repo.deleteExam(examId) }
}