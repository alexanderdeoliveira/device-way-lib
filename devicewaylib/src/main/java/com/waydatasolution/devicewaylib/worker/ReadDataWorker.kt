package com.waydatasolution.devicewaylib.worker

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.TZONE.Bluetooth.BLE
import com.TZONE.Bluetooth.ILocalBluetoothCallBack
import com.TZONE.Bluetooth.Temperature.BroadcastService
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.domain.ReadDataUseCase
import com.waydatasolution.devicewaylib.domain.SaveDataUseCase
import com.waydatasolution.devicewaylib.domain.ScheduleSendDataUseCase
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import com.waydatasolution.devicewaylib.util.BluetoothUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadDataWorker(
    private val context: Context,
    workerParams: WorkerParameters
): CoroutineWorker(context, workerParams), ILocalBluetoothCallBack {

    private val serviceLocator by lazy { ServiceLocatorImpl.getInstance(context, inputData.getBoolean(IS_TEST_KEY, true)) }

    private var isDebug: Boolean = true
    private var mTimer: Long = 60
    private var sensorIdList: ArrayList<String> = arrayListOf("11407170")

    private lateinit var saveDataUseCase: SaveDataUseCase
    private lateinit var readDataUseCase: ReadDataUseCase
    private lateinit var scheduleSendEventsUseCase: ScheduleSendDataUseCase

    private val broadcastService = BroadcastService()
    private var isScanning = false
    private var isInit = false

    private val deviceList: MutableList<Pair<String, BluetoothDevice>> = mutableListOf()

    private var bluetoothAdapter: BluetoothAdapter? = null

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Main.immediate) {
            init()
            Result.success()
        }
    }

    private fun init() {
        saveDataUseCase = serviceLocator.saveDataUseCase
        readDataUseCase = serviceLocator.readDataUseCase
        scheduleSendEventsUseCase = serviceLocator.scheduleSendDataUseCase

        scanForDevices()
    }

    private fun scanForDevices() {
        if (BluetoothUtil.isBluetoothConnected()) {
            if (bluetoothAdapter == null) {
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                bluetoothAdapter = bluetoothManager.adapter
            }

            CoroutineScope(Dispatchers.Main).launch {
                if (!isScanning) {
                    if (!isInit)
                        isInit = broadcastService.Init(bluetoothAdapter, this@ReadDataWorker)

                    if (isInit) {
                        isScanning = true
                        broadcastService.StartScan()
                    }
                }
            }
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
        deviceList.map {
            readSamplesByDevice(it.second) {
                if (it.second.samples.isNotEmpty()) {
                    saveSamplesByDevice(it.second)
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
                bluetoothAdapter!!,
                device,
                callback
            )
        }
    }

    private fun saveSamplesByDevice(device: BluetoothDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            saveDataUseCase.invoke(device) {
                finishSaving()
            }
        }
    }

    private fun finishSaving() {
        scheduleSendEventsUseCase.invoke()
    }

    override fun OnEntered(ble: BLE) {
        processBLE(ble)
    }

    override fun OnUpdate(ble: BLE) {
        processBLE(ble)
    }

    override fun OnExited(p0: BLE?) {}

    override fun OnScanComplete() {}

    companion object {
        const val IS_TEST_KEY = "IS_TEST_KEY"
    }
}