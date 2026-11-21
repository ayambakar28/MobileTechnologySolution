package com.example.myapplication

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.ChatMessage
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.data.ReportItem
import com.example.myapplication.databinding.ActivityItemDetailBinding
import com.example.myapplication.databinding.BottomSheetClaimBinding
import com.example.myapplication.ui.DetailPhotoAdapter
import com.example.myapplication.ui.UiHelpers
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator

class ItemDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
    }

    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var item: ReportItem
    private var claimAttachUri: String? = null

    private val claimPhotoLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                claimAttachUri = uri.toString()
                Toast.makeText(this, "Proof photo attached", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val itemId = intent.getStringExtra(EXTRA_ITEM_ID)
        item = AppRepository.getItemById(itemId) ?: AppRepository.items.firstOrNull() ?: run {
            finish()
            return
        }
        AppRepository.recordItemView(item.id)

        setupToolbar()
        setupPhotos()
        bindItemDetails()
        setupActions()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        updateSaveMenuIcon()
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_share -> {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${item.title} (${item.status.name}) at BINUS ${item.campus} - ${item.location}"
                        )
                    }
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
                    true
                }
                R.id.action_save -> {
                    item.isSaved = !item.isSaved
                    updateSaveMenuIcon()
                    Toast.makeText(
                        this,
                        if (item.isSaved) "Saved to your items" else "Removed from saved items",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                R.id.action_report -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.action_report)
                        .setMessage("Report this post to BINUS CampusFind moderators?")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.action_report) { _, _ ->
                            Toast.makeText(this, "Report submitted. Thank you.", Toast.LENGTH_SHORT).show()
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun updateSaveMenuIcon() {
        val saveItem = binding.toolbar.menu.findItem(R.id.action_save)
        saveItem?.setIcon(if (item.isSaved) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border)
    }

    private fun setupPhotos() {
        val photos: List<String?> = if (item.photoUris.isNotEmpty()) item.photoUris else listOf(null)
        binding.vpPhotos.adapter = DetailPhotoAdapter(photos) { uri ->
            val intent = Intent(this, ImageViewerActivity::class.java).apply {
                putExtra(ImageViewerActivity.EXTRA_IMAGE_URI, uri)
                putExtra(ImageViewerActivity.EXTRA_CAPTION, "${item.title} · ${item.postedTime}")
            }
            startActivity(intent)
        }
        TabLayoutMediator(binding.tabPhotoDots, binding.vpPhotos) { _, _ -> }.attach()
        binding.tabPhotoDots.visibility = if (photos.size > 1) View.VISIBLE else View.GONE
        binding.tvPhotoCounter.text = "1/${photos.size}"

        binding.vpPhotos.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tvPhotoCounter.text = "${position + 1}/${photos.size}"
            }
        })
    }

    private fun bindItemDetails() {
        binding.tvTitle.text = item.title
        binding.tvPostedTime.text = item.postedTime
        UiHelpers.bindStatusBadge(binding.tvStatus, item.status)
        binding.tvCategory.text = item.category

        binding.ivPosterAvatar.setImageResource(R.drawable.ic_person)
        binding.ivPosterAvatar.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.cf_navy_700))
        val pad = UiHelpers.dpToPx(this, 8)
        binding.ivPosterAvatar.setPadding(pad, pad, pad, pad)

        binding.tvPosterName.text = item.posterName
        binding.tvPosterInfo.text = item.posterInfo
        binding.tvPosterVerified.visibility = if (item.posterVerified) View.VISIBLE else View.GONE

        binding.tvDescription.text = item.description.ifBlank { "No additional description provided." }

        bindAttrRow(binding.rowAttrColor, binding.tvAttrColor, item.colors.joinToString(", "))
        bindAttrRow(binding.rowAttrShape, binding.tvAttrShape, item.shape)
        bindAttrRow(binding.rowAttrType, binding.tvAttrType, item.itemType)
        bindAttrRow(binding.rowAttrBrand, binding.tvAttrBrand, item.brand)
        bindAttrRow(binding.rowAttrMarks, binding.tvAttrMarks, item.marks)

        bindJourney()

        if (item.location.isBlank() && item.campus.isBlank()) {
            binding.cardLocation.visibility = View.GONE
        } else {
            binding.cardLocation.visibility = View.VISIBLE
            binding.tvLocation.text = item.location.ifBlank { "BINUS ${item.campus} Campus" }
            binding.tvLocationTime.text = item.dateTimeLabel
        }

        updateActionBar()
    }

    private fun bindAttrRow(row: View, valueView: android.widget.TextView, value: String) {
        if (value.isBlank()) {
            row.visibility = View.GONE
        } else {
            row.visibility = View.VISIBLE
            valueView.text = value
        }
    }

    private fun bindJourney() {
        val step = when (item.status) {
            ItemStatus.LOST -> 1
            ItemStatus.FOUND -> 2
            ItemStatus.RETURNED -> 3
        }
        binding.tvJourneyStep.text = getString(R.string.detail_journey_step_format, step)

        val idleBg = ContextCompat.getColor(this, R.color.cf_lavender_100)
        val idleLine = ContextCompat.getColor(this, R.color.cf_lavender_200)
        val idleTint = ContextCompat.getColor(this, R.color.cf_ink_muted)

        val lostBg = ContextCompat.getColor(this, R.color.cf_lost_bg)
        val lostTint = ContextCompat.getColor(this, R.color.cf_lost_text)
        val foundBg = ContextCompat.getColor(this, R.color.cf_found_bg)
        val foundTint = ContextCompat.getColor(this, R.color.cf_found_text)
        val returnedBg = ContextCompat.getColor(this, R.color.cf_returned_bg)
        val returnedTint = ContextCompat.getColor(this, R.color.cf_returned_text)

        binding.journeyLost.backgroundTintList = ColorStateList.valueOf(lostBg)
        binding.journeyLost.imageTintList = ColorStateList.valueOf(lostTint)

        binding.journeyLine1.backgroundTintList =
            ColorStateList.valueOf(if (step >= 2) foundBg else idleLine)
        binding.journeyFound.backgroundTintList =
            ColorStateList.valueOf(if (step >= 2) foundBg else idleBg)
        binding.journeyFound.imageTintList =
            ColorStateList.valueOf(if (step >= 2) foundTint else idleTint)

        binding.journeyLine2.backgroundTintList =
            ColorStateList.valueOf(if (step >= 3) returnedBg else idleLine)
        binding.journeyReturned.backgroundTintList =
            ColorStateList.valueOf(if (step >= 3) returnedBg else idleBg)
        binding.journeyReturned.imageTintList =
            ColorStateList.valueOf(if (step >= 3) returnedTint else idleTint)
    }

    private fun updateActionBar() {
        if (item.isMine) {
            binding.groupVisitorActions.visibility = View.GONE
            binding.btnMarkReturned.visibility =
                if (item.status == ItemStatus.RETURNED) View.GONE else View.VISIBLE
        } else {
            binding.btnMarkReturned.visibility = View.GONE
            binding.groupVisitorActions.visibility = View.VISIBLE
            binding.btnClaim.setText(
                if (item.status == ItemStatus.LOST) R.string.detail_i_found_this
                else R.string.detail_this_is_mine
            )
            binding.btnClaim.isEnabled = item.status != ItemStatus.RETURNED
        }
    }

    private fun setupActions() {
        binding.btnMap.setOnClickListener {
            val intent = Intent(this, ItemMapActivity::class.java).apply {
                putExtra(ItemMapActivity.EXTRA_ITEM_ID, item.id)
            }
            startActivity(intent)
        }

        binding.btnChat.setOnClickListener {
            val room = AppRepository.getOrCreateChatForItem(item)
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHAT_ID, room.id)
            }
            startActivity(intent)
        }

        binding.btnClaim.setOnClickListener {
            showClaimBottomSheet()
        }

        binding.btnMarkReturned.setOnClickListener {
            AppRepository.updateItemStatus(item.id, ItemStatus.RETURNED)
            UiHelpers.bindStatusBadge(binding.tvStatus, item.status)
            bindJourney()
            updateActionBar()
            Toast.makeText(this, "Item marked as Returned!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showClaimBottomSheet() {
        claimAttachUri = null
        val dialog = BottomSheetDialog(this)
        val b = BottomSheetClaimBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(b.root)

        b.btnClaimAttach.setOnClickListener {
            claimPhotoLauncher.launch("image/*")
        }

        b.btnClaimSubmit.setOnClickListener {
            val proof = b.etClaimProof.text?.toString()?.trim().orEmpty()
            if (proof.isEmpty()) {
                b.tilClaimProof.error = "Please provide verification details."
                return@setOnClickListener
            }
            val room = AppRepository.submitClaim(item, proof, claimAttachUri)

            dialog.dismiss()
            Toast.makeText(this, "Claim sent! Opening chat...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHAT_ID, room.id)
            }
            startActivity(intent)
        }

        dialog.show()
    }
}
