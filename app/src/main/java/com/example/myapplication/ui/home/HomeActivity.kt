package com.example.myapplication.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.data.DBHelper
import com.example.myapplication.ui.auth.LoginActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = DBHelper(this)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val userId = intent.getIntExtra("User ID", -1)
        val user = db.getUserByID(userId)
        etEmail.setText(user?.email)
        etPassword.setText(user?.password)

        btnUpdate.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val result = db.updateUser(userId, email, password)
            if(result != 0){
                Toast.makeText(this, "Update sukses", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Update Gagal", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            val result =db.delete(userId)
            if(result != 0){
                Toast.makeText(this, "Delete sukses", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{ 
                Toast.makeText(this, "Delete Gagal", Toast.LENGTH_SHORT).show()
            }
        }

    }
}