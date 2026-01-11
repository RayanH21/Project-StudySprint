package com.example.studysprint.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studysprint.R
import com.example.studysprint.repository.StudyRepository
import com.example.studysprint.util.formatSeconds
import com.example.studysprint.ui.components.AppCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(repository: StudyRepository, padding: PaddingValues) {
    val vm: StatsViewModel = viewModel(factory = StatsViewModelFactory(repository))
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.stats_title)) }) }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.focus_by_course),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                AppCard {
                    if (state.courseStats.isEmpty()) {
                        Text(
                            text = "No study sessions yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        FlowChipsCourse(state)
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.focus_by_exam),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                AppCard {
                    if (state.examStats.isEmpty()) {
                        Text(
                            text = "No exam-linked sessions yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        FlowChipsExam(state)
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.exam_status_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                AppCard {
                    if (state.readinessStats.isEmpty()) {
                        Text(
                            text = "No readiness feedback yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        FlowChipsReadiness(state)
                    }
                }
            }
        }
    }
}

@Composable
private fun FlowChipsCourse(state: StatsUiState) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.courseStats.forEach { s ->
            AssistChip(
                onClick = { },
                label = { Text("${s.name}: ${formatSeconds(s.totalFocusSeconds)}") }
            )
        }
    }
}

@Composable
private fun FlowChipsExam(state: StatsUiState) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.examStats.forEach { s ->
            AssistChip(
                onClick = { },
                label = { Text("${s.title}: ${formatSeconds(s.totalFocusSeconds)}") }
            )
        }
    }
}

@Composable
private fun FlowChipsReadiness(state: StatsUiState) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.readinessStats.forEach { s ->
            AssistChip(
                onClick = { },
                label = { Text("${s.title}: ${s.status}") }
            )
        }
    }
}