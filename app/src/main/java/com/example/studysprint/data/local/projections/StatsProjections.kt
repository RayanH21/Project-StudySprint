package com.example.studysprint.data.local.projections

import androidx.room.ColumnInfo

data class CourseFocusTotal(
    @ColumnInfo(name = "courseId") val courseId: Long,
    @ColumnInfo(name = "totalFocusSeconds") val totalFocusSeconds: Long
)

data class ExamFocusTotal(
    @ColumnInfo(name = "examId") val examId: Long,
    @ColumnInfo(name = "totalFocusSeconds") val totalFocusSeconds: Long
)

data class ExamReadinessStatus(
    @androidx.room.ColumnInfo(name = "examId") val examId: Long,
    @androidx.room.ColumnInfo(name = "readiness") val readiness: Int
)

data class ExamWithCourseName(
    val id: Long,
    val title: String,
    val dateEpochDay: Long,
    val courseId: Long,
    val courseName: String
)