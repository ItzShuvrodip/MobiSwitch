package com.example.mobiswitch

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class wifi : AppCompatActivity() {
    private lateinit var wifiManager: WifiManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WiFiAdapter
    private lateinit var scanResults: MutableList<ScanResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wifi)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        recyclerView = findViewById(R.id.recyclerView)
        findViewById<Button>(R.id.btnScan).setOnClickListener { scanWiFi() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        scanResults = mutableListOf()
        adapter = WiFiAdapter(scanResults) { connectToWiFi(it) }
        recyclerView.adapter = adapter

        registerReceiver(wifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun scanWiFi() {
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Turn on location services to scan WiFi!", Toast.LENGTH_LONG).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wifiManager.startScan()
            Toast.makeText(this, "Scanning WiFi...", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scanWiFi()
        } else {
            Toast.makeText(this, "Permission Denied! Cannot scan WiFi.", Toast.LENGTH_SHORT).show()
        }
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val uniqueNetworks = mutableSetOf<String>()
            val filteredResults = mutableListOf<ScanResult>()

            for (scanResult in wifiManager.scanResults) {
                if (!uniqueNetworks.contains(scanResult.SSID) && scanResult.SSID.isNotEmpty()) {
                    uniqueNetworks.add(scanResult.SSID)
                    filteredResults.add(scanResult)
                }
            }

            scanResults.clear()
            scanResults.addAll(filteredResults)
            adapter.notifyDataSetChanged()
        }
    }

    private fun connectToWiFi(scanResult: ScanResult) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Connect to ${scanResult.SSID}")

        val input = EditText(this)
        input.hint = "Enter WiFi Password"
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Connect") { _, _ ->
            val password = input.text.toString()
                connectWithPassword(scanResult, password)
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun connectWithPassword(scanResult: ScanResult, password: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val suggestion = WifiNetworkSuggestion.Builder()
                .setSsid(scanResult.SSID)
                .setWpa2Passphrase(password)
                .build()

            val suggestionsList = listOf(suggestion)
            val status = wifiManager.addNetworkSuggestions(suggestionsList)

            if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                Toast.makeText(this, "Network suggested! Open WiFi settings to connect.", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to suggest network", Toast.LENGTH_SHORT).show()
            }
        } else {
            val wifiConfig = WifiConfiguration().apply {
                SSID = "\"${scanResult.SSID}\""
                preSharedKey = "\"$password\""
            }

            val netId = wifiManager.addNetwork(wifiConfig)
            if (netId != -1) {
                wifiManager.disconnect()
                wifiManager.enableNetwork(netId, true)
                wifiManager.reconnect()
                Toast.makeText(this, "Connecting to ${scanResult.SSID}...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to connect to ${scanResult.SSID}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiScanReceiver)
    }
}
