package com.example.mobiswitch

import android.app.Application
import com.google.firebase.FirebaseApp

class MobiSwitchApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}