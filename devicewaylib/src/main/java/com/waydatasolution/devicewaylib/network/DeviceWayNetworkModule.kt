package com.waydatasolution.devicewaylib.network

import android.content.Context
import com.google.gson.Gson
import com.waydatasolution.devicewaylib.BuildConfig
import com.waydatasolution.devicewaylib.util.CONNECT_TIMEOUT
import com.waydatasolution.devicewaylib.util.READ_TIMEOUT
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal class DeviceWayNetworkModule(
    private val context: Context,
    private val isDebug: Boolean
) {
    private lateinit var deviceWayClient: DeviceWayClient

    init {
        createHttpClient()
    }

    private fun createHttpClient() {
        val client = OkHttpClient.Builder()
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(DeviceWayInterceptor(context, isDebug))
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(getHttpLoggingInterceptor())
                }
            }
            .build()

        val baseUrl = if (isDebug) {
            BuildConfig.BASE_URL_HMG
        } else {
            BuildConfig.BASE_URL_PRD
        }
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(client)
            .baseUrl(baseUrl)
            .build()

        deviceWayClient = retrofit.create(DeviceWayClient::class.java)
    }

    private fun getHttpLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun getClient(): DeviceWayClient {
        return deviceWayClient
    }
}