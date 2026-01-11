package com.example.studysprint.ui.screens.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysprint.R
import com.example.studysprint.repository.StudyRepository
import androidx.compose.runtime.saveable.rememberSaveable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    repository: StudyRepository,
    padding: PaddingValues
) {
    val vm: CoursesViewModel = viewModel(factory = CoursesViewModelFactory(repository))
    val courses by vm.courses.collectAsState()

    var input by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.courses_title)) }) }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = input,
                        onValueChange = { input = it },
                        label = { Text(stringResource(R.string.course_name_hint)) },
                        singleLine = true
                    )
                    Button(onClick = {
                        vm.addCourse(input)
                        input = ""
                    }) { Text(stringResource(R.string.add_course)) }
                }
            }

            if (courses.isEmpty()) {
                item { Text(stringResource(R.string.no_courses)) }
            }

            items(courses, key = { it.id }) { course ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Checkbox(
                        checked = course.isCompleted,
                        onCheckedChange = { vm.setCompleted(course.id, it) }
                    )
                    Text(
                        modifier = Modifier.weight(1f).padding(top = 12.dp),
                        text = course.name
                    )
                    IconButton(onClick = { vm.deleteCourse(course.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                    }
                }
            }

            item { Spacer(Modifier.height(6.dp)) }
        }
    }
}