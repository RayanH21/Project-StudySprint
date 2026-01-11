package com.example.studysprint.ui.screens.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysprint.R
import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.data.local.entity.ExamEntity
import com.example.studysprint.repository.StudyRepository
import com.example.studysprint.util.formatSeconds
import androidx.compose.foundation.layout.Column

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(repository: StudyRepository, padding: PaddingValues) {
    var note by remember { mutableStateOf("") }
    var readiness by remember { mutableStateOf<Int?>(null) }
    val vm: TimerViewModel = viewModel(factory = TimerViewModelFactory(repository))
    val state by vm.uiState.collectAsState()

    if (state.showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { vm.dismissDialog() },
            title = { Text(stringResource(R.string.session_completed)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(stringResource(R.string.note_optional)) }
                    )

                    if (state.selectedExamId != null) {
                        Text(stringResource(R.string.ready_question))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = readiness == 1,
                                onClick = { readiness = 1 },
                                label = { Text(stringResource(R.string.ready_yes)) }
                            )
                            FilterChip(
                                selected = readiness == 0,
                                onClick = { readiness = 0 },
                                label = { Text(stringResource(R.string.ready_no)) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.saveSession(
                        note = note,
                        readiness = if (state.selectedExamId != null) readiness else null
                    )
                    note = ""
                    readiness = null
                }) { Text(stringResource(R.string.save_session)) }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.timer_title)) }) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CourseDropdown(
                courses = state.courses,
                selectedCourseId = state.selectedCourseId,
                onSelect = vm::setCourse
            )

            ExamDropdown(
                exams = state.exams,
                selectedExamId = state.selectedExamId,
                onSelect = vm::setExam
            )

            PresetsRow(selected = state.preset, onSelect = vm::setPreset)

            Text(
                text = if (state.phase == TimerPhase.FOCUS) stringResource(R.string.focus) else stringResource(R.string.break_time),
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = formatSeconds(state.remainingSeconds.toLong()),
                style = MaterialTheme.typography.displayMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    enabled = state.selectedCourseId != null,
                    onClick = vm::startPause
                ) {
                    Text(if (state.isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
                }
                OutlinedButton(onClick = vm::reset) {
                    Text(stringResource(R.string.reset))
                }
            }

            if (state.selectedCourseId == null) {
                Text(stringResource(R.string.course_required))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseDropdown(
    courses: List<CourseEntity>,
    selectedCourseId: Long?,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = courses.firstOrNull { it.id == selectedCourseId }?.name

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selectedName ?: "",
            onValueChange = {},
            label = { Text(stringResource(R.string.select_course)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text(course.name) },
                    onClick = {
                        onSelect(course.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamDropdown(
    exams: List<ExamEntity>,
    selectedExamId: Long?,
    onSelect: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTitle = exams.firstOrNull { it.id == selectedExamId }?.title

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selectedTitle ?: "",
            onValueChange = {},
            label = { Text(stringResource(R.string.select_exam_optional)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.none)) },
                onClick = {
                    onSelect(null)
                    expanded = false
                }
            )
            exams.forEach { exam ->
                DropdownMenuItem(
                    text = { Text(exam.title) },
                    onClick = {
                        onSelect(exam.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PresetsRow(selected: Preset, onSelect: (Preset) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == Preset.STANDARD,
            onClick = { onSelect(Preset.STANDARD) },
            label = { Text(stringResource(R.string.preset_standard)) }
        )
        FilterChip(
            selected = selected == Preset.LONG,
            onClick = { onSelect(Preset.LONG) },
            label = { Text(stringResource(R.string.preset_long)) }
        )
        FilterChip(
            selected = selected == Preset.DEMO,
            onClick = { onSelect(Preset.DEMO) },
            label = { Text(stringResource(R.string.preset_demo)) }
        )
    }
}