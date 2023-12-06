package com.waydatasolution.devicewaylib

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.TZONE.Bluetooth.BLE
import com.TZONE.Bluetooth.ILocalBluetoothCallBack
import com.TZONE.Bluetooth.Temperature.BroadcastService
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.data.model.ResponseStatus
import com.waydatasolution.devicewaylib.domain.ReadDataUseCase
import com.waydatasolution.devicewaylib.domain.SaveDataUseCase
import com.waydatasolution.devicewaylib.domain.SendDataUseCase
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import com.waydatasolution.devicewaylib.util.BluetoothUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceWayLive(
    private val context: Context,
    private val isDebug: Boolean,
    private val sensorIdList: List<String>,
    private val callbackStatus: (String) -> Unit
): ILocalBluetoothCallBack {

    private lateinit var bluetoothAdapter: BluetoothAdapter

    private var saveDataUseCase: SaveDataUseCase = ServiceLocatorImpl.getInstance(context, isDebug).saveDataUseCase
    private var readDataUseCase: ReadDataUseCase = ServiceLocatorImpl.getInstance(context, isDebug).readDataUseCase
    private var sendDataUseCase: SendDataUseCase = ServiceLocatorImpl.getInstance(context, isDebug).sendDataUseCase

    private val broadcastService = BroadcastService()
    private var isScanning = false
    private var isInit = false

    private val deviceList: MutableList<Pair<String,BluetoothDevice>> = mutableListOf()

    fun start() {
        if (BluetoothUtil.isBluetoothConnected()) {
            scanForDevices()
        } else {
            updateStatus(DeviceWayLib.DeviceWayStatus.BLUETOOTH_DISABLED.statusText)
        }
    }

    private fun scanForDevices() {
        updateStatus(DeviceWayLib.DeviceWayStatus.SEARCHING.statusText)
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        CoroutineScope(Dispatchers.Main).launch {
            if (!isScanning) {
                if (!isInit)
                    isInit = broadcastService.Init(bluetoothAdapter, this@DeviceWayLive)

                if (isInit) {
                    isScanning = true
                    broadcastService.StartScan()
                }
            }
        }
    }

    override fun OnEntered(ble: BLE) {
        processBLE(ble)
    }

    override fun OnUpdate(ble: BLE) {
        processBLE(ble)
    }

    override fun OnExited(ble: BLE) {}

    override fun OnScanComplete() {
        if (deviceList.isEmpty()) {
            updateStatus(DeviceWayLib.DeviceWayStatus.NOT_FOUND.statusText)
        }
    }

    private fun processBLE(ble: BLE) {
        val device = BluetoothDevice()
        device.fromScanData(ble)

        if (!device.SN.isNullOrEmpty()) {
            deviceList.add(Pair(device.SN, device))
            if (sensorIdList.size == deviceList.size) {
                broadcastService.StopScan()
                onComplete()
            }
        }
    }

    private fun onComplete() {
        readAndSaveSamples()
    }

    private fun readAndSaveSamples() {
        updateStatus(DeviceWayLib.DeviceWayStatus.READING.statusText)
        deviceList.map {
            readSamplesByDevice(it.second) {
                if (it.second.samples.isNotEmpty()) {
                    saveSamplesByDevice(it.second)
                }
                else {
                    updateStatus(DeviceWayLib.DeviceWayStatus.NO_DATA.statusText)
                }
            }
        }
    }

    private fun readSamplesByDevice(
        device: BluetoothDevice,
        callback: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            readDataUseCase.invoke(
                context,
                bluetoothAdapter,
                device,
                callback
            )
        }
    }

    private fun saveSamplesByDevice(device: BluetoothDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            updateStatus(DeviceWayLib.DeviceWayStatus.SAVING.statusText)
            saveDataUseCase.invoke(device) {
                finishSaving()
            }
        }
    }

    private fun finishSaving() {
        isInit = false
        isScanning = false
        CoroutineScope(Dispatchers.IO).launch {
            updateStatus(DeviceWayLib.DeviceWayStatus.SENDING.statusText)
            when (val response = sendDataUseCase.invoke()) {
                is ResponseStatus.Success -> updateStatus(DeviceWayLib.DeviceWayStatus.SEND_DATA_SUCCESS.statusText)
                is ResponseStatus.Failure -> updateStatus("${DeviceWayLib.DeviceWayStatus.SEND_DATA_FAILED} - Code: ${response.statusCode}, Message: ${response.message}" )
            }
        }
    }

    private fun updateStatus(statusText: String) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            callbackStatus.invoke(statusText)
        }
    }
}