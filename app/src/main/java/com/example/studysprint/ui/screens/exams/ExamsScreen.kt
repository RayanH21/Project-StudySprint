package com.example.studysprint.ui.screens.exams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysprint.R
import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.repository.StudyRepository
import com.example.studysprint.util.epochDayToLocalDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamsScreen(
    repository: StudyRepository,
    padding: PaddingValues
) {
    val vm: ExamsViewModel = viewModel(factory = ExamsViewModelFactory(repository))
    val courses by vm.courses.collectAsState()
    val exams by vm.exams.collectAsState()

    var title by rememberSaveable { mutableStateOf("") }
    var selectedCourseId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * 86_400_000L
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) {
                        val epochDay = millis / 86_400_000L
                        selectedDate = epochDayToLocalDate(epochDay)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = state) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.exams_title)) }) }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.exam_title_hint)) },
                    singleLine = true
                )
            }

            item {
                CourseDropdown(
                    courses = courses,
                    selectedCourseId = selectedCourseId,
                    onSelect = { selectedCourseId = it }
                )
            }

            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "${stringResource(R.string.exam_date)}: $selectedDate")
                    TextButton(onClick = { showDatePicker = true }) {
                        Text(stringResource(R.string.pick))
                    }
                }
            }

            item {
                Button(
                    enabled = selectedCourseId != null,
                    onClick = {
                        vm.addExam(title, selectedDate.toEpochDay(), selectedCourseId!!)
                        title = ""
                    }
                ) { Text(stringResource(R.string.add_exam)) }
            }

            items(exams, key = { it.id }) { exam ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${exam.title} • ${epochDayToLocalDate(exam.dateEpochDay)} • ${exam.courseName}"
                    )
                    IconButton(onClick = { vm.deleteExam(exam.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                    }
                }
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
            label = { Text(stringResource(R.string.linked_course)) },
            placeholder = { Text(stringResource(R.string.select_course)) },
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