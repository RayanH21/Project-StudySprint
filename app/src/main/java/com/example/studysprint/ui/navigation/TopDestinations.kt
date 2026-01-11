package com.example.studysprint.ui.navigation

sealed class TopDest(val route: String) {
    data object Courses : TopDest("courses")
    data object Exams : TopDest("exams")
    data object Timer : TopDest("timer")
    data object Stats : TopDest("stats")
}