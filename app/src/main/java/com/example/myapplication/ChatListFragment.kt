package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.databinding.FragmentChatListBinding
import com.example.myapplication.ui.ChatRoomAdapter

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ChatRoomAdapter
    private var query: String = ""
    private var filterMode: Int = R.id.chip_chat_all
    private var showingArchived = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatRoomAdapter { room ->
            room.unreadCount = 0
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHAT_ID, room.id)
            }
            startActivity(intent)
        }
        binding.rvChats.adapter = adapter

        binding.btnSearchChat.setOnClickListener {
            binding.etSearchChat.requestFocus()
        }
        binding.btnProfile.setOnClickListener {
            (activity as? MainActivity)?.openProfileTab()
        }

        binding.btnClearChatSearch.visibility = View.GONE
        binding.etSearchChat.doAfterTextChanged { text ->
            query = text?.toString()?.trim().orEmpty()
            binding.btnClearChatSearch.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE
            renderChats()
        }
        binding.btnClearChatSearch.setOnClickListener {
            binding.etSearchChat.setText("")
        }

        binding.cgChatFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            filterMode = checkedIds.firstOrNull() ?: R.id.chip_chat_all
            renderChats()
        }

        binding.btnArchived.setOnClickListener {
            showingArchived = !showingArchived
            renderChats()
        }

        renderChats()
    }

    override fun onResume() {
        super.onResume()
        renderChats()
    }

    private fun renderChats() {
        val activeRooms = AppRepository.chatRooms.filter { !it.isArchived }
        val archivedRooms = AppRepository.chatRooms.filter { it.isArchived }

        val unreadTotal = activeRooms.count { it.unreadCount > 0 }
        binding.chipChatUnread.text = if (unreadTotal > 0) {
            "${getString(R.string.chat_filter_unread)} $unreadTotal"
        } else {
            getString(R.string.chat_filter_unread)
        }

        val source = if (showingArchived) archivedRooms else activeRooms
        val filtered = source.filter { room ->
            val matchesQuery = query.isEmpty() ||
                room.peerName.contains(query, ignoreCase = true) ||
                room.itemTitle.contains(query, ignoreCase = true) ||
                room.lastMessage.contains(query, ignoreCase = true)

            val matchesFilter = when (filterMode) {
                R.id.chip_chat_unread -> room.unreadCount > 0
                R.id.chip_chat_lost -> room.itemStatus == ItemStatus.LOST
                R.id.chip_chat_found -> room.itemStatus == ItemStatus.FOUND
                else -> true
            }
            matchesQuery && matchesFilter
        }

        adapter.submitList(filtered)
        binding.emptyState.root.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvChats.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE

        binding.tvChatCount.text = getString(
            R.string.chat_count_format,
            filtered.size,
            source.size
        )

        binding.btnArchived.text = if (showingArchived) {
            "Active chats (${activeRooms.size}) →"
        } else {
            getString(R.string.chat_archived_format, archivedRooms.size)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
