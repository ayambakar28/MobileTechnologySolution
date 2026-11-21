package com.example.myapplication

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.AppRepository
import com.example.myapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private val animators = mutableListOf<ObjectAnimator>()

    private val navigateRunnable = Runnable {
        val nextIntent = when {
            !AppRepository.isOnboardingDone(this) -> Intent(this, OnboardingActivity::class.java)
            !AppRepository.isLoggedIn(this) -> Intent(this, LoginActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }
        startActivity(nextIntent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppRepository.loadUserPrefs(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        animateLoadingDots()
        handler.postDelayed(navigateRunnable, 1300L)
    }

    private fun animateLoadingDots() {
        val count = binding.loadingDots.childCount
        for (i in 0 until count) {
            val dot = binding.loadingDots.getChildAt(i)
            val anim = ObjectAnimator.ofFloat(dot, "alpha", 0.35f, 1f).apply {
                duration = 500L
                startDelay = i * 160L
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                start()
            }
            animators.add(anim)
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(navigateRunnable)
        animators.forEach { it.cancel() }
        super.onDestroy()
    }
}
