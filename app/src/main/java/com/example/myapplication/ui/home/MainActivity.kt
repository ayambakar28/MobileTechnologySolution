package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.data.DBHelper
import com.example.myapplication.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DBHelper(this)
        val etEmail = findViewById<EditText>(R.id.etNim)
        val etPassword = findViewById<EditText>(R.id.etPass)
        val etConfirmPassword= findViewById<EditText>(R.id.etPassConfirm)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val result = db.register(email, password)

            if (result != -1L) {
                Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }
}