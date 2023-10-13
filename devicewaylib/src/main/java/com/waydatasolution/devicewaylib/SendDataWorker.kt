package com.waydatasolution.devicewaylib

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SendDataWorker(
    private val context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    private val serviceLocator by lazy { ServiceLocatorImpl.getInstance(context) }

    override suspend fun doWork(): Result {
        return try {
            withContext(Dispatchers.IO) {
                return@withContext if (serviceLocator.sendDataUseCase()) {
                    Result.success()
                } else {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}