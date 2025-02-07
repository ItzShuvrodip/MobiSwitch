package com.example.mobiswitch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        databaseRef = FirebaseDatabase.getInstance().getReference("esp32/command")

        val switchLED: Switch = findViewById(R.id.switchLED)
        val btnChangeWiFi: Button = findViewById(R.id.btnChangeWiFi)
        switchLED.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                databaseRef.setValue(true)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Command ON sent", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show()
                    }
            } else {
                databaseRef.setValue(false)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Command OFF sent", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to send command", Toast.LENGTH_SHORT).show()
                    }
            }


            btnChangeWiFi.setOnClickListener {
                val intent = Intent(this, wifi::class.java)
                startActivity(intent)
            }
        }
    }
}