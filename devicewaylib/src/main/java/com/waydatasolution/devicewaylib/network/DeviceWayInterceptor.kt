package com.waydatasolution.devicewaylib.network

import android.content.Context
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import com.waydatasolution.devicewaylib.util.AUTH_TOKEN_HEADER
import com.waydatasolution.devicewaylib.util.NetworkUtil
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class DeviceWayInterceptor(
    private val context: Context
): Interceptor {

    private val serviceLocator by lazy { ServiceLocatorImpl.getInstance(context) }

    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            if (!NetworkUtil.internetIsActivated(context)) {
                throw IOException("")
            }
            val request = chain.request()
            val initialConfig = serviceLocator.repository.getInitialConfig()
            val newRequest = request
                .newBuilder()
                .addHeader(AUTH_TOKEN_HEADER, "Bearer ${initialConfig.authToken}")
                .build()
            chain.proceed(newRequest)
        }
    }
}