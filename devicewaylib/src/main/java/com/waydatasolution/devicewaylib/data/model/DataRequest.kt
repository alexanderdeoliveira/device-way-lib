package com.waydatasolution.devicewaylib.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class DataRequest(
    @SerializedName("data")
    val date: Long,
    @SerializedName("valor")
    val value: Double,
    @SerializedName("tipo")
    val type: Int
): Parcelable