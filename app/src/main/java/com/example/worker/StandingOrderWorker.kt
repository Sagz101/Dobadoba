package com.example.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.DobadobaDatabase
import com.example.data.FeeEntity
import com.example.util.FeeCalculator

class StandingOrderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        try {
            val db = DobadobaDatabase.getDatabase(applicationContext)
            val dao = db.dobadobaDao()
            val orders = dao.getAllStandingOrdersList()

            for (order in orders) {
                if (order.isPaused) continue

                val prefs = applicationContext.getSharedPreferences("dobapay_prefs", Context.MODE_PRIVATE)
                val currentBalance = prefs.getFloat("wallet_balance", 120000f).toDouble()

                val fee = FeeCalculator.calculateFee(order.amountMwk)
                val totalDeduction = order.amountMwk + fee

                if (currentBalance < totalDeduction) {
                    showFailNotification(order.recipientPhone, order.amountMwk)
                    Log.d("StandingOrderWorker", "Insufficient wallet balance for standing order to ${order.recipientPhone}")
                } else {
                    // Deduct balance and insert standard 1% fee
                    prefs.edit().putFloat("wallet_balance", (currentBalance - totalDeduction).toFloat()).apply()
                    dao.insertFee(
                        FeeEntity(
                            feeAmountMwk = fee, 
                            associatedTransferId = "SO-${order.id}-${System.currentTimeMillis()}"
                        )
                    )
                    Log.d("StandingOrderWorker", "Executed standing order to ${order.recipientPhone}: ${order.amountMwk} + fee $fee")
                }
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("StandingOrderWorker", "Standing order task failed", e)
            return Result.failure()
        }
    }

    private fun showFailNotification(recipient: String, amount: Double) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "standing_order_chan"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(channelId, "Standing Orders", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(chan)
        }
        val formattedAmount = FeeCalculator.formatMwk(amount)
        val notif = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("Scheduled Payment Failed")
            .setContentText("Your scheduled payment of $formattedAmount to $recipient failed — please top up your DobaPay wallet.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        manager.notify(System.currentTimeMillis().toInt(), notif)
    }
}
