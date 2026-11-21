package com.example.myapplication

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentPostStep2Binding
import com.example.myapplication.databinding.ItemPhotoSlotBinding
import java.io.File
import java.io.FileOutputStream

class PostStep2Fragment : Fragment() {

    private var _binding: FragmentPostStep2Binding? = null
    private val binding get() = _binding!!

    private val postActivity: PostItemActivity?
        get() = activity as? PostItemActivity

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) addPhotoUri(uri.toString())
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                val uri = saveBitmapToCache(bitmap)
                if (uri != null) addPhotoUri(uri.toString())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostStep2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGallery.setOnClickListener { openGalleryIfAllowed() }
        binding.btnCamera.setOnClickListener { openCameraIfAllowed() }

        val slots = listOf(binding.slot1, binding.slot2, binding.slot3)
        slots.forEachIndexed { index, slot ->
            slot.root.setOnClickListener {
                val photos = postActivity?.draft?.photoUris ?: return@setOnClickListener
                if (index >= photos.size) {
                    openGalleryIfAllowed()
                }
            }
            slot.btnRemove.setOnClickListener {
                val photos = postActivity?.draft?.photoUris ?: return@setOnClickListener
                if (index < photos.size) {
                    photos.removeAt(index)
                    renderSlots()
                }
            }
        }

        renderSlots()
    }

    private fun openGalleryIfAllowed() {
        val photos = postActivity?.draft?.photoUris ?: return
        if (photos.size >= 3) {
            Toast.makeText(requireContext(), "Maximum 3 photos attached.", Toast.LENGTH_SHORT).show()
            return
        }
        galleryLauncher.launch("image/*")
    }

    private fun openCameraIfAllowed() {
        val photos = postActivity?.draft?.photoUris ?: return
        if (photos.size >= 3) {
            Toast.makeText(requireContext(), "Maximum 3 photos attached.", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            cameraLauncher.launch(null)
        } catch (_: Exception) {
            galleryLauncher.launch("image/*")
        }
    }

    private fun addPhotoUri(uriString: String) {
        val photos = postActivity?.draft?.photoUris ?: return
        if (photos.size < 3) {
            photos.add(uriString)
            renderSlots()
        }
    }

    private fun renderSlots() {
        val photos = postActivity?.draft?.photoUris ?: emptyList()
        binding.tvPhotoCount.text = getString(R.string.post_photo_count_format, photos.size)

        val slots: List<ItemPhotoSlotBinding> = listOf(binding.slot1, binding.slot2, binding.slot3)
        slots.forEachIndexed { index, slot ->
            slot.tvAddLabel.text = getString(R.string.post_add_photo_format, index + 1)
            if (index < photos.size) {
                slot.groupAdd.visibility = View.GONE
                slot.ivPhoto.visibility = View.VISIBLE
                slot.btnRemove.visibility = View.VISIBLE
                slot.tvMainLabel.visibility = if (index == 0) View.VISIBLE else View.GONE
                try {
                    slot.ivPhoto.setImageURI(Uri.parse(photos[index]))
                } catch (_: Exception) {
                    slot.ivPhoto.setImageResource(R.drawable.ic_image)
                }
            } else {
                slot.groupAdd.visibility = View.VISIBLE
                slot.ivPhoto.visibility = View.GONE
                slot.btnRemove.visibility = View.GONE
                slot.tvMainLabel.visibility = View.GONE
            }
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri? {
        return try {
            val file = File(requireContext().cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
            }
            Uri.fromFile(file)
        } catch (_: Exception) {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
