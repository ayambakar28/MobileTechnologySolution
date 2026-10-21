package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ui.home.MainActivity

class LoginActivity : AppCompatActivity() {

    private val nimBenar = "123456"
    private val passwordBenar = "123456"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etNim = findViewById<EditText>(R.id.etNim)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val nim = etNim.text.toString().trim()
            val password = etPassword.text.toString()

            if (nim.isEmpty()) { etNim.error = "NIM wajib diisi"; return@setOnClickListener }
            if (password.isEmpty()) { etPassword.error = "Password wajib diisi"; return@setOnClickListener }

            if (nim == nimBenar && password == passwordBenar) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("nim", nim)      
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "NIM atau password salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}