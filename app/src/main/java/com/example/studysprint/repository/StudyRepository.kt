package com.example.studysprint.repository

import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.data.local.entity.ExamEntity
import com.example.studysprint.data.local.projections.CourseFocusTotal
import com.example.studysprint.data.local.projections.ExamFocusTotal
import com.example.studysprint.data.local.projections.ExamReadinessCounts
import com.example.studysprint.data.local.projections.ExamWithCourseName
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    fun observeCourses(): Flow<List<CourseEntity>>
    suspend fun addCourse(name: String)
    suspend fun deleteCourse(courseId: Long)
    suspend fun setCourseCompleted(courseId: Long, completed: Boolean)

    fun observeExamsWithCourseName(): Flow<List<ExamWithCourseName>>
    fun observeExamsForCourse(courseId: Long): Flow<List<ExamEntity>>
    suspend fun addExam(title: String, dateEpochDay: Long, courseId: Long)
    suspend fun deleteExam(examId: Long)

    suspend fun saveSession(
        courseId: Long,
        examId: Long?,
        startedAt: Long,
        focusSeconds: Long,
        breakSeconds: Long,
        note: String?,
        readiness: Int?
    )

    fun observeCourseFocusTotals(): Flow<List<CourseFocusTotal>>
    fun observeExamFocusTotals(): Flow<List<ExamFocusTotal>>
    fun observeExamReadinessCounts(): Flow<List<ExamReadinessCounts>>
}