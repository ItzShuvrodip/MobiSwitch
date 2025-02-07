package com.example.mobiswitch

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val switchLED: Switch = findViewById(R.id.switchLED)
        val btnChangeWiFi: Button = findViewById(R.id.btnChangeWiFi)

        switchLED.setOnCheckedChangeListener { _, isChecked ->
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val currentSSID = wifiManager.connectionInfo.ssid

            if (currentSSID != "\"ESP32_Control\"") {
                Toast.makeText(this, "Please connect to the ESP32 Wi-Fi", Toast.LENGTH_SHORT).show()
            } else {
                val url = if (isChecked) {
                    "http://192.168.4.1/on"
                } else {
                    "http://192.168.4.1/off"
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val client = OkHttpClient()

                        val request = Request.Builder()
                            .url(url)
                            .build()
                        val response: Response = client.newCall(request).execute()

                        if (response.isSuccessful) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, "Command sent successfully", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, "Failed to send command", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        btnChangeWiFi.setOnClickListener {
            val intent = Intent(this, wifi::class.java)
            startActivity(intent)
        }
    }
}