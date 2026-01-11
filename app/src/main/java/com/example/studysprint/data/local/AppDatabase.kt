package com.example.studysprint.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studysprint.data.local.dao.CourseDao
import com.example.studysprint.data.local.dao.ExamDao
import com.example.studysprint.data.local.dao.StudySessionDao
import com.example.studysprint.data.local.entity.CourseEntity
import com.example.studysprint.data.local.entity.ExamEntity
import com.example.studysprint.data.local.entity.StudySessionEntity

@Database(
    entities = [CourseEntity::class, ExamEntity::class, StudySessionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun examDao(): ExamDao
    abstract fun sessionDao(): StudySessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_sprint.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}