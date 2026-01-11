package com.example.studysprint.repository

import com.example.studysprint.data.local.AppDatabase
import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.data.local.entity.ExamEntity
import com.example.studysprint.data.local.entity.StudySessionEntity
import com.example.studysprint.data.local.projections.CourseFocusTotal
import com.example.studysprint.data.local.projections.ExamFocusTotal
import com.example.studysprint.data.local.projections.ExamWithCourseName
import kotlinx.coroutines.flow.Flow

class StudyRepositoryImpl(
    private val db: AppDatabase
) : StudyRepository {

    override fun observeCourses(): Flow<List<CourseEntity>> = db.courseDao().observeCourses()

    override suspend fun addCourse(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        db.courseDao().insert(CourseEntity(name = trimmed))
    }

    override suspend fun deleteCourse(courseId: Long) {
        db.courseDao().delete(courseId)
    }

    override suspend fun setCourseCompleted(courseId: Long, completed: Boolean) {
        db.courseDao().setCompleted(courseId, completed)
    }

    override fun observeExamsWithCourseName(): Flow<List<ExamWithCourseName>> =
        db.examDao().observeExamsWithCourseName()

    override fun observeExamsForCourse(courseId: Long): Flow<List<ExamEntity>> =
        db.examDao().observeExamsForCourse(courseId)

    override suspend fun addExam(title: String, dateEpochDay: Long, courseId: Long) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        db.examDao().insert(ExamEntity(title = trimmed, dateEpochDay = dateEpochDay, courseId = courseId))
    }

    override suspend fun deleteExam(examId: Long) {
        db.examDao().delete(examId)
    }

    override suspend fun saveSession(
        courseId: Long,
        examId: Long?,
        startedAt: Long,
        focusSeconds: Long,
        breakSeconds: Long,
        note: String?,
        readiness: Int?
    ) {
        db.sessionDao().insert(
            StudySessionEntity(
                courseId = courseId,
                examId = examId,
                startedAt = startedAt,
                focusSeconds = focusSeconds,
                breakSeconds = breakSeconds,
                note = note?.trim()?.takeIf { it.isNotEmpty() },
                readiness = readiness
            )
        )
    }

    override fun observeCourseFocusTotals(): Flow<List<CourseFocusTotal>> =
        db.sessionDao().observeCourseFocusTotals()

    override fun observeExamFocusTotals(): Flow<List<ExamFocusTotal>> =
        db.sessionDao().observeExamFocusTotals()

    override fun observeExamReadinessStatus() =
        db.sessionDao().observeExamReadinessStatus()
}