package com.example.wifiscanner

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var statusText: TextView
    private lateinit var resultsText: TextView

    private val permissionRequestCode = 1001

    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            val success = intent?.getBooleanExtra(
                WifiManager.EXTRA_RESULTS_UPDATED,
                false
            ) ?: false

            if (success) {
                showResults()
            } else {
                statusText.text = "Scan gagal atau menggunakan hasil sebelumnya."
                showResults()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        wifiManager =
            applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        statusText = findViewById(R.id.statusText)
        resultsText = findViewById(R.id.resultsText)

        val scanButton = findViewById<Button>(R.id.scanButton)

        scanButton.setOnClickListener {
            startWifiScan()
        }
    }

    override fun onStart() {
        super.onStart()

        ContextCompat.registerReceiver(
            this,
            scanReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(scanReceiver)
    }

    private fun startWifiScan() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionRequestCode
            )

            return
        }

        if (!isLocationEnabled()) {

            statusText.text =
                "Aktifkan Location/GPS terlebih dahulu."

            startActivity(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            )

            return
        }

        statusText.text = "Sedang memindai Wi-Fi..."

        resultsText.text = ""

        @Suppress("DEPRECATION")
        val started = wifiManager.startScan()

        if (!started) {

            statusText.text =
                "Scan tidak dimulai. Silakan coba lagi."
        }
    }

    private fun showResults() {

        @Suppress("DEPRECATION")
        val results: List<ScanResult> =
            wifiManager.scanResults
                .sortedByDescending { it.level }

        statusText.text =
            "${results.size} jaringan Wi-Fi ditemukan."

        if (results.isEmpty()) {

            resultsText.text =
                "Tidak ada jaringan Wi-Fi ditemukan."

            return
        }

        val output = StringBuilder()

        results.forEachIndexed { index, result ->

            val ssid =
                if (result.SSID.isNullOrBlank()) {
                    "<Hidden SSID>"
                } else {
                    result.SSID
                }

            output.append(index + 1)
                .append(". ")
                .append(ssid)
                .append("\n")

            output.append("   BSSID    : ")
                .append(result.BSSID)
                .append("\n")

            output.append("   Signal   : ")
                .append(result.level)
                .append(" dBm\n")

            output.append("   Frequency: ")
                .append(result.frequency)
                .append(" MHz\n")

            output.append("   Security : ")
                .append(securityType(result))
                .append("\n\n")
        }

        resultsText.text = output.toString()
    }

    private fun securityType(result: ScanResult): String {

        val capabilities =
            result.capabilities.uppercase(Locale.US)

        return when {

            "WPA3" in capabilities ->
                "WPA3"

            "WPA2" in capabilities ->
                "WPA2"

            "WPA" in capabilities ->
                "WPA"

            "WEP" in capabilities ->
                "WEP"

            else ->
                "Open / Unknown"
        }
    }

    private fun isLocationEnabled(): Boolean {

        val locationManager =
            getSystemService(Context.LOCATION_SERVICE)
                    as android.location.LocationManager

        return locationManager.isProviderEnabled(
            android.location.LocationManager.GPS_PROVIDER
        ) ||
        locationManager.isProviderEnabled(
            android.location.LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == permissionRequestCode &&
            grantResults.isNotEmpty() &&
            grantResults[0] ==
            PackageManager.PERMISSION_GRANTED
        ) {

            startWifiScan()

        } else {

            statusText.text =
                "Izin lokasi diperlukan untuk scan Wi-Fi."
        }
    }
}
