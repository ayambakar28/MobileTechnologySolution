package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.ReportAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReportAdapter
    private var selectedStatus: ItemStatus? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReportAdapter(
            onItemClick = { item ->
                val intent = Intent(requireContext(), ItemDetailActivity::class.java).apply {
                    putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id)
                }
                startActivity(intent)
            },
            onBookmarkClick = { item ->
                item.isSaved = !item.isSaved
                renderItems()
            }
        )
        binding.rvItems.adapter = adapter

        binding.header.btnHeaderNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
        binding.header.btnHeaderProfile.setOnClickListener {
            (activity as? MainActivity)?.openProfileTab()
        }
        binding.btnNotifications.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
        binding.ivUserAvatar.setOnClickListener {
            (activity as? MainActivity)?.openProfileTab()
        }

        binding.cardSearch.setOnClickListener {
            (activity as? MainActivity)?.openSearchTab(openFilter = false)
        }
        binding.btnFilter.setOnClickListener {
            (activity as? MainActivity)?.openSearchTab(openFilter = true)
        }
        binding.btnSeeAll.setOnClickListener {
            (activity as? MainActivity)?.openSearchTab(openFilter = false)
        }

        binding.cgStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            selectedStatus = when (checkedIds.firstOrNull()) {
                R.id.chip_lost -> ItemStatus.LOST
                R.id.chip_found -> ItemStatus.FOUND
                R.id.chip_returned -> ItemStatus.RETURNED
                else -> null
            }
            renderItems()
        }

        binding.emptyState.btnEmptyAction.visibility = View.VISIBLE
        binding.emptyState.btnEmptyAction.setOnClickListener {
            (activity as? MainActivity)?.openPostItem()
        }

        refreshUi()
    }

    override fun onResume() {
        super.onResume()
        refreshUi()
    }

    private fun refreshUi() {
        val user = AppRepository.currentUser
        binding.tvGreeting.text = getString(R.string.home_greeting_format, user.firstName)
        binding.tvUserCampus.text = "${user.campus} Campus"

        if (!user.avatarUri.isNullOrBlank()) {
            try {
                binding.ivUserAvatar.setImageURI(Uri.parse(user.avatarUri))
                binding.ivUserAvatar.imageTintList = null
                binding.ivUserAvatar.setContentPadding(0, 0, 0, 0)
            } catch (_: Exception) {
            }
        }

        val unread = AppRepository.unreadNotificationCount()
        if (unread > 0) {
            binding.tvNotifBadge.visibility = View.VISIBLE
            binding.tvNotifBadge.text = if (unread > 9) "9+" else unread.toString()
        } else {
            binding.tvNotifBadge.visibility = View.GONE
        }

        binding.tvReturnedStat.text = getString(
            R.string.home_returned_stat_format,
            AppRepository.totalReturnedThisSemester()
        )

        renderItems()
    }

    private fun renderItems() {
        val filtered = AppRepository.items.filter { item ->
            selectedStatus == null || item.status == selectedStatus
        }
        adapter.submitList(filtered.take(5))
        binding.emptyState.root.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvItems.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
