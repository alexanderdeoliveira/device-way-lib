package com.waydatasolution.devicewaylib.data.model

import retrofit2.Response

sealed class ResponseStatus {
    data class Success(val statusCode: Int): ResponseStatus()
    data class Failure(val statusCode: Int, val message: String): ResponseStatus()
}

fun <R : Any> Response<R>.parseResponse(): ResponseStatus {
    return if (isSuccessful) {
        ResponseStatus.Success(code())
    } else {
        ResponseStatus.Failure(code(), message())
    }
}