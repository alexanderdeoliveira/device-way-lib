package com.waydatasolution.devicewaylib.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
internal data class DataRequest(
    @SerializedName("data")
    val date: String,
    @SerializedName("valor")
    val value: Double,
    @SerializedName("tipo")
    val type: String
): Parcelable