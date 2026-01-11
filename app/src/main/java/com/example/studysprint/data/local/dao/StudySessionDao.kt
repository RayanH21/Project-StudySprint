package com.example.studysprint.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.studysprint.data.local.projections.CourseFocusTotal
import com.example.studysprint.data.local.projections.ExamFocusTotal
import com.example.studysprint.data.local.projections.ExamReadinessCounts
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
        SELECT examId AS examId,
               SUM(CASE WHEN readiness = 1 THEN 1 ELSE 0 END) AS readyCount,
               SUM(CASE WHEN readiness = 0 THEN 1 ELSE 0 END) AS notReadyCount
        FROM study_sessions
        WHERE examId IS NOT NULL AND readiness IS NOT NULL
        GROUP BY examId
    """)
    fun observeExamReadinessCounts(): Flow<List<ExamReadinessCounts>>
}