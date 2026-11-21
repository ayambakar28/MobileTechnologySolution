package com.example.myapplication

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityImageViewerBinding
import com.example.myapplication.ui.UiHelpers

class ImageViewerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_CAPTION = "extra_caption"
    }

    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val uriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val caption = intent.getStringExtra(EXTRA_CAPTION).orEmpty()

        UiHelpers.loadPhotoOrPlaceholder(binding.ivFull, uriString)

        if (caption.isBlank()) {
            binding.tvCaption.visibility = View.GONE
        } else {
            binding.tvCaption.visibility = View.VISIBLE
            binding.tvCaption.text = caption
        }

        binding.btnClose.setOnClickListener { finish() }
    }
}
