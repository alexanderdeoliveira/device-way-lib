package com.waydatasolution.devicewaylib.domain

internal interface SaveQueryParamsUseCase {
    suspend operator fun invoke(
        queryParams: Map<String, String>
    )
}