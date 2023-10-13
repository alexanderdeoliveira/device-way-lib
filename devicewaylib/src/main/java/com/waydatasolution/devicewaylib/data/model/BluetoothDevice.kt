package com.waydatasolution.devicewaylib.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
internal class BluetoothDevice(
//    @SerializedName("MeasuredPower")
//    var measuredPower: Int = 0,
//    @SerializedName("Battery")
//    var battery: Int = 0,
//    @SerializedName("SN")
//    var sN: String = "",
//    @SerializedName("Token")
//    var token: String = "000000",
//    @SerializedName("Interval")
//    var interval: Int = -1000,
//    @SerializedName("TransmitPower")
//    var transmitPower: Int = -1000,
//    @SerializedName("HardwareModel")
//    var hardwareModel: String = "",
//    @SerializedName("Firmware")
//    var firmware: String = "",
//    @SerializedName("Temperature")
//    var temperature: Double = -1000.0,
//    @SerializedName("Humidity")
//    var humidity: Double = -1000.0,
//    @SerializedName("AlarmType")
//    var alarmType: String = "00",
//    @SerializedName("SamplingInterval")
//    var samplingInterval: Double = -1000.0,
//    @SerializedName("SaveInterval")
//    var saveInterval: Int = 0,
//    @SerializedName("SaveInterval2")
//    var saveInterval2: Int = 0,
//    @SerializedName("IsSaveOverwrite")
//    var isSaveOverwrite: Boolean = false,
//    @SerializedName("SavaCount")
//    var savaCount: Int = 0,
//    @SerializedName("LT")
//    var lT: Double = -1000.0,
//    @SerializedName("HT")
//    var hT: Double = -1000.0,
//    @SerializedName("UTCTime")
//    var uTCTime: Date? = null,
//    @SerializedName("TripStatus")
//    var tripStatus: Int = 0,
//    @SerializedName("LDOVoltage")
//    var lDOVoltage: Double = -1000.0,
//    @SerializedName("LDOTemp")
//    var lDOTemp: Double = -1000.0,
//    @SerializedName("LDOPower")
//    var lDOPower: Int = 0,
//    var OtherBytes: List<ByteArray>,
//    @SerializedName("Notes")
//    var notes: String = "",
//    @SerializedName("Description")
//    var description: String = "",
//    var name: String? = "",
//    var rSSI: Int = 0,
//    var scanData: ByteArray? = null,
//    var macAddress: String? = null,
    var samples: MutableList<Sample> = mutableListOf()
): com.TZONE.Bluetooth.Temperature.Model.Device(), Parcelable {

//    fun fromBle(ble: com.TZONE.Bluetooth.BLE): BluetoothDeviceOld? {
//        return BluetoothDeviceOld().apply {
//            OtherBytes = ArrayList<ByteArray?>()
//            OtherBytes.add(ByteArray(20))
//            OtherBytes.add(ByteArray(20))
//            OtherBytes.add(ByteArray(20))
//            OtherBytes.add(ByteArray(20))
//            OtherBytes.add(ByteArray(16))
//
//            name = ble.Name
//            rSSI = ble.RSSI
//            scanData = ble.ScanData
//            macAddress = ble.MacAddress
//            lastScanTime = ble.LastScanTime
//            LastScanTime = Date()
//            val strScanData = StringConvertUtil.bytesToHexString(scanData)
//            val serviceData = BroadcastPacketsUtil.GetScanParam(strScanData, "16")
//            val len = serviceData.substring(4, 6).toInt(16)
//            if (len >= 11) {
//                HardwareModel = serviceData.substring(6, 10).toUpperCase(Locale.getDefault())
//                if (HardwareModel != "3901" && HardwareModel != "3A01" && HardwareModel != "3C01" && HardwareModel != "3A04") {
//                    return null
//                }
//                Firmware = serviceData.substring(10, 12)
//                SN = serviceData.substring(12, 20)
//                Battery = serviceData.substring(20, 22).toInt(16)
//            }
//            val n_0 = 22
//            val l_Temperature = serviceData.substring(n_0, n_0 + 2).toInt(16)
//            Temperature = -1000.0
//            Humidity = -1000.0
//            if (l_Temperature == 4) {
//                val s_Temperature = StringConvertUtil.hexString2binaryString(
//                    serviceData.substring(
//                        n_0 + 2,
//                        n_0 + 4
//                    )
//                )
//                if (s_Temperature.substring(0, 1) == "0") {
//                    var symbol = 1
//                    if (s_Temperature.substring(1, 2) == "1") {
//                        symbol = -1
//                    }
//                    Temperature = (StringConvertUtil.binaryString2hexString(
//                        "00" + s_Temperature.substring(
//                            2,
//                            8
//                        )
//                    ) + serviceData.substring(n_0 + 4, n_0 + 6)).toInt(16).toDouble() / 100.0
//                    Temperature *= symbol.toDouble()
//                }
//                if (HardwareModel == "3901" || HardwareModel == "3C01") {
//                    val s_Humidity = StringConvertUtil.hexString2binaryString(
//                        serviceData.substring(
//                            n_0 + 6,
//                            n_0 + 8
//                        )
//                    )
//                    if (s_Temperature.substring(0, 1) == "0") {
//                        Humidity = (StringConvertUtil.binaryString2hexString(
//                            "00" + s_Humidity.substring(
//                                2,
//                                8
//                            )
//                        ) + serviceData.substring(
//                            n_0 + 8,
//                            n_0 + 10
//                        )).toInt(16).toDouble() / 100.0
//                    }
//                }
//            }
//            val n_3 = n_0 + 2 + l_Temperature * 2 + 4
//            AlarmType = "00"
//            if (serviceData.length >= 38) {
//                AlarmType = serviceData.substring(n_3, n_3 + 2)
//            }
//            if (Temperature != -1000.0) {
//                Temperature =
//                    StringUtil.ToString(Temperature, 1).toDouble()
//            }
//            if (Humidity != -1000.0) {
//                Humidity =
//                    StringUtil.ToString(Humidity, 1).toDouble()
//            }
//        }
//    }
}