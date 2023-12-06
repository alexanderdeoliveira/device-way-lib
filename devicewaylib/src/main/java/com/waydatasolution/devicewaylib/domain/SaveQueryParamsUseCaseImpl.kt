package com.waydatasolution.devicewaylib.domain

import com.waydatasolution.devicewaylib.data.model.QueryParam
import com.waydatasolution.devicewaylib.data.repository.DeviceWayRepository

internal class SaveQueryParamsUseCaseImpl(
    private val repository: DeviceWayRepository
): SaveQueryParamsUseCase {
    override suspend fun invoke(
        queryParams: Map<String, String>
    ) {
        val queryParamList = queryParams.map {
            QueryParam(it.key, it.value)
        }
        repository.saveQueryParams(queryParamList)
    }
}