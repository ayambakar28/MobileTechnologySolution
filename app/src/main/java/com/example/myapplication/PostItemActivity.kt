package com.example.myapplication

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.data.ReportItem
import com.example.myapplication.databinding.ActivityPostItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PostItemActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EDIT_ITEM_ID = "extra_edit_item_id"
    }

    data class PostDraft(
        var status: ItemStatus = ItemStatus.FOUND,
        var title: String = "",
        var category: String = "",
        val colors: MutableSet<String> = mutableSetOf(),
        var shape: String = "",
        var itemType: String = "",
        var brand: String = "",
        var description: String = "",
        var marks: String = "",
        var hiddenDetail: String = "",
        val photoUris: MutableList<String> = mutableListOf(),
        var campus: String = "Kemanggisan",
        var spot: String = "",
        var dateLabel: String = SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(Date()),
        var timeLabel: String = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault()).format(Date()),
        var matchAlerts: Boolean = true
    )

    private lateinit var binding: ActivityPostItemBinding
    val draft = PostDraft(campus = AppRepository.currentUser.campus)
    private var editingItem: ReportItem? = null
    private val handler = Handler(Looper.getMainLooper())

    private var step1Fragment: PostStep1Fragment? = null
    private var step2Fragment: PostStep2Fragment? = null
    private var step3Fragment: PostStep3Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPostItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editId = intent.getStringExtra(EXTRA_EDIT_ITEM_ID)
        editingItem = AppRepository.getItemById(editId)
        editingItem?.let { existing ->
            draft.status = if (existing.status == ItemStatus.LOST) ItemStatus.LOST else ItemStatus.FOUND
            draft.title = existing.title
            draft.category = existing.category
            draft.colors.clear()
            draft.colors.addAll(existing.colors)
            draft.shape = existing.shape
            draft.itemType = existing.itemType
            draft.brand = existing.brand
            draft.description = existing.description
            draft.marks = existing.marks
            draft.hiddenDetail = existing.hiddenDetail
            draft.photoUris.clear()
            draft.photoUris.addAll(existing.photoUris)
            draft.campus = existing.campus
            draft.spot = existing.location
        }

        binding.vpPost.isUserInputEnabled = false
        binding.vpPost.offscreenPageLimit = 2
        binding.vpPost.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3
            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> PostStep1Fragment().also { step1Fragment = it }
                1 -> PostStep2Fragment().also { step2Fragment = it }
                else -> PostStep3Fragment().also { step3Fragment = it }
            }
        }

        binding.vpPost.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateStepUi(position)
                if (position == 2) {
                    step3Fragment?.syncStatusLabel()
                }
            }
        })

        binding.btnClose.setOnClickListener { finish() }

        binding.btnBack.setOnClickListener {
            val current = binding.vpPost.currentItem
            if (current > 0) {
                binding.vpPost.currentItem = current - 1
            }
        }

        binding.btnNext.setOnClickListener {
            when (binding.vpPost.currentItem) {
                0 -> {
                    val step1 = step1Fragment
                        ?: supportFragmentManager.findFragmentByTag("f0") as? PostStep1Fragment
                    if (step1 == null || step1.validateAndSave()) {
                        binding.vpPost.currentItem = 1
                    }
                }
                1 -> {
                    binding.vpPost.currentItem = 2
                }
                2 -> {
                    val step3 = step3Fragment
                        ?: supportFragmentManager.findFragmentByTag("f2") as? PostStep3Fragment
                    step3?.saveToDraft()
                    submitPost()
                }
            }
        }

        updateStepUi(0)
    }

    private fun updateStepUi(position: Int) {
        val titleRes = when (position) {
            0 -> R.string.post_title_step1
            1 -> R.string.post_title_step2
            else -> R.string.post_title_step3
        }
        binding.tvPostTitle.setText(titleRes)
        binding.tvStepLabel.text = getString(R.string.post_step_format, position + 1)

        val doneColor = ContextCompat.getColor(this, R.color.cf_found_bg)
        val activeColor = ContextCompat.getColor(this, R.color.cf_navy_700)
        val idleColor = ContextCompat.getColor(this, R.color.cf_lavender_100)

        val segments = listOf(binding.step1, binding.step2, binding.step3)
        segments.forEachIndexed { idx, view ->
            val color = when {
                idx < position -> doneColor
                idx == position -> activeColor
                else -> idleColor
            }
            view.backgroundTintList = ColorStateList.valueOf(color)
        }

        binding.btnBack.visibility = if (position == 0) View.GONE else View.VISIBLE
        binding.tvFooterHint.visibility = if (position == 2) View.VISIBLE else View.GONE

        binding.btnNext.text = getString(
            when (position) {
                0 -> R.string.post_next_photo
                1 -> R.string.post_next_location
                else -> R.string.post_submit
            }
        )
    }

    private fun submitPost() {
        binding.progressPost.visibility = View.VISIBLE
        binding.btnNext.isEnabled = false
        binding.btnBack.isEnabled = false

        handler.postDelayed({
            val existing = editingItem
            if (existing != null) {
                val updated = existing.copy(
                    title = draft.title.ifBlank { existing.title },
                    status = draft.status,
                    category = draft.category.ifBlank { existing.category },
                    colors = draft.colors.toList(),
                    shape = draft.shape,
                    itemType = draft.itemType,
                    brand = draft.brand,
                    description = draft.description,
                    marks = draft.marks,
                    hiddenDetail = draft.hiddenDetail,
                    campus = draft.campus,
                    location = draft.spot,
                    locationNote = if (draft.spot.isNotBlank()) draft.spot else "${draft.campus} Campus",
                    dateTimeLabel = "${draft.dateLabel}, ${draft.timeLabel}",
                    photoUris = draft.photoUris.toList()
                )
                AppRepository.updateItem(updated)
                Toast.makeText(this, "Post updated!", Toast.LENGTH_SHORT).show()
            } else {
                val prefix = if (draft.status == ItemStatus.LOST) "L" else "F"
                val codeNum = (31..99).random()
                val user = AppRepository.currentUser

                val newItem = ReportItem(
                    id = "item_${UUID.randomUUID().toString().take(8)}",
                    code = "#$prefix$codeNum",
                    title = draft.title.ifBlank { "Campus Item" },
                    status = draft.status,
                    category = draft.category.ifBlank { getString(R.string.category_other) },
                    colors = draft.colors.toList(),
                    shape = draft.shape,
                    itemType = draft.itemType,
                    brand = draft.brand,
                    description = draft.description,
                    marks = draft.marks,
                    hiddenDetail = draft.hiddenDetail,
                    campus = draft.campus,
                    location = draft.spot,
                    locationNote = if (draft.spot.isNotBlank()) draft.spot else "${draft.campus} Campus",
                    postedTime = "Just now",
                    dateTimeLabel = "${draft.dateLabel}, ${draft.timeLabel}",
                    posterName = user.name,
                    posterInfo = "BINUS ${user.campus} · ${user.major}",
                    posterVerified = user.isVerified,
                    isMine = true,
                    isSaved = false,
                    views = 1,
                    inquiries = 0,
                    photoUris = draft.photoUris.toList()
                )
                AppRepository.addItem(newItem, matchAlerts = draft.matchAlerts)
                Toast.makeText(this, "Item posted successfully!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }, 400L)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
