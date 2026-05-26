package com.example

import android.app.Application
import com.example.data.DobadobaDatabase
import com.example.data.DobadobaRepository
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Main Application class for Dobadoba.
 * Incorporates a fallback manual DI [AppContainer] to ensure runtime safety in the absence of a compiler plugin.
 */
class DobaApplication : Application() {

    // Fallback Manual DI Container to ensure runtime compatibility and stability
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        try {
            com.example.data.DobadobaSecurity.performPlaintextToEncryptedMigration(this)
        } catch (e: Exception) {
            android.util.Log.e("DobaApplication", "Silent database migration failed", e)
        }
        container = AppContainer(this)

        // Schedule WorkManager tasks for automated/resilient operations
        try {
            val workManager = androidx.work.WorkManager.getInstance(this)
            
            val feeSyncRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.worker.FeeSyncWorker>(
                24, java.util.concurrent.TimeUnit.HOURS
            ).build()
            workManager.enqueueUniquePeriodicWork(
                "FeeSyncWork",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                feeSyncRequest
            )

            val standingOrderRequest = androidx.work.PeriodicWorkRequestBuilder<com.example.worker.StandingOrderWorker>(
                24, java.util.concurrent.TimeUnit.HOURS
            ).build()
            workManager.enqueueUniquePeriodicWork(
                "StandingOrderWork",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                standingOrderRequest
            )
        } catch (e: Exception) {
            android.util.Log.e("DobaApplication", "WorkManager scheduling failed", e)
        }
    }
}

/**
 * Fallback Dependency Injection Container.
 * Manages Singletons for Databases, Retrofit Network APIs, and Repositories.
 */
class AppContainer(private val application: Application) {

    val database: DobadobaDatabase by lazy {
        DobadobaDatabase.getDatabase(application)
    }

    val repository: DobadobaRepository by lazy {
        DobadobaRepository(database.dobadobaDao())
    }

    // Aligned with Retrofit and security pinning requirement
    val okHttpClient: OkHttpClient by lazy {
        val certificatePinner = CertificatePinner.Builder()
            .add("api.dobadoba.mw", "sha256/DobaPrimaryPinGenSha256HashCheckValue=")
            .add("api.dobadoba.mw", "sha256/DobaBackupPinGenSha256HashBackupValue=")
            .build()

        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .eventListener(object : okhttp3.EventListener() {
                override fun callFailed(call: okhttp3.Call, ioe: java.io.IOException) {
                    if (ioe is javax.net.ssl.SSLPeerUnverifiedException || ioe.message?.contains("Certificate pinning") == true) {
                        try {
                            val monitoringClient = OkHttpClient.Builder().build()
                            val mediaType = "application/json".toMediaTypeOrNull()
                            val payload = """{"deviceId":"${android.os.Build.ID}","timestamp":${System.currentTimeMillis()}}"""
                            val body = payload.toRequestBody(mediaType)
                            val request = okhttp3.Request.Builder()
                                .url("https://api.dobadoba.mw/security/pin-failure")
                                .post(body)
                                .build()
                            monitoringClient.newCall(request).enqueue(object : okhttp3.Callback {
                                override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {}
                                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                                    response.close()
                                }
                            })
                        } catch (e: Exception) {
                            // Silent fail safe inside callback
                        }
                    }
                }
            })
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.dobadoba.mw/") // Fallback URL
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}
