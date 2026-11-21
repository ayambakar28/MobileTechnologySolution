package com.example.myapplication

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.FilterState
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.databinding.FragmentSearchBinding
import com.example.myapplication.databinding.ItemChipRecentSearchBinding
import com.example.myapplication.ui.FilterBottomSheet
import com.example.myapplication.ui.SearchResultAdapter
import com.google.android.material.tabs.TabLayout

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchResultAdapter
    private var filterState = FilterState()
    private var query: String = ""
    private var currentPage = 1
    private val pageSize = 4
    private var totalPages = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchResultAdapter { item ->
            val intent = Intent(requireContext(), ItemDetailActivity::class.java).apply {
                putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id)
            }
            startActivity(intent)
        }
        binding.rvResults.adapter = adapter

        binding.header.btnHeaderNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
        binding.header.btnHeaderProfile.setOnClickListener {
            (activity as? MainActivity)?.openProfileTab()
        }
        binding.btnBack.setOnClickListener {
            (activity as? MainActivity)?.openHomeTab()
        }

        binding.btnClearSearch.visibility = View.GONE
        binding.etSearch.doAfterTextChanged { text ->
            query = text?.toString()?.trim().orEmpty()
            binding.btnClearSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
            currentPage = 1
            renderResults()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (query.isNotEmpty()) {
                    AppRepository.addRecentSearch(query)
                    renderRecentSearches()
                }
                true
            } else {
                false
            }
        }

        binding.btnClearSearch.setOnClickListener {
            binding.etSearch.setText("")
        }

        binding.btnClearRecent.setOnClickListener {
            AppRepository.recentSearches.clear()
            renderRecentSearches()
        }

        binding.tabStatus.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val newStatus = when (tab.position) {
                    1 -> ItemStatus.LOST
                    2 -> ItemStatus.FOUND
                    3 -> ItemStatus.RETURNED
                    else -> null
                }
                if (filterState.status != newStatus) {
                    filterState = filterState.copy(status = newStatus)
                    currentPage = 1
                    renderFilterBanner()
                    renderResults()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val openFilterAction = View.OnClickListener { openFilterSheet() }
        binding.btnFilter.setOnClickListener(openFilterAction)
        binding.btnEditFilters.setOnClickListener(openFilterAction)

        binding.pagination.btnPrevPage.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                renderResults()
            }
        }
        binding.pagination.btnNextPage.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                renderResults()
            }
        }
        binding.pagination.tvPage1.setOnClickListener { selectPage(1) }
        binding.pagination.tvPage2.setOnClickListener { selectPage(2) }
        binding.pagination.tvPage3.setOnClickListener { selectPage(3) }

        binding.emptyState.tvEmptyTitle.setText(R.string.search_no_results_title)
        binding.emptyState.tvEmptyMessage.setText(R.string.search_no_results_msg)

        renderRecentSearches()
        renderFilterBanner()
        renderResults()
    }

    override fun onResume() {
        super.onResume()
        val main = activity as? MainActivity
        if (main != null && main.openFilterOnSearchTab) {
            main.openFilterOnSearchTab = false
            openFilterSheet()
        }
        renderResults()
    }

    private fun openFilterSheet() {
        FilterBottomSheet.show(requireContext(), filterState) { updated ->
            filterState = updated
            currentPage = 1
            syncTabWithStatus(updated.status)
            renderFilterBanner()
            renderResults()
        }
    }

    private fun syncTabWithStatus(status: ItemStatus?) {
        val targetIndex = when (status) {
            ItemStatus.LOST -> 1
            ItemStatus.FOUND -> 2
            ItemStatus.RETURNED -> 3
            null -> 0
        }
        binding.tabStatus.getTabAt(targetIndex)?.select()
    }

    private fun renderRecentSearches() {
        val recent = AppRepository.recentSearches
        binding.groupRecent.visibility = if (recent.isEmpty()) View.GONE else View.VISIBLE
        binding.cgRecentSearches.removeAllViews()
        for (term in recent) {
            val chipBinding = ItemChipRecentSearchBinding.inflate(
                layoutInflater, binding.cgRecentSearches, false
            )
            chipBinding.root.text = term
            chipBinding.root.setOnClickListener {
                binding.etSearch.setText(term)
                binding.etSearch.setSelection(term.length)
            }
            binding.cgRecentSearches.addView(chipBinding.root)
        }
    }

    private fun renderFilterBanner() {
        val count = filterState.activeCount()
        if (count > 0) {
            binding.tvFilterCount.visibility = View.VISIBLE
            binding.tvFilterCount.text = count.toString()
            binding.cardActiveFilters.visibility = View.VISIBLE
            binding.tvFilterSummary.text = filterState.summary()
        } else {
            binding.tvFilterCount.visibility = View.GONE
            binding.cardActiveFilters.visibility = View.GONE
        }
    }

    private fun renderResults() {
        val filtered = AppRepository.items.filter { item ->
            val matchesQuery = query.isEmpty() ||
                item.title.contains(query, ignoreCase = true) ||
                item.location.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true) ||
                item.campus.contains(query, ignoreCase = true) ||
                item.colors.any { it.contains(query, ignoreCase = true) }

            val matchesStatus = filterState.status == null || item.status == filterState.status
            val matchesCampus = filterState.campus.isNullOrBlank() ||
                item.campus.equals(filterState.campus, ignoreCase = true)
            val matchesCategory = filterState.category.isNullOrBlank() ||
                item.category.equals(filterState.category, ignoreCase = true)
            val matchesColors = filterState.colors.isEmpty() ||
                item.colors.any { c -> filterState.colors.any { it.equals(c, ignoreCase = true) } }

            matchesQuery && matchesStatus && matchesCampus && matchesCategory && matchesColors
        }

        totalPages = maxOf(1, (filtered.size + pageSize - 1) / pageSize)
        if (currentPage > totalPages) currentPage = totalPages

        val fromIndex = (currentPage - 1) * pageSize
        val pageItems = if (fromIndex < filtered.size) {
            filtered.subList(fromIndex, minOf(fromIndex + pageSize, filtered.size))
        } else {
            emptyList()
        }

        adapter.submitList(pageItems)
        binding.emptyState.root.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvResults.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        binding.pagination.root.visibility = if (filtered.size > pageSize) View.VISIBLE else View.GONE

        updatePaginationUi()
    }

    private fun selectPage(page: Int) {
        if (page in 1..totalPages) {
            currentPage = page
            renderResults()
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
        binding.pagination.btnPrevPage.alpha = if (currentPage > 1) 1f else 0.4f
        binding.pagination.btnNextPage.alpha = if (currentPage < totalPages) 1f else 0.4f
    }

    private fun stylePageButton(tv: TextView, selected: Boolean) {
        val ctx = requireContext()
        if (selected) {
            tv.setBackgroundResource(R.drawable.bg_circle)
            tv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.cf_navy_900))
            tv.setTextColor(ContextCompat.getColor(ctx, R.color.cf_on_dark))
        } else {
            tv.background = null
            tv.backgroundTintList = null
            tv.setTextColor(ContextCompat.getColor(ctx, R.color.cf_ink))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
