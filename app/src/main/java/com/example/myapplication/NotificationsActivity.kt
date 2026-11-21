package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.NotifType
import com.example.myapplication.databinding.ActivityNotificationsBinding
import com.example.myapplication.ui.NotificationAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var adapter: NotificationAdapter
    private var selectedType: NotifType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = NotificationAdapter { notif ->
            notif.isUnread = false
            renderNotifications()
            when {
                notif.relatedChatId != null -> {
                    val intent = Intent(this, ChatActivity::class.java).apply {
                        putExtra(ChatActivity.EXTRA_CHAT_ID, notif.relatedChatId)
                    }
                    startActivity(intent)
                }
                notif.relatedItemId != null -> {
                    val intent = Intent(this, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailActivity.EXTRA_ITEM_ID, notif.relatedItemId)
                    }
                    startActivity(intent)
                }
            }
        }
        binding.rvNotifications.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }

        binding.btnMore.setOnClickListener { anchor ->
            val popup = PopupMenu(this, anchor)
            popup.menuInflater.inflate(R.menu.menu_notifications, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_mark_all_read -> {
                        AppRepository.markAllNotificationsRead()
                        renderNotifications()
                        Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_notif_settings -> {
                        startActivity(Intent(this, NotificationsSettingsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        binding.cgNotifFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedType = when (checkedIds.firstOrNull()) {
                R.id.chip_notif_matches -> NotifType.MATCH
                R.id.chip_notif_messages -> NotifType.MESSAGE
                R.id.chip_notif_status -> NotifType.STATUS
                else -> null
            }
            renderNotifications()
        }

        binding.btnChangeCampus.setOnClickListener {
            val campuses = arrayOf(
                getString(R.string.campus_kemanggisan),
                getString(R.string.campus_alam_sutera),
                getString(R.string.campus_bekasi)
            )
            val currentIdx = campuses.indexOf(AppRepository.currentUser.campus).coerceAtLeast(0)
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.post_selected_campus)
                .setSingleChoiceItems(campuses, currentIdx) { dialog, which ->
                    AppRepository.currentUser.campus = campuses[which]
                    AppRepository.saveUserPrefs(this)
                    updateCampusBanner()
                    dialog.dismiss()
                }
                .show()
        }

        updateCampusBanner()
        renderNotifications()
    }

    private fun updateCampusBanner() {
        binding.tvPushCampus.text = "${AppRepository.currentUser.campus} Campus"
    }

    private fun renderNotifications() {
        val filtered = AppRepository.notifications.filter {
            selectedType == null || it.type == selectedType
        }
        adapter.submitList(filtered)
        binding.emptyState.root.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvNotifications.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }
}
