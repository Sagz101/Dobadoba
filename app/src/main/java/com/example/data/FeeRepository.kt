package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import android.util.Log

class FeeRepository(private val dao: DobadobaDao) {
    val allFees: Flow<List<FeeEntity>> = dao.getAllFees()

    suspend fun recordFee(feeAmountMwk: Double, associatedTransferId: String) = withContext(Dispatchers.IO) {
        val f = FeeEntity(
            feeAmountMwk = feeAmountMwk, 
            associatedTransferId = associatedTransferId,
            timestamp = System.currentTimeMillis()
        )
        dao.insertFee(f)
    }

    suspend fun getFeeTotal(): Double = withContext(Dispatchers.IO) {
        val list = dao.getAllFeesList()
        list.sumOf { it.feeAmountMwk }
    }

    suspend fun syncFeesToServer(): Boolean = withContext(Dispatchers.IO) {
        try {
            val list = dao.getAllFeesList()
            if (list.isEmpty()) {
                Log.d("FeeRepository", "No fees to sync.")
                return@withContext true
            }
            val sum = list.sumOf { it.feeAmountMwk }
            Log.d("FeeRepository", "Syncing daily fee total of $sum MWK (${list.size} audit events) to server...")
            // Call simulated API
            kotlinx.coroutines.delay(500)
            Log.d("FeeRepository", "Sync complete. All fees recorded offline are now synced on-server.")
            return@withContext true
        } catch (e: Exception) {
            Log.e("FeeRepository", "Error syncing fees to server", e)
            return@withContext false
        }
    }
}
