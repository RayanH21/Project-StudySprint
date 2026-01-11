package com.example.studysprint.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.focus_by_course),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(state.courseStats) { s ->
                Card {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = "${s.name}: ${formatSeconds(s.totalFocusSeconds)}"
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.focus_by_exam),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(state.examStats) { s ->
                Card {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = "${s.title}: ${formatSeconds(s.totalFocusSeconds)}"
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.exam_status_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(state.readinessStats) { s ->
                Card {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = "${s.title}: ${s.status}"
                    )
                }
            }
        }
    }
}