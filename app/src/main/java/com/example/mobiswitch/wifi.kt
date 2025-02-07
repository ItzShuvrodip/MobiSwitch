package com.example.mobiswitch

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class wifi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wifi)
        val etWifiSSID = findViewById<EditText>(R.id.etWifiSSID)
        val etWifiPassword = findViewById<EditText>(R.id.etWifiPassword)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val database = FirebaseDatabase.getInstance()
        val wifiRef = database.getReference("esp32/wifi")

        btnSubmit.setOnClickListener {
            val ssid = etWifiSSID.text.toString().trim()
            val password = etWifiPassword.text.toString().trim()

            if (ssid.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both SSID and Password", Toast.LENGTH_SHORT).show()
            } else {
                val wifiData = HashMap<String, String>()
                wifiData["ssid"] = ssid
                wifiData["password"] = password
                wifiRef.setValue(wifiData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "WiFi Credentials Sent!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}