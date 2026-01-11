package com.example.studysprint.ui.screens.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysprint.R
import com.example.studysprint.repository.StudyRepository
import com.example.studysprint.ui.components.AppCard
import com.example.studysprint.ui.components.EmptyState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    repository: StudyRepository,
    padding: PaddingValues
) {
    val vm: CoursesViewModel = viewModel(factory = CoursesViewModelFactory(repository))
    val courses by vm.courses.collectAsState()

    var input by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    fun submit() {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return
        vm.addCourse(trimmed)
        input = ""
        focusManager.clearFocus()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.courses_title)) }) }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AppCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = input,
                            onValueChange = { input = it },
                            label = { Text(stringResource(R.string.course_name_hint)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { submit() })
                        )
                        Button(
                            modifier = Modifier.heightIn(min = 56.dp),
                            enabled = input.trim().isNotEmpty(),
                            onClick = { submit() }
                        ) {
                            Text(stringResource(R.string.add_course))
                        }
                    }
                }
            }

            if (courses.isEmpty()) {
                item { EmptyState(stringResource(R.string.no_courses)) }
            } else {
                items(courses, key = { it.id }) { course ->
                    AppCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = course.isCompleted,
                                onCheckedChange = { vm.setCompleted(course.id, it) }
                            )

                            val decoration =
                                if (course.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            val alpha = if (course.isCompleted) 0.6f else 1f

                            Text(
                                modifier = Modifier.weight(1f),
                                text = course.name,
                                textDecoration = decoration,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                                style = MaterialTheme.typography.bodyLarge
                            )

                            IconButton(onClick = { vm.deleteCourse(course.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(6.dp)) }
        }
    }
}