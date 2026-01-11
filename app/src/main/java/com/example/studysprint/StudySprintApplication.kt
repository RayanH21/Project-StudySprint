package com.example.studysprint

import android.app.Application
import com.example.studysprint.data.local.AppDatabase
import com.example.studysprint.repository.StudyRepository
import com.example.studysprint.repository.StudyRepositoryImpl

class StudySprintApplication : Application() {
    lateinit var repository: StudyRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = StudyRepositoryImpl(db)
    }
}