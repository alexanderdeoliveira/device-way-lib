package com.waydatasolution.devicewaylib.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.waydatasolution.devicewaylib.util.DATA_TYPE_HUMIDITY
import com.waydatasolution.devicewaylib.util.DATA_TYPE_TEMPERATURE
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
internal data class Sample(
    @SerializedName("position")
    var position: Int = 0,
    @SerializedName("timestamp")
    var timestamp: Date,
    @SerializedName("temperature")
    var temperature: Double,
    @SerializedName("humidity")
    var humidity: Double
): Parcelable

internal fun Sample.toDataModel(mac: String): List<Data> {
    val dataList = mutableListOf<Data>()

    if (humidity != 0.0) {
        val data = Data(
            value = humidity,
            date = timestamp.time,
            mac = mac,
            type = DATA_TYPE_HUMIDITY
        )

        dataList.add(data)
    }

    val data = Data(
        value = temperature,
        date = timestamp.time,
        mac = mac,
        type = DATA_TYPE_TEMPERATURE
    )

    dataList.add(data)

    return dataList
}