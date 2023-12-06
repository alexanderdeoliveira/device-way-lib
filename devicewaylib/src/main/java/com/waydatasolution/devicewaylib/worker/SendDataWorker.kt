package com.waydatasolution.devicewaylib.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.waydatasolution.devicewaylib.data.model.ResponseStatus
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class SendDataWorker(
    private val context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    private val serviceLocator by lazy { ServiceLocatorImpl.getInstance(context, inputData.getBoolean(IS_TEST_KEY, true)) }

    override suspend fun doWork(): Result {
        return try {
            inputData
            withContext(Dispatchers.IO) {
                val response = serviceLocator.sendDataUseCase()
                return@withContext if (response is ResponseStatus.Success) {
                    Result.success()
                } else {
                    Result.failure()
                }
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val IS_TEST_KEY = "IS_TEST_KEY"
    }
}