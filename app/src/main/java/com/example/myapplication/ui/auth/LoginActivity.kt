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

    private lateinit var b: ActivityLoginBinding
    private val auth = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.isLoggedIn()) { goHome(); return }
        b = ActivityLoginBinding.inflate(layoutInflater); setContentView(b.root)
        b.btnLogin.setOnClickListener { submit(register = false) }
        b.btnRegister.setOnClickListener { submit(register = true) }
    }

    private fun submit(register: Boolean) {
        val nim = b.etNim.text.toString().trim(); val pass = b.etPass.text.toString()
        if (nim.isEmpty() || pass.length < 6) {
            Toast.makeText(this, "NIM wajib diisi, password min. 6 karakter", Toast.LENGTH_SHORT).show(); return
        }
        b.progress.visibility = View.VISIBLE
        val done: (String?) -> Unit = { err ->
            b.progress.visibility = View.GONE
            if (err == null) goHome() else Toast.makeText(this, err, Toast.LENGTH_LONG).show()
        }
        if (register) auth.register(nim, pass, done) else auth.login(nim, pass, done)
    }

    private fun goHome() { startActivity(Intent(this, MainActivity::class.java)); finish() }
}