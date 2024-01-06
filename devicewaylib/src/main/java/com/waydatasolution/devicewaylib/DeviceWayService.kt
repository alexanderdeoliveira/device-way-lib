package com.waydatasolution.devicewaylib

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.TZONE.Bluetooth.BLE
import com.TZONE.Bluetooth.ILocalBluetoothCallBack
import com.TZONE.Bluetooth.Temperature.BroadcastService
import com.waydatasolution.devicewaylib.data.model.BluetoothDevice
import com.waydatasolution.devicewaylib.domain.ReadDataUseCase
import com.waydatasolution.devicewaylib.domain.SaveDataUseCase
import com.waydatasolution.devicewaylib.domain.ScheduleSendDataUseCase
import com.waydatasolution.devicewaylib.domain.ServiceLocatorImpl
import com.waydatasolution.devicewaylib.util.BluetoothUtil
import com.waydatasolution.devicewaylib.util.CHANNEL_ID
import com.waydatasolution.devicewaylib.util.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

class DeviceWayService: Service(), ILocalBluetoothCallBack {

    private var isDebug: Boolean = true
    private var mTimer: Long = 0
    private var sensorIdList: ArrayList<String> = arrayListOf()

    private lateinit var saveDataUseCase: SaveDataUseCase
    private lateinit var readDataUseCase: ReadDataUseCase
    private lateinit var scheduleSendEventsUseCase: ScheduleSendDataUseCase

    private val broadcastService = BroadcastService()
    private var isScanning = false
    private var isInit = false

    private val deviceList: MutableList<Pair<String,BluetoothDevice>> = mutableListOf()

    private var jobSearch: Job? = null
    private var jobResearch: Job? = null
    private var jobRead: Job? = null
    private var jobSave: Job? = null

    private var isServiceFinished = false

    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onBind(intent: Intent): IBinder {
        return DeviceWayBinder()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.apply {
            isDebug = getBooleanExtra(IS_DEBUG_EXTRA, true)
            mTimer = getLongExtra(TIMER_EXTRA, 0)
            sensorIdList = getStringArrayListExtra(SENSOR_ID_LIST_EXTRA) as ArrayList<String>
        }

        saveDataUseCase = ServiceLocatorImpl.getInstance(applicationContext, isDebug).saveDataUseCase
        readDataUseCase = ServiceLocatorImpl.getInstance(applicationContext, isDebug).readDataUseCase
        scheduleSendEventsUseCase = ServiceLocatorImpl.getInstance(applicationContext, isDebug).scheduleSendDataUseCase

        startForeground()
        return START_STICKY
    }

    private fun startForeground() {
        isServiceFinished = false
        createNotification(MainActivityLib::class.java)
        if (deviceList.isEmpty()) {
            scanForDevices()
        } else {
            readAndSaveSamples()
        }
    }

    private fun scanForDevices() {
        if (BluetoothUtil.isBluetoothConnected()) {
            updateNotification(
                MainActivityLib::class.java,
                DeviceWayLib.DeviceWayStatus.SEARCHING
            )

            val bluetoothManager = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter

            jobSearch = CoroutineScope(Dispatchers.Main).launch {
                if (!isScanning) {
                    if (!isInit)
                        isInit = broadcastService.Init(bluetoothAdapter, this@DeviceWayService)

                    if (isInit) {
                        isScanning = true
                        broadcastService.StartScan()
                    }
                }
            }
        } else {
            updateNotification(
                MainActivityLib::class.java,
                DeviceWayLib.DeviceWayStatus.BLUETOOTH_DISABLED
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceFinished = true
        jobSearch?.cancel()
        jobResearch?.cancel()
        jobRead?.cancel()
        jobSave?.cancel()
        if (isInit) {
            broadcastService.StopScan()
        }
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        jobSearch?.cancel()
        jobResearch?.cancel()
        jobRead?.cancel()
        jobSave?.cancel()    }

    override fun OnEntered(ble: BLE) {
        processBLE(ble)
    }

    override fun OnUpdate(ble: BLE) {
        processBLE(ble)
    }

    override fun OnExited(ble: BLE) {
        updateNotification(
            MainActivityLib::class.java,
            DeviceWayLib.DeviceWayStatus.STANDBY
        )
    }

    override fun OnScanComplete() {
        if (deviceList.isEmpty()) {
            updateNotification(
                MainActivityLib::class.java,
                DeviceWayLib.DeviceWayStatus.NOT_FOUND
            )

            restartRead()
        }
    }

    private fun processBLE(ble: BLE) {
        val device = BluetoothDevice()
        device.fromScanData(ble)

        if (!device.SN.isNullOrEmpty() && sensorIdList.contains(device.SN)) {
            deviceList.add(Pair(device.SN, device))
            log("Dispositivo encontrado: ${device.SN}")
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
        updateNotification(
            MainActivityLib::class.java,
            DeviceWayLib.DeviceWayStatus.READING
        )
        deviceList.map {
            readSamplesByDevice(it.second) {
                if (it.second.samples.isNotEmpty()) {
                    saveSamplesByDevice(it.second)
                } else {
                    updateNotification(
                        MainActivityLib::class.java,
                        DeviceWayLib.DeviceWayStatus.NO_DATA
                    )
                }
            }
        }
    }

    private fun readSamplesByDevice(
        device: BluetoothDevice,
        callback: () -> Unit
    ) {
        jobRead = CoroutineScope(Dispatchers.Main).launch {
            readDataUseCase.invoke(
                this@DeviceWayService,
                bluetoothAdapter!!,
                device,
                callback
            )
        }
    }

    private fun saveSamplesByDevice(device: BluetoothDevice) {
        updateNotification(
            MainActivityLib::class.java,
            DeviceWayLib.DeviceWayStatus.SAVING
        )
        jobSave = CoroutineScope(Dispatchers.IO).launch {
            saveDataUseCase.invoke(device) {
                finishSaving()
            }
        }
    }

    private fun finishSaving() {
        updateNotification(
            MainActivityLib::class.java,
            DeviceWayLib.DeviceWayStatus.STANDBY
        )
        scheduleSendEventsUseCase.invoke()
        restartRead()
    }

    private fun restartRead() {
        if (!isServiceFinished) {
            isScanning = false
            isInit = false
            deviceList.clear()
            jobResearch = CoroutineScope(Dispatchers.Main).launch {
                delay(mTimer)
                scanForDevices()
            }
        }
    }

    private fun createNotification(targetActivity: Class<MainActivityLib>) {
        createChannel()
        startForeground(
            NOTIFICATION_ID,
            getNotification(
                targetActivity,
                DeviceWayLib.DeviceWayStatus.STANDBY
            )
        )
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun getNotification(
        targetActivity: Class<MainActivityLib>,
        status: DeviceWayLib.DeviceWayStatus
    ): Notification {
//        val notificationIntent = Intent(this, targetActivity)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            notificationIntent,
//            PendingIntent.FLAG_IMMUTABLE
//        )

//        val notificationLayout = RemoteViews(packageName, R.layout.layout_notification)
//        notificationLayout.setTextViewText(R.id.tv_status, status.statusText)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bluetooth)
//            .setContentIntent(pendingIntent)
//            .setCustomContentView(notificationLayout)
//            .setCustomBigContentView(notificationLayout)
            .setContentText(status.statusText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(
        targetActivity: Class<MainActivityLib>,
        status: DeviceWayLib.DeviceWayStatus
    ) {
        if (!isServiceFinished) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.notify(
                NOTIFICATION_ID,
                getNotification(
                    targetActivity,
                    status
                )
            )

            log(status.statusText)
        }
    }

    private fun log(message: String) {
        Log.i("DeviceWay", message)
        saveLog("$message/n")
    }

    private fun saveLog(message: String) {
        try {
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "status.txt")
            val fileWriter = FileWriter(file)
            fileWriter.write(message)
            fileWriter.close()
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        }catch (e: NumberFormatException){
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    inner class DeviceWayBinder : Binder() {
        fun getService(): DeviceWayService = this@DeviceWayService
    }

    companion object {
        const val IS_DEBUG_EXTRA = "IS_DEBUG_EXTRA"
        const val TIMER_EXTRA = "TIMER_EXTRA"
        const val SENSOR_ID_LIST_EXTRA = "SENSOR_ID_LIST_EXTRA"
    }
}