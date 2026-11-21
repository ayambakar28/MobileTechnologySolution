package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.myapplication.data.AppRepository
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.etNim.doAfterTextChanged { hideError() }
        binding.etPassword.doAfterTextChanged { hideError() }

        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptSignIn()
                true
            } else {
                false
            }
        }

        binding.btnSignIn.setOnClickListener { attemptSignIn() }

        binding.btnSso.setOnClickListener {
            hideError()
            proceedToMain()
        }

        binding.btnForgotPassword.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.login_forgot_password)
                .setMessage("Please reset your student account password through BINUS Maya or BINUS SSO portal.")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    private fun attemptSignIn() {
        val nim = binding.etNim.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString().orEmpty()

        if (nim.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.login_error_empty))
            return
        }

        if (nim.all { it.isDigit() } && nim.length >= 8) {
            AppRepository.currentUser.nim = nim
        }
        proceedToMain()
    }

    private fun proceedToMain() {
        binding.progressLogin.visibility = View.VISIBLE
        binding.btnSignIn.isEnabled = false
        binding.btnSso.isEnabled = false

        handler.postDelayed({
            AppRepository.setLoggedIn(this, true)
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }, 450L)
    }

    private fun showError(msg: String) {
        binding.tvLoginError.text = msg
        binding.tvLoginError.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.tvLoginError.visibility = View.GONE
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
