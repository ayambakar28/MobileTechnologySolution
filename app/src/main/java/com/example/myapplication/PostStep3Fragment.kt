package com.example.myapplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.databinding.FragmentPostStep3Binding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PostStep3Fragment : Fragment() {

    private var _binding: FragmentPostStep3Binding? = null
    private val binding get() = _binding!!

    private val postActivity: PostItemActivity?
        get() = activity as? PostItemActivity

    private val calendar: Calendar = Calendar.getInstance()
    private var googleMap: GoogleMap? = null
    private var mapCreated = false

    private val campusCoords = mapOf(
        "Kemanggisan" to LatLng(-6.2019, 106.7815),
        "Alam Sutera" to LatLng(-6.2236, 106.6494),
        "Bekasi" to LatLng(-6.2216, 107.0007)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            binding.mapPreview.onCreate(savedInstanceState)
            mapCreated = true
            binding.mapPreview.getMapAsync { map ->
                googleMap = map
                updateMapForCampus(postActivity?.draft?.campus ?: "Kemanggisan")
            }
        } catch (_: Exception) {
            binding.mapPreview.visibility = View.GONE
        }

        val draft = postActivity?.draft
        binding.tvDate.text = draft?.dateLabel ?: "Today"
        binding.tvTime.text = draft?.timeLabel ?: "14:00 WIB"
        if (!draft?.spot.isNullOrBlank()) {
            binding.etSpot.setText(draft?.spot)
        }

        val currentCampus = draft?.campus ?: getString(R.string.campus_kemanggisan)
        when (currentCampus) {
            getString(R.string.campus_alam_sutera) -> binding.chipPostAlamSutera.isChecked = true
            getString(R.string.campus_bekasi) -> binding.chipPostBekasi.isChecked = true
            else -> binding.chipPostKemanggisan.isChecked = true
        }
        binding.tvMapCampus.text = "BINUS $currentCampus"

        binding.cgCampus.setOnCheckedStateChangeListener { _, checkedIds ->
            val chipId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val campus = binding.root.findViewById<Chip>(chipId)?.text?.toString() ?: return@setOnCheckedStateChangeListener
            postActivity?.draft?.campus = campus
            binding.tvMapCampus.text = "BINUS $campus"
            updateMapForCampus(campus)
        }

        binding.btnUseGps.setOnClickListener {
            binding.chipPostKemanggisan.isChecked = true
            if (binding.etSpot.text.isNullOrBlank()) {
                binding.etSpot.setText("Anggrek Campus, Main Lobby")
            }
            Toast.makeText(requireContext(), "Location set to BINUS Kemanggisan", Toast.LENGTH_SHORT).show()
        }

        binding.cardDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val formatted = SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(calendar.time)
                    binding.tvDate.text = formatted
                    postActivity?.draft?.dateLabel = formatted
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.cardTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val formatted = String.format(Locale.getDefault(), "%02d:%02d WIB", hourOfDay, minute)
                    binding.tvTime.text = formatted
                    postActivity?.draft?.timeLabel = formatted
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.swMatchAlerts.setOnCheckedChangeListener { _, isChecked ->
            postActivity?.draft?.matchAlerts = isChecked
        }

        syncStatusLabel()
    }

    fun syncStatusLabel() {
        if (_binding == null) return
        val status = postActivity?.draft?.status ?: ItemStatus.FOUND
        binding.tvSpotLabel.setText(
            if (status == ItemStatus.LOST) R.string.post_spot_label_lost
            else R.string.post_spot_label_found
        )
    }

    fun saveToDraft() {
        if (_binding == null) return
        val draft = postActivity?.draft ?: return
        draft.spot = binding.etSpot.text?.toString()?.trim().orEmpty()
        draft.matchAlerts = binding.swMatchAlerts.isChecked
    }

    private fun updateMapForCampus(campus: String) {
        val latLng = campusCoords[campus] ?: campusCoords.values.first()
        googleMap?.apply {
            clear()
            addMarker(MarkerOptions().position(latLng).title("BINUS $campus"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    override fun onStart() {
        super.onStart()
        if (mapCreated) runCatching { binding.mapPreview.onStart() }
    }

    override fun onResume() {
        super.onResume()
        if (mapCreated) runCatching { binding.mapPreview.onResume() }
    }

    override fun onPause() {
        if (mapCreated) runCatching { binding.mapPreview.onPause() }
        super.onPause()
    }

    override fun onStop() {
        if (mapCreated) runCatching { binding.mapPreview.onStop() }
        super.onStop()
    }

    override fun onDestroyView() {
        if (mapCreated) runCatching { binding.mapPreview.onDestroy() }
        super.onDestroyView()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (mapCreated && _binding != null) runCatching { binding.mapPreview.onLowMemory() }
    }
}
