package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.AppRepository
import com.example.myapplication.databinding.ActivityNotificationsSettingsBinding

class NotificationsSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationsSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        val toggles = listOf(
            binding.swPush to "notif_push",
            binding.swEmail to "notif_email",
            binding.swMatches to "notif_matches",
            binding.swMessages to "notif_messages",
            binding.swStatus to "notif_status",
            binding.swAnnouncements to "notif_announcements"
        )

        toggles.forEach { (switchView, key) ->
            switchView.isChecked = AppRepository.getBoolPref(this, key, true)
            switchView.setOnCheckedChangeListener { _, isChecked ->
                AppRepository.setBoolPref(this, key, isChecked)
            }
        }
    }
}
