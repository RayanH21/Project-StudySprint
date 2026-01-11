package com.example.studysprint.ui.screens.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.repository.StudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CoursesViewModel(private val repo: StudyRepository) : ViewModel() {

    val courses: StateFlow<List<CourseEntity>> =
        repo.observeCourses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addCourse(name: String) = viewModelScope.launch { repo.addCourse(name) }
    fun deleteCourse(courseId: Long) = viewModelScope.launch { repo.deleteCourse(courseId) }
    fun setCompleted(courseId: Long, completed: Boolean) =
        viewModelScope.launch { repo.setCourseCompleted(courseId, completed) }
}