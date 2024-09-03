package com.example.todogo

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

            FirebaseApp.initializeApp(this)

    }

}

