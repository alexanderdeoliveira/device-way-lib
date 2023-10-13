package com.waydatasolution.devicewaylib

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import com.TZONE.Bluetooth.BLE
import com.TZONE.Bluetooth.ILocalBluetoothCallBack
import com.TZONE.Bluetooth.Temperature.BroadcastService
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.domain.ReadDataUseCase
import com.waydatasolution.devicewaylib.domain.SaveDataUseCase
import com.waydatasolution.devicewaylib.domain.ScheduleSendDataUseCase
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceWayService: Service(), ILocalBluetoothCallBack {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var saveDataUseCase: SaveDataUseCase
    private lateinit var readDataUseCase: ReadDataUseCase
    private lateinit var scheduleSendEventsUseCase: ScheduleSendDataUseCase

    private val broadcastService = BroadcastService()
    private var isScanning = false
    private var isInit = false

    private val deviceList: MutableList<Pair<String,BluetoothDevice>> = mutableListOf()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()

        startForeground()
        return START_STICKY
    }

    private fun startForeground() {
//        val pendingIntent: PendingIntent =
//            Intent(this, BluetoothActivity::class.java).let { notificationIntent ->
//                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
//            }
//
//        val notification: Notification = Notification.Builder(this, "0")
//            .setContentTitle("Title")
//            .setContentText("Message")
//            .setSmallIcon(androidx.core.R.drawable.ic_call_answer)
//            .setContentIntent(pendingIntent)
//            .setTicker("Ticker")
//            .build()
//
//        startForeground(1, notification)
        saveDataUseCase = ServiceLocatorImpl.getInstance(this).saveDataUseCase
        readDataUseCase = ServiceLocatorImpl.getInstance(this).readDataUseCase
        scheduleSendEventsUseCase = ServiceLocatorImpl.getInstance(this).scheduleSendDataUseCase

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        scanForDevices()
    }

    private fun scanForDevices() {
        deviceList.clear()
        CoroutineScope(Dispatchers.Main).launch {
            if (!isScanning) {
                if (!isInit)
                    isInit = broadcastService.Init(bluetoothAdapter, this@DeviceWayService)

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

    override fun OnExited(ble: BLE) {
        Toast.makeText(this, "Scan exited", Toast.LENGTH_SHORT).show()
    }

    override fun OnScanComplete() {
        Toast.makeText(this, "Scan completed", Toast.LENGTH_SHORT).show()
        readAnsSaveSamples()
    }

    private fun processBLE(ble: BLE) {
        val device = BluetoothDevice()
        device.fromScanData(ble)

        if (!device.SN.isNullOrEmpty()) {
            deviceList.removeIf {
                it.first == device.SN
            }
            deviceList.add(Pair(device.SN, device))
        }
    }

    private fun readAnsSaveSamples() {
        deviceList.map {
            readSamplesByDevice(it.second) {
                saveSamplesByDevice(it.second)
            }
        }
    }

    private fun readSamplesByDevice(
        device: BluetoothDevice,
        callback: () -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            readDataUseCase.invoke(
                this@DeviceWayService,
                bluetoothAdapter,
                device,
                callback
            )
        }
    }

    private fun saveSamplesByDevice(device: BluetoothDevice) {
        CoroutineScope(Dispatchers.IO).launch {
            saveDataUseCase.invoke(
                device.SN,
                device.samples.subList(0, 100)
            ) {
                scheduleSendEventsUseCase.invoke()
            }
        }
    }
}