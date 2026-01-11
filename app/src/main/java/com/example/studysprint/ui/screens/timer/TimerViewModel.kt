package com.example.studysprint.ui.screens.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.data.local.entity.ExamEntity
import com.example.studysprint.repository.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class TimerPhase { FOCUS, BREAK }

enum class Preset(val focusSeconds: Int, val breakSeconds: Int) {
    STANDARD(25 * 60, 5 * 60),
    LONG(50 * 60, 10 * 60),
    DEMO(10, 5)
}

data class TimerUiState(
    val courses: List<CourseEntity> = emptyList(),
    val exams: List<ExamEntity> = emptyList(),
    val selectedCourseId: Long? = null,
    val selectedExamId: Long? = null,

    val preset: Preset = Preset.STANDARD,
    val phase: TimerPhase = TimerPhase.FOCUS,
    val remainingSeconds: Int = Preset.STANDARD.focusSeconds,
    val isRunning: Boolean = false
)

class TimerViewModel(private val repo: StudyRepository) : ViewModel() {

    private val internal = MutableStateFlow(TimerUiState())

    private val selectedCourseIdFlow = internal.map { it.selectedCourseId }.distinctUntilChanged()

    private val examsFlow: Flow<List<ExamEntity>> =
        selectedCourseIdFlow.flatMapLatest { courseId ->
            if (courseId == null) flowOf(emptyList())
            else repo.observeExamsForCourse(courseId)
        }

    val uiState: StateFlow<TimerUiState> =
        combine(repo.observeCourses(), examsFlow, internal) { courses, exams, state ->
            state.copy(courses = courses, exams = exams)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimerUiState())

    private var ticker: Job? = null

    fun setCourse(courseId: Long) {
        internal.update { it.copy(selectedCourseId = courseId, selectedExamId = null) }
    }

    fun setExam(examId: Long?) {
        internal.update { it.copy(selectedExamId = examId) }
    }

    fun setPreset(preset: Preset) {
        ticker?.cancel()
        internal.update {
            it.copy(
                preset = preset,
                phase = TimerPhase.FOCUS,
                remainingSeconds = preset.focusSeconds,
                isRunning = false
            )
        }
    }

    fun startPause() {
        val s = internal.value
        if (s.selectedCourseId == null) return

        if (s.isRunning) {
            ticker?.cancel()
            internal.update { it.copy(isRunning = false) }
            return
        }

        internal.update { it.copy(isRunning = true) }
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (isActive && internal.value.isRunning) {
                delay(1_000)
                internal.update { cur ->
                    val next = cur.remainingSeconds - 1
                    if (next > 0) cur.copy(remainingSeconds = next)
                    else onPhaseEnd(cur)
                }
            }
        }
    }

    private fun onPhaseEnd(cur: TimerUiState): TimerUiState {
        return when (cur.phase) {
            TimerPhase.FOCUS -> cur.copy(
                phase = TimerPhase.BREAK,
                remainingSeconds = cur.preset.breakSeconds
            )
            TimerPhase.BREAK -> cur.copy(
                isRunning = false,
                remainingSeconds = 0
            )
        }
    }

    fun reset() {
        ticker?.cancel()
        val preset = internal.value.preset
        internal.update {
            it.copy(
                phase = TimerPhase.FOCUS,
                remainingSeconds = preset.focusSeconds,
                isRunning = false
            )
        }
    }
}