package com.example.wificheck

import android.Manifest
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        wifiManager =
            applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        resultText = findViewById(R.id.resultText)

        val scanButton = findViewById<Button>(R.id.scanButton)

        scanButton.setOnClickListener {
            scanWifi()
        }
    }

    private fun scanWifi() {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )

            return
        }

        wifiManager.startScan()

        val results = wifiManager.scanResults

        val output = StringBuilder()

        output.append("Wi-Fi ditemukan: ")
            .append(results.size)
            .append("\n\n")

        for ((index, wifi) in results.withIndex()) {

            output.append(index + 1)
                .append(". ")
                .append(wifi.SSID)
                .append("\n")

            output.append("Signal: ")
                .append(wifi.level)
                .append(" dBm\n")

            output.append("Frequency: ")
                .append(wifi.frequency)
                .append(" MHz\n\n")
        }

        resultText.text = output.toString()
    }
}
