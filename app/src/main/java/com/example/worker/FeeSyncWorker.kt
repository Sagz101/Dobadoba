package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.DobadobaDatabase
import com.example.data.FeeRepository

class FeeSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val db = DobadobaDatabase.getDatabase(applicationContext)
            val repository = FeeRepository(db.dobadobaDao())
            val success = repository.syncFeesToServer()
            if (success) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
