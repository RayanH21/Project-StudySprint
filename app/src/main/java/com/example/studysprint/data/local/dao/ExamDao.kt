package com.example.studysprint.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.studysprint.data.local.entity.ExamEntity
import com.example.studysprint.data.local.projections.ExamWithCourseName
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Query("""
        SELECT e.id, e.title, e.dateEpochDay, e.courseId, c.name AS courseName
        FROM exams e
        JOIN courses c ON c.id = e.courseId
        ORDER BY e.dateEpochDay ASC
    """)
    fun observeExamsWithCourseName(): Flow<List<ExamWithCourseName>>

    @Query("SELECT * FROM exams WHERE courseId = :courseId ORDER BY dateEpochDay ASC")
    fun observeExamsForCourse(courseId: Long): Flow<List<ExamEntity>>

    @Insert
    suspend fun insert(exam: ExamEntity): Long

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun delete(examId: Long)
}