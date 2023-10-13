package com.waydatasolution.devicewaylib.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class Data(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val value: Double,
    val mac: String,
    val date: Long,
    val type: Int
)

internal fun List<Data>.toDataRequest(): List<DataRequest> {
    return this.map {
        DataRequest(
            date = it.date,
            value = it.value,
            type = it.type
        )
    }
}