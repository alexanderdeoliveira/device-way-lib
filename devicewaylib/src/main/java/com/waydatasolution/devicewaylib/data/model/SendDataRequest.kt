package com.waydatasolution.devicewaylib.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SendDataRequest(
    @SerializedName("medicoes")
    val dataList: List<DataRequest>
): Parcelable