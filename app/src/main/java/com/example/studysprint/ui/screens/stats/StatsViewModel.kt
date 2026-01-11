package com.example.studysprint.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysprint.repository.StudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CourseStat(val name: String, val totalFocusSeconds: Long)
data class ExamStat(val title: String, val totalFocusSeconds: Long)
data class ExamReadinessStat(val title: String, val status: String)

data class StatsUiState(
    val courseStats: List<CourseStat> = emptyList(),
    val examStats: List<ExamStat> = emptyList(),
    val readinessStats: List<ExamReadinessStat> = emptyList()
)

class StatsViewModel(private val repo: StudyRepository) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        repo.observeCourses(),
        repo.observeExamsWithCourseName(),
        repo.observeCourseFocusTotals(),
        repo.observeExamFocusTotals(),
        repo.observeExamReadinessStatus()
    ) { courses, exams, courseTotals, examTotals, readinessStatuses ->

        val courseStats = courses.map { c ->
            val total = courseTotals.firstOrNull { it.courseId == c.id }?.totalFocusSeconds ?: 0L
            CourseStat(c.name, total)
        }.sortedByDescending { it.totalFocusSeconds }

        val examStats = exams.map { e ->
            val total = examTotals.firstOrNull { it.examId == e.id }?.totalFocusSeconds ?: 0L
            ExamStat(e.title, total)
        }.sortedByDescending { it.totalFocusSeconds }

        val readinessMap = readinessStatuses.associateBy({ it.examId }, { it.readiness })

        val readinessStats = exams.map { e ->
            val r = readinessMap[e.id] // 1 / 0 / null
            val status = when (r) {
                1 -> "Ready"
                0 -> "Not ready"
                else -> "No feedback yet"
            }
            ExamReadinessStat(e.title, status)
        }

        StatsUiState(courseStats, examStats, readinessStats)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())
}