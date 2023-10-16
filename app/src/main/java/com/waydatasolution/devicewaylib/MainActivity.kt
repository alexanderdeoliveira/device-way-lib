package com.waydatasolution.devicewaylib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.waydatasolution.devicewaylib.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnStartScan.setOnClickListener {
            DeviceWayLib.init(
                this@MainActivity,
                "",
                0,
                0,
                0,
                0
            )
        }
    }
}