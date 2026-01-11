package com.example.studysprint.ui.screens.timer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysprint.R
import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.data.local.entity.ExamEntity
import com.example.studysprint.repository.StudyRepository
import com.example.studysprint.util.formatMmSs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(repository: StudyRepository, padding: PaddingValues) {
    var note by remember { mutableStateOf("") }
    var readiness by remember { mutableStateOf<Int?>(null) }

    val vm: TimerViewModel = viewModel(factory = TimerViewModelFactory(repository))
    val state by vm.uiState.collectAsState()

    if (state.showCompletionDialog) {
        val canSave = state.selectedExamId == null || readiness != null

        AlertDialog(
            onDismissRequest = {
                vm.dismissDialog()
                note = ""
                readiness = null
            },
            title = { Text(stringResource(R.string.session_completed)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(stringResource(R.string.note_optional)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    if (state.selectedExamId != null) {
                        Text(stringResource(R.string.ready_question))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
                TextButton(
                    enabled = canSave,
                    onClick = {
                        vm.saveSession(
                            note = note,
                            readiness = if (state.selectedExamId != null) readiness else null
                        )
                        note = ""
                        readiness = null
                    }
                ) { Text(stringResource(R.string.save_session)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    vm.dismissDialog()
                    note = ""
                    readiness = null
                }) { Text(stringResource(R.string.cancel)) }
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

            // Selections
            SectionCard {
                CourseDropdown(
                    courses = state.courses,
                    selectedCourseId = state.selectedCourseId,
                    onSelect = vm::setCourse
                )

                Spacer(Modifier.height(10.dp))

                ExamDropdown(
                    exams = state.exams,
                    selectedExamId = state.selectedExamId,
                    onSelect = vm::setExam
                )

                if (state.selectedCourseId == null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.course_required),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Timer
            SectionCard {
                Text(
                    text = if (state.phase == TimerPhase.FOCUS)
                        stringResource(R.string.focus)
                    else
                        stringResource(R.string.break_time),
                    style = MaterialTheme.typography.titleLarge
                )

                // ✅ Round indicator
                Text(
                    text = stringResource(R.string.round_of, state.currentRound, state.roundsTarget),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val total =
                    if (state.phase == TimerPhase.FOCUS) state.preset.focusSeconds else state.preset.breakSeconds
                val progress = (state.remainingSeconds.toFloat() / total.toFloat()).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    text = formatMmSs(state.remainingSeconds.toLong()),
                    style = MaterialTheme.typography.displayMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = state.selectedCourseId != null,
                        onClick = vm::startPause
                    ) {
                        Text(if (state.isRunning) stringResource(R.string.pause) else stringResource(R.string.start))
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = vm::reset
                    ) {
                        Text(stringResource(R.string.reset))
                    }
                }
            }

            // Preset + Rounds
            SectionCard {
                val scroll = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scroll),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.preset == Preset.STANDARD,
                        onClick = { vm.setPreset(Preset.STANDARD) },
                        label = { Text(stringResource(R.string.preset_standard)) }
                    )
                    FilterChip(
                        selected = state.preset == Preset.LONG,
                        onClick = { vm.setPreset(Preset.LONG) },
                        label = { Text(stringResource(R.string.preset_long)) }
                    )
                    FilterChip(
                        selected = state.preset == Preset.DEMO,
                        onClick = { vm.setPreset(Preset.DEMO) },
                        label = { Text(stringResource(R.string.preset_demo)) }
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ✅ Rounds selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.rounds),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            enabled = !state.isRunning && state.roundsTarget > 1,
                            onClick = { vm.setRoundsTarget(state.roundsTarget - 1) }
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null)
                        }

                        Text(
                            text = state.roundsTarget.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )

                        IconButton(
                            enabled = !state.isRunning && state.roundsTarget < 8,
                            onClick = { vm.setRoundsTarget(state.roundsTarget + 1) }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun CourseDropdown(
    courses: List<CourseEntity>,
    selectedCourseId: Long?,
    onSelect: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = courses.firstOrNull { it.id == selectedCourseId }?.name ?: ""

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedName,
            onValueChange = {},
            label = { Text(stringResource(R.string.select_course)) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(role = Role.Button) { expanded = !expanded }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
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

@Composable
private fun ExamDropdown(
    exams: List<ExamEntity>,
    selectedExamId: Long?,
    onSelect: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedTitle = exams.firstOrNull { it.id == selectedExamId }?.title ?: ""

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedTitle,
            onValueChange = {},
            label = { Text(stringResource(R.string.select_exam_optional)) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(role = Role.Button) { expanded = !expanded }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
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