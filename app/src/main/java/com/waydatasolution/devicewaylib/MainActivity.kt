package com.waydatasolution.devicewaylib

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waydatasolution.devicewaylib.databinding.ActivityMainBinding
import com.waydatasolution.devicewaylib.util.DATA_TYPE_HUMIDITY
import com.waydatasolution.devicewaylib.util.DATA_TYPE_TEMPERATURE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()

        DeviceWayLib.init(
            applicationContext,
            false,
            queryParams = mapOf(
                Pair("authToken", "eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTUxMiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3VzZXJkYXRhIjoiSUJJWDRybUFEMlcxUFBqWG1YRGNJNG8wc1NNdkpwaVNsb3BiejVsUEJ4dGRUSjlMUU0rcHRyRmorZTFWREVPRzFOc0RtSjFpeTdFQWNEYjJndTlVN1RmZElPdU9ya3FGMGhTcUZWNGRBTVpra3hrZ0h1VFlzU0VnUm5KTE1PdnZuVWwrcDlhaUx3bkJzRFVVeDRJVXpoU1h3M3JWVEFoRmdUY0szTktiSkRVPSQwMDBFRzlXMlF0T0dTbVpSSDVKRGtBPT0iLCJuYmYiOjE2OTgzNTA5MjgsImV4cCI6MTcyOTk3NDIyOCwiaXNzIjoiQXV0aEFQSSIsImF1ZCI6IkVudHJlZ2FXYXkifQ.OVMOVb9gLzw7qxOE4vYl1-N1nfoKFJxUnUVUfn4IZQmOtJDyd2A9EF_Ei_5KvQmm3y7xvBWeGoXxGqy467-Oug"),
                Pair("estabelecimento", "244"),
                Pair("codigoRota", "208504"),
                Pair("codigoMotorista", "123456798"),
                Pair("codigoSensor", "3162"),
                Pair("ignoreInstanceCount", "true")
            )
        )
    }

    private fun getCurrentData(
        context: Context
    ) {
        DeviceWayLib.getCurrentData(
            context,
            "11407170",
            10000L,
        ) { dataList ->
            CoroutineScope(Dispatchers.Main).launch {
                if (!dataList.isNullOrEmpty()) {
                    dataList.map { data ->
                        when (data.type) {
                            DATA_TYPE_TEMPERATURE -> binding.tvTemperatureValue.text = data.value.toString()
                            DATA_TYPE_HUMIDITY -> binding.tvHumidityValue.text = data.value.toString()
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnStartScan.setOnClickListener {
            DeviceWayLib.start(
                this@MainActivity,
                arrayListOf(
                    "11407170"
                ),
                10000L
            )

            getCurrentData(this@MainActivity)
        }

        binding.btnStartLiveScan.setOnClickListener {
            DeviceWayLib.startLiveScan(
                this@MainActivity,
                arrayListOf(
                    "11407170"
                )
            ) {
                binding.tvStatusValue.text = it
            }
        }

        binding.btnFinish.setOnClickListener {
            DeviceWayLib.finish(this@MainActivity)
        }

        binding.btnGetNotSendData.setOnClickListener {
            DeviceWayLib.getNotSendDataCount(
                this@MainActivity,
            ) { size ->
                CoroutineScope(Dispatchers.Main).launch {
                    binding.tvNotSendDataValue.text = size.toString()
                }
            }

            DeviceWayLib.getNotSendData(
                this@MainActivity,
            ) { json ->

            }
        }
    }
}