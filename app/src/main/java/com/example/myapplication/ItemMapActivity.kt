package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.ReportItem
import com.example.myapplication.databinding.ActivityItemMapBinding
import com.example.myapplication.ui.UiHelpers
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ItemMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
    }

    private lateinit var binding: ActivityItemMapBinding
    private var googleMap: GoogleMap? = null
    private var item: ReportItem? = null
    private var activeCampus: String = "Kemanggisan"

    private val campusCoords = linkedMapOf(
        "Kemanggisan" to LatLng(-6.2019, 106.7815),
        "Alam Sutera" to LatLng(-6.2236, 106.6494),
        "Bekasi" to LatLng(-6.2216, 107.0007)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val itemId = intent.getStringExtra(EXTRA_ITEM_ID)
        item = AppRepository.getItemById(itemId) ?: AppRepository.items.firstOrNull()
        activeCampus = item?.campus ?: AppRepository.currentUser.campus

        bindBottomCard()
        updateCampusLabels()

        runCatching {
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnCampus.setOnClickListener {
            val campuses = campusCoords.keys.toTypedArray()
            val selectedIdx = campuses.indexOf(activeCampus).coerceAtLeast(0)
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.post_selected_campus)
                .setSingleChoiceItems(campuses, selectedIdx) { dialog, which ->
                    activeCampus = campuses[which]
                    updateCampusLabels()
                    centerOnCampus(activeCampus)
                    dialog.dismiss()
                }
                .show()
        }

        binding.btnCompass.setOnClickListener {
            centerOnCampus(activeCampus)
        }

        binding.fabMyLocation.setOnClickListener {
            centerOnCampus(activeCampus)
            Toast.makeText(this, "Centered on BINUS $activeCampus", Toast.LENGTH_SHORT).show()
        }

        binding.btnGetDirections.setOnClickListener {
            val coords = campusCoords[activeCampus] ?: campusCoords.values.first()
            val label = Uri.encode(item?.location?.ifBlank { "BINUS $activeCampus" } ?: "BINUS $activeCampus")
            val uri = Uri.parse("geo:${coords.latitude},${coords.longitude}?q=${coords.latitude},${coords.longitude}($label)")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "No map app installed on this device.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnChatFinder.setOnClickListener {
            val targetItem = item ?: return@setOnClickListener
            val room = AppRepository.getOrCreateChatForItem(targetItem)
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHAT_ID, room.id)
            }
            startActivity(intent)
        }
    }

    private fun bindBottomCard() {
        val current = item ?: return
        binding.tvPlace.text = current.location.ifBlank { "BINUS ${current.campus} Campus" }
        binding.tvDistance.text = "120 m away (2 min walk)"
        binding.tvPlaceNote.text = current.locationNote.ifBlank { current.description }
        UiHelpers.loadPhotoOrPlaceholder(binding.ivItemPhoto, current.photoUris.firstOrNull())
    }

    private fun updateCampusLabels() {
        binding.btnCampus.text = activeCampus
        binding.tvMapTitle.text = "BINUS $activeCampus Campus Map"
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        centerOnCampus(activeCampus)
    }

    private fun centerOnCampus(campus: String) {
        val latLng = campusCoords[campus] ?: campusCoords.values.first()
        googleMap?.apply {
            clear()
            addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(item?.title ?: "BINUS $campus")
                    .snippet(item?.location ?: campus)
            )
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.5f))
        }
    }
}
