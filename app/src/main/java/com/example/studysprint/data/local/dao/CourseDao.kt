package com.example.studysprint.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.studysprint.data.local.entity.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    fun observeCourses(): Flow<List<CourseEntity>>

    @Insert
    suspend fun insert(course: CourseEntity): Long

    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun delete(courseId: Long)

    @Query("UPDATE courses SET isCompleted = :completed WHERE id = :courseId")
    suspend fun setCompleted(courseId: Long, completed: Boolean)
}