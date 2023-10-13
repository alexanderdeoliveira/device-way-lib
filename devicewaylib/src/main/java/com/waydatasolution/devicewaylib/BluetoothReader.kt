package com.waydatasolution.devicewaylib

import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.TZONE.Bluetooth.BLEGattService
import com.TZONE.Bluetooth.IConfigCallBack
import com.TZONE.Bluetooth.Temperature.ConfigService
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.Sample
import com.waydatasolution.devicewaylib.data.model.SamplesPeriod
import com.waydatasolution.devicewaylib.data.model.SensorDataParser
import com.waydatasolution.devicewaylib.util.DATA_SYNCRONIZATION_MODE_CHARACTERISTIC
import com.waydatasolution.devicewaylib.util.DEFAULT_PASSWORD
import com.waydatasolution.devicewaylib.util.DEFAULT_TIMEOUT
import com.waydatasolution.devicewaylib.util.FAST_SYNC_MODE
import com.waydatasolution.devicewaylib.util.PASSWORD_CHARACTERISTIC
import com.waydatasolution.devicewaylib.util.ROUTER_ON_OFF
import com.waydatasolution.devicewaylib.util.SAVED_DATA
import com.waydatasolution.devicewaylib.util.SYNCHRONOUS_DATA_SWITCHES_CHARACTERISTIC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.HashMap
import java.util.UUID

internal class BluetoothReader(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter
) : IConfigCallBack {

    private var dataParser: SensorDataParser? = null

    private var configService: ConfigService? = null

    private var onLoginCompleteListener: (success: Boolean) -> Unit = {}
    private var onReadSamplesProgressListener: (sample: List<Sample>?, success: Boolean, isFinished: Boolean) -> Unit =
        { _, _, _ -> }
    private var onRestartBluetoothCompleteListener: (success: Boolean) -> Unit = {}
    private var onReadSamplesCountCompleteListener: (samplesCount: Int) -> Unit = {}
    private var onWriteConfigsCallBack: (success: Boolean) -> Unit = {}

    private var onDisconnectListener: () -> Unit = {}

    fun readBluetoothSamples(
        device: BluetoothDevice,
        readingPeriod: SamplesPeriod,
        onProgress: (sample: List<Sample>?, success: Boolean, isFinished: Boolean) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            onReadSamplesProgressListener = onProgress

            dataParser = SensorDataParser(device)

            validateToken(device) { success ->
                if (success) {
                    configService!!.SetSyncDateTimeMode(FAST_SYNC_MODE, readingPeriod.code)
                } else {
                    onReadSamplesProgressListener(null, false, true)
                }
            }
        }
    }

    /**
     * Escreve o Token para liberar a leitura e escrita nas Characteristics do Bluetooth. Além de
     * já estabelecer uma conexão caso não tenha uma.
     */
    private fun validateToken(
        device: com.TZONE.Bluetooth.Temperature.Model.Device,
        onComplete: (success: Boolean) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            onLoginCompleteListener = onComplete

            if (configService == null || !configService!!.IsConnected) {
                configService = ConfigService(
                    bluetoothAdapter,
                    context,
                    device.MacAddress,
                    DEFAULT_TIMEOUT,
                    this@BluetoothReader
                )
            } else {
                onLoginCompleteListener(true)
            }
        }
    }

    override fun OnConnected() {}

    override fun OnDisConnected() {
        onDisconnectListener.invoke()
    }

    override fun OnServicesed(p0: MutableList<BLEGattService>) {
        configService!!.CheckToken(DEFAULT_PASSWORD)
    }

    override fun OnReadCallBack(uuid: UUID, data: ByteArray) {
        when {
            uuid.toString().equals(UUID.fromString(SAVED_DATA).toString(), true) -> {
                if (data.isNotEmpty()) {
                    val savedData =
                        (data[0].toInt() and 0xFF) or ((data[1].toInt() and 0xFF) shl 8)

                    onReadSamplesCountCompleteListener(savedData)
                }
            }
        }
    }

    override fun OnWriteCallBack(uuid: UUID, success: Boolean) {
        when {
            uuid.toString().equals(UUID.fromString(PASSWORD_CHARACTERISTIC).toString(), true) -> {
                onLoginCompleteListener(success)
            }

            uuid.toString()
                .equals(UUID.fromString(DATA_SYNCRONIZATION_MODE_CHARACTERISTIC).toString(), true)
            -> {
                if (success)
                    configService!!.Sync(true)
                else
                    onReadSamplesProgressListener(null, false, true)
            }

            uuid.toString().equals(UUID.fromString(ROUTER_ON_OFF).toString(), true) -> {
                onWriteConfigsCallBack(success)
            }
        }
    }

    override fun OnReceiveCallBack(uuid: UUID, data: ByteArray) {
        if (uuid.toString()
                .equals(UUID.fromString(SYNCHRONOUS_DATA_SWITCHES_CHARACTERISTIC).toString(), true)
        ) {
            dataParser?.parseData(data) { sampleList, isFinished ->
                onReadSamplesProgressListener(sampleList, true, isFinished)
            }
        }
    }

    override fun OnReadConfigCallBack(p0: Boolean, p1: HashMap<String, ByteArray>?) {}

    override fun OnWriteConfigCallBack(p0: Boolean) {}
}