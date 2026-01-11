package com.example.studysprint.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.studysprint.data.local.projections.CourseFocusTotal
import com.example.studysprint.data.local.projections.ExamFocusTotal
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    @Insert
    suspend fun insert(session: com.example.studysprint.data.local.entity.StudySessionEntity): Long

    @Query("""
        SELECT courseId AS courseId, COALESCE(SUM(focusSeconds), 0) AS totalFocusSeconds
        FROM study_sessions
        GROUP BY courseId
    """)
    fun observeCourseFocusTotals(): Flow<List<CourseFocusTotal>>

    @Query("""
        SELECT examId AS examId, COALESCE(SUM(focusSeconds), 0) AS totalFocusSeconds
        FROM study_sessions
        WHERE examId IS NOT NULL
        GROUP BY examId
    """)
    fun observeExamFocusTotals(): Flow<List<ExamFocusTotal>>

    @Query("""
    SELECT s.examId AS examId, s.readiness AS readiness
    FROM study_sessions s
    WHERE s.examId IS NOT NULL
      AND s.readiness IS NOT NULL
      AND s.id = (
        SELECT MAX(id)
        FROM study_sessions
        WHERE examId = s.examId AND readiness IS NOT NULL
      )
""")
    fun observeExamReadinessStatus(): Flow<List<com.example.studysprint.data.local.projections.ExamReadinessStatus>>
}