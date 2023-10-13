package com.waydatasolution.devicewaylib.data.model

import com.waydatasolution.devicewaylib.extensions.unixToDate
import com.waydatasolution.devicewaylib.util.HARDWARE_MODEL_BT04B
import java.util.Calendar
import java.util.Date

internal class SensorDataParser(device: com.TZONE.Bluetooth.Temperature.Model.Device) {
    companion object {
        private const val INITIAL_BLOCK = 1
        private const val SAMPLES_BLOCK = 0
        private const val END_BLOCK = 3
    }

    private val deviceModel = device.HardwareModel

    private var beginningTime = Date()
    private var samplesCount = 0
    private var periodTimeInterval = 0
    private val sampleList = mutableListOf<Sample>()

    fun parseData(
        data: ByteArray,
        onProgress: (sampleList: List<Sample>, isFinished: Boolean) -> Unit
    ) {
        when (data[0].toInt() ushr 5) {
            INITIAL_BLOCK -> {
                val unixDate = ByteArray(4)
                unixDate[0] = data[2]
                unixDate[1] = data[3]
                unixDate[2] = data[4]
                unixDate[3] = data[5]

                beginningTime = unixDate.unixToDate()

                periodTimeInterval = ((data[6].toInt() and 0xFF) shl 24) or
                        ((data[7].toInt() and 0xFF) shl 16) or
                        ((data[8].toInt() and 0xFF) shl 8) or
                        (data[9].toInt() and 0xFF)

                val sampleBlock = (data.size - 9) / 3

                samplesCount = 0

                generateSample(data, 10, sampleBlock)
            }
            SAMPLES_BLOCK -> {
                val sampleBlock = (data.size - 2) / 3

                generateSample(data, 2, sampleBlock)

                onProgress(sampleList, false)
            }
            END_BLOCK -> {
                onProgress(sampleList, true)
            }
        }
    }

    private fun generateSample(data: ByteArray, sampleIndex: Int, sampleBlock: Int) {
        val calendar: Calendar = Calendar.getInstance()

        for (i in 0 until sampleBlock) {

            calendar.time = beginningTime

            calendar.add(Calendar.SECOND, periodTimeInterval * samplesCount)

            samplesCount++

            val humidity = if (deviceModel.equals(HARDWARE_MODEL_BT04B, true))
                ((data[sampleIndex + i * 3].toInt() and 0xFE) ushr 1).toDouble()
            else
                0.0

            val rawTemperature = ((data[sampleIndex + i * 3].toInt() and 0x01) shl 10) or
                    ((data[sampleIndex + 1 + i * 3].toInt() and 0xFF) shl 2) or
                    ((data[sampleIndex + 2 + i * 3].toInt() and 0xC0) ushr 6)

            val temperature = if (rawTemperature > 1250)
                (rawTemperature - 2048) * 0.1
            else
                rawTemperature * 0.1

            val sample = Sample(
                temperature = temperature,
                humidity = humidity,
                timestamp = calendar.time
            )

            sampleList.add(sample)
        }
    }
}