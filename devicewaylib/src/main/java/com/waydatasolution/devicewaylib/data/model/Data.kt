package com.waydatasolution.devicewaylib.data.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.waydatasolution.devicewaylib.util.toDatetime

@Entity(primaryKeys = ["mac","date","type"])
data class Data(
    @SerializedName("value")
    val value: Double,
    @SerializedName("mac")
    val mac: String,
    @SerializedName("date")
    val date: Long,
    @SerializedName("type")
    val type: String,
)

internal fun List<Data>.toDataRequest(): List<DataRequest> {
    return this.map {
        DataRequest(
            date = it.date.toDatetime(),
            value = it.value,
            type = it.type
        )
    }
}