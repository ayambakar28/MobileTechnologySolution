package com.example.myapplication.ui.home

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nim = intent.getStringExtra("nim")
        findViewById<TextView>(R.id.tvWelcome).text = "Selamat datang, $nim"
    }
}