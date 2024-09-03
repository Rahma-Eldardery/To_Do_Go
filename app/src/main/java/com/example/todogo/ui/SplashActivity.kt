package com.example.todogo.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.todogo.R
import com.example.todogo.ui.authorization.SignInActivity
import com.google.firebase.FirebaseApp

class SplashActivity : AppCompatActivity() {
    lateinit var logoImage: ImageView
    lateinit var titleText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_splash)
        val fromTop = AnimationUtils.loadAnimation(this, R.anim.from_top)
        val fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom)
        logoImage = findViewById(R.id.logo)
        logoImage.startAnimation(fromTop)
        titleText = findViewById(R.id.title)
        titleText.startAnimation(fromBottom)

        Handler(Looper.getMainLooper()).postDelayed({
          val intent = Intent(this , SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}