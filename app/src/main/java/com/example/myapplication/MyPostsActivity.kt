package com.example.myapplication

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.data.ReportItem
import com.example.myapplication.databinding.ActivityMyPostsBinding
import com.example.myapplication.ui.MyPostsAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyPostsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SAVED_MODE = "extra_saved_mode"
    }

    private lateinit var binding: ActivityMyPostsBinding
    private lateinit var adapter: MyPostsAdapter
    private var savedMode = false
    private var selectedStatus: ItemStatus? = null
    private var currentPage = 1
    private val pageSize = 4
    private var totalPages = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        savedMode = intent.getBooleanExtra(EXTRA_SAVED_MODE, false)
        binding.tvTitle.setText(if (savedMode) R.string.profile_saved else R.string.my_posts_title)
        binding.btnPostNew.visibility = if (savedMode) View.GONE else View.VISIBLE

        adapter = MyPostsAdapter(
            showMoreMenu = !savedMode,
            onItemClick = { item ->
                val intent = Intent(this, ItemDetailActivity::class.java).apply {
                    putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id)
                }
                startActivity(intent)
            },
            onMoreClick = { anchor, item -> showPostOptions(anchor, item) }
        )
        binding.rvMyPosts.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }
        binding.btnFilter.setOnClickListener {
            binding.chipMyAll.isChecked = true
            Toast.makeText(this, "Showing all items", Toast.LENGTH_SHORT).show()
        }
        binding.btnPostNew.setOnClickListener {
            startActivity(Intent(this, PostItemActivity::class.java))
        }

        binding.cgMyPosts.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedStatus = when (checkedIds.firstOrNull()) {
                R.id.chip_my_lost -> ItemStatus.LOST
                R.id.chip_my_found -> ItemStatus.FOUND
                R.id.chip_my_returned -> ItemStatus.RETURNED
                else -> null
            }
            currentPage = 1
            renderList()
        }

        binding.pagination.btnPrevPage.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                renderList()
            }
        }
        binding.pagination.btnNextPage.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                renderList()
            }
        }
        binding.pagination.tvPage1.setOnClickListener { selectPage(1) }
        binding.pagination.tvPage2.setOnClickListener { selectPage(2) }
        binding.pagination.tvPage3.setOnClickListener { selectPage(3) }

        renderList()
    }

    override fun onResume() {
        super.onResume()
        renderList()
    }

    private fun showPostOptions(anchor: View, item: ReportItem) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_my_post, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    val intent = Intent(this, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id)
                    }
                    startActivity(intent)
                    true
                }
                R.id.action_mark_found -> {
                    item.status = ItemStatus.FOUND
                    renderList()
                    Toast.makeText(this, "Marked as Found", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_mark_returned -> {
                    item.status = ItemStatus.RETURNED
                    renderList()
                    Toast.makeText(this, "Marked as Returned", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_delete -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.action_delete)
                        .setMessage("Delete '${item.title}'?")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.action_delete) { _, _ ->
                            AppRepository.deleteItem(item.id)
                            renderList()
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun renderList() {
        val source = if (savedMode) {
            AppRepository.items.filter { it.isSaved }
        } else {
            AppRepository.items.filter { it.isMine }
        }

        binding.chipMyAll.text = "${getString(R.string.status_all)} (${source.size})"
        binding.chipMyLost.text = "${getString(R.string.status_lost)} (${source.count { it.status == ItemStatus.LOST }})"
        binding.chipMyFound.text = "${getString(R.string.status_found)} (${source.count { it.status == ItemStatus.FOUND }})"
        binding.chipMyReturned.text = "${getString(R.string.status_returned)} (${source.count { it.status == ItemStatus.RETURNED }})"

        val filtered = source.filter { selectedStatus == null || it.status == selectedStatus }

        totalPages = maxOf(1, (filtered.size + pageSize - 1) / pageSize)
        if (currentPage > totalPages) currentPage = totalPages

        val fromIdx = (currentPage - 1) * pageSize
        val pageItems = if (fromIdx < filtered.size) {
            filtered.subList(fromIdx, minOf(fromIdx + pageSize, filtered.size))
        } else {
            emptyList()
        }

        adapter.submitList(pageItems)
        binding.emptyState.root.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvMyPosts.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        binding.tvShowing.text = getString(R.string.my_posts_showing_format, pageItems.size, filtered.size)
        binding.pagination.root.visibility = if (filtered.size > pageSize) View.VISIBLE else View.GONE

        updatePaginationUi()
    }

    private fun selectPage(page: Int) {
        if (page in 1..totalPages) {
            currentPage = page
            renderList()
        }
    }

    private fun updatePaginationUi() {
        val pageViews = listOf(
            binding.pagination.tvPage1,
            binding.pagination.tvPage2,
            binding.pagination.tvPage3
        )
        pageViews.forEachIndexed { idx, tv ->
            val pageNum = idx + 1
            tv.visibility = if (pageNum <= totalPages) View.VISIBLE else View.GONE
            stylePageButton(tv, pageNum == currentPage)
        }
    }

    private fun stylePageButton(tv: TextView, selected: Boolean) {
        if (selected) {
            tv.setBackgroundResource(R.drawable.bg_circle)
            tv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.cf_navy_900))
            tv.setTextColor(ContextCompat.getColor(this, R.color.cf_on_dark))
        } else {
            tv.background = null
            tv.backgroundTintList = null
            tv.setTextColor(ContextCompat.getColor(this, R.color.cf_ink))
        }
    }
}
