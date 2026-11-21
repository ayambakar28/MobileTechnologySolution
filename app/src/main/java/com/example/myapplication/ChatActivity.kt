package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.ChatMessage
import com.example.myapplication.data.ChatRoom
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.databinding.ActivityChatBinding
import com.example.myapplication.databinding.BottomSheetAttachBinding
import com.example.myapplication.ui.ChatMessageAdapter
import com.example.myapplication.ui.UiHelpers
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream

class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CHAT_ID = "extra_chat_id"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var room: ChatRoom
    private lateinit var adapter: ChatMessageAdapter
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var pendingPhotoUri: String? = null
    private var pendingPhotoName: String = "photo.jpg"

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                showPhotoPreview(uri.toString(), uri.lastPathSegment?.substringAfterLast('/') ?: "gallery_photo.jpg")
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                val uri = saveBitmapToCache(bitmap)
                if (uri != null) {
                    showPhotoPreview(uri.toString(), "IMG_${System.currentTimeMillis()}.jpg")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val chatId = intent.getStringExtra(EXTRA_CHAT_ID)
        room = AppRepository.getChatById(chatId) ?: AppRepository.chatRooms.firstOrNull() ?: run {
            finish()
            return
        }
        room.unreadCount = 0

        adapter = ChatMessageAdapter { imageMsg ->
            val intent = Intent(this, ImageViewerActivity::class.java).apply {
                putExtra(ImageViewerActivity.EXTRA_IMAGE_URI, imageMsg.imageUri)
                putExtra(ImageViewerActivity.EXTRA_CAPTION, "${imageMsg.caption} · ${imageMsg.time}")
            }
            startActivity(intent)
        }
        binding.rvMessages.adapter = adapter

        bindHeaderAndContext()
        setupListeners()
        renderMessages()
    }

    private fun bindHeaderAndContext() {
        binding.tvPeerName.text = room.peerName
        binding.tvPeerStatus.setText(R.string.chat_status_active)

        binding.tvItemTitle.text = room.itemTitle
        binding.tvItemLocation.text = room.itemLocation
        UiHelpers.bindStatusBadge(binding.tvItemStatus, room.itemStatus)
        val relatedItem = AppRepository.getItemById(room.itemId)
        UiHelpers.loadPhotoOrPlaceholder(binding.ivItemPhoto, relatedItem?.photoUris?.firstOrNull())
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        val openItemDetail = View.OnClickListener {
            val intent = Intent(this, ItemDetailActivity::class.java).apply {
                putExtra(ItemDetailActivity.EXTRA_ITEM_ID, room.itemId)
            }
            startActivity(intent)
        }
        binding.cardItemContext.setOnClickListener(openItemDetail)
        binding.btnViewItem.setOnClickListener(openItemDetail)

        binding.btnMore.setOnClickListener { anchor ->
            val popup = PopupMenu(this, anchor)
            popup.menuInflater.inflate(R.menu.menu_chat, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_view_item -> {
                        openItemDetail.onClick(anchor)
                        true
                    }
                    R.id.action_mark_returned -> {
                        AppRepository.updateItemStatus(room.itemId, ItemStatus.RETURNED)
                        UiHelpers.bindStatusBadge(binding.tvItemStatus, room.itemStatus)
                        renderMessages()
                        true
                    }
                    R.id.action_report_user -> {
                        Toast.makeText(this, "User reported to moderators.", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.action_block_user -> {
                        room.isArchived = true
                        Toast.makeText(this, "Conversation archived and user blocked.", Toast.LENGTH_SHORT).show()
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        binding.chipQuick1.setOnClickListener { sendTextMessage(binding.chipQuick1.text.toString()) }
        binding.chipQuick2.setOnClickListener { sendTextMessage(binding.chipQuick2.text.toString()) }
        binding.chipQuick3.setOnClickListener { sendTextMessage(binding.chipQuick3.text.toString()) }

        binding.btnAttach.setOnClickListener { showAttachSheet() }
        binding.btnRemovePreview.setOnClickListener { clearPhotoPreview() }

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text?.toString()?.trim().orEmpty()
            val photoUri = pendingPhotoUri
            if (AppRepository.sendChatMessage(room, text, photoUri, pendingPhotoName)) {
                if (photoUri != null) clearPhotoPreview()
                binding.etMessage.setText("")
                renderMessages()
                scheduleSimulatedReply(text.ifBlank { "photo" })
            }
        }
    }

    private fun sendTextMessage(text: String) {
        if (AppRepository.sendChatMessage(room, text)) {
            renderMessages()
            scheduleSimulatedReply(text)
        }
    }

    private fun scheduleSimulatedReply(userText: String) {
        handler.removeCallbacksAndMessages(null)
        val firstName = room.peerName.substringBefore(" ")
        binding.tvTyping.text = getString(R.string.chat_typing_format, firstName)
        binding.tvTyping.visibility = View.VISIBLE
        handler.postDelayed({
            binding.tvTyping.visibility = View.GONE
            AppRepository.generateSimulatedReply(room, userText)
            renderMessages()
        }, 1100L)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun renderMessages() {
        adapter.submitList(room.messages)
        if (room.messages.isNotEmpty()) {
            binding.rvMessages.scrollToPosition(room.messages.lastIndex)
        }
    }

    private fun showAttachSheet() {
        val dialog = BottomSheetDialog(this)
        val b = BottomSheetAttachBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(b.root)

        b.cardAttachCamera.setOnClickListener {
            dialog.dismiss()
            try {
                cameraLauncher.launch(null)
            } catch (_: Exception) {
                galleryLauncher.launch("image/*")
            }
        }
        b.cardAttachGallery.setOnClickListener {
            dialog.dismiss()
            galleryLauncher.launch("image/*")
        }

        dialog.show()
    }

    private fun showPhotoPreview(uriString: String, filename: String) {
        pendingPhotoUri = uriString
        pendingPhotoName = filename
        binding.tvPreviewName.text = filename
        UiHelpers.loadPhotoOrPlaceholder(binding.ivPreview, uriString)
        binding.groupPhotoPreview.visibility = View.VISIBLE
    }

    private fun clearPhotoPreview() {
        pendingPhotoUri = null
        binding.groupPhotoPreview.visibility = View.GONE
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri? {
        return try {
            val file = File(cacheDir, "chat_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
            }
            Uri.fromFile(file)
        } catch (_: Exception) {
            null
        }
    }
}
