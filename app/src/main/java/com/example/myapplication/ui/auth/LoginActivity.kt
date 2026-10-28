package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.data.DBHelper
import com.example.myapplication.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DBHelper(this)
        val etEmail = findViewById<EditText>(R.id.etNim)
        val etPassword = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            val user = db.login(email, password)
            if(user!= null){
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("User ID", user.id)
            }else{
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }
    }
}