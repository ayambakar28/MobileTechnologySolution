package com.example.myapplication.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.ChatMessage
import com.example.myapplication.data.ChatRoom
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.data.NotifType
import com.example.myapplication.data.NotificationItem
import com.example.myapplication.data.ReportItem
import com.example.myapplication.databinding.ItemChatRoomBinding
import com.example.myapplication.databinding.ItemDetailPhotoBinding
import com.example.myapplication.databinding.ItemMessageImageIncomingBinding
import com.example.myapplication.databinding.ItemMessageImageOutcomingBinding
import com.example.myapplication.databinding.ItemMessageIncomingBinding
import com.example.myapplication.databinding.ItemMessageOutgoingBinding
import com.example.myapplication.databinding.ItemMessageSystemBinding
import com.example.myapplication.databinding.ItemMyPostBinding
import com.example.myapplication.databinding.ItemNotificationBinding
import com.example.myapplication.databinding.ItemReportBinding
import com.example.myapplication.databinding.ItemSearchResultBinding

class ReportAdapter(
    private val onItemClick: (ReportItem) -> Unit,
    private val onBookmarkClick: (ReportItem) -> Unit
) : RecyclerView.Adapter<ReportAdapter.VH>() {

    private var items: List<ReportItem> = emptyList()

    fun submitList(newItems: List<ReportItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class VH(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val b = holder.binding
        b.tvCode.text = item.code
        b.tvTitle.text = item.title
        b.tvLocation.text = item.location.ifBlank { "${item.campus} Campus" }
        b.tvTime.text = item.postedTime
        UiHelpers.bindStatusBadge(b.tvStatus, item.status)
        UiHelpers.loadPhotoOrPlaceholder(b.ivPhoto, item.photoUris.firstOrNull())

        b.btnBookmark.setImageResource(
            if (item.isSaved) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border
        )
        b.btnBookmark.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                b.root.context,
                if (item.isSaved) R.color.cf_navy_700 else R.color.cf_ink_muted
            )
        )

        b.root.setOnClickListener { onItemClick(item) }
        b.btnBookmark.setOnClickListener { onBookmarkClick(item) }
    }

    override fun getItemCount(): Int = items.size
}

class SearchResultAdapter(
    private val onItemClick: (ReportItem) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.VH>() {

    private var items: List<ReportItem> = emptyList()

    fun submitList(newItems: List<ReportItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class VH(val binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val b = holder.binding
        b.tvTitle.text = item.title
        b.tvCategory.text = item.category
        val loc = item.location.ifBlank { "${item.campus} Campus" }
        b.tvLocationTime.text = "$loc · ${item.postedTime}"
        UiHelpers.bindStatusBadge(b.tvStatus, item.status)
        UiHelpers.loadPhotoOrPlaceholder(b.ivPhoto, item.photoUris.firstOrNull())
        b.root.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size
}

class ChatRoomAdapter(
    private val onChatClick: (ChatRoom) -> Unit
) : RecyclerView.Adapter<ChatRoomAdapter.VH>() {

    private var rooms: List<ChatRoom> = emptyList()

    fun submitList(newRooms: List<ChatRoom>) {
        rooms = newRooms
        notifyDataSetChanged()
    }

    class VH(val binding: ItemChatRoomBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val room = rooms[position]
        val b = holder.binding
        b.tvName.text = room.peerName
        b.tvTime.text = room.lastTime
        b.tvLastMessage.text = room.lastMessage
        b.viewOnline.visibility = if (room.isOnline) View.VISIBLE else View.GONE

        b.ivAvatar.setImageResource(R.drawable.ic_person)
        b.ivAvatar.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(b.root.context, R.color.cf_navy_700)
        )
        val pad = UiHelpers.dpToPx(b.root.context, 12)
        b.ivAvatar.setPadding(pad, pad, pad, pad)

        UiHelpers.bindStatusBadge(b.tvItemTag, room.itemStatus, room.itemTitle)

        if (room.unreadCount > 0) {
            b.tvUnreadCount.visibility = View.VISIBLE
            b.tvUnreadCount.text = room.unreadCount.toString()
            b.ivReadStatus.visibility = View.GONE
        } else {
            b.tvUnreadCount.visibility = View.GONE
            b.ivReadStatus.visibility = View.VISIBLE
        }

        b.root.setOnClickListener { onChatClick(room) }
    }

    override fun getItemCount(): Int = rooms.size
}

class MyPostsAdapter(
    private val showMoreMenu: Boolean = true,
    private val onItemClick: (ReportItem) -> Unit,
    private val onMoreClick: (View, ReportItem) -> Unit
) : RecyclerView.Adapter<MyPostsAdapter.VH>() {

    private var items: List<ReportItem> = emptyList()

    fun submitList(newItems: List<ReportItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class VH(val binding: ItemMyPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemMyPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val b = holder.binding
        b.tvTitle.text = item.title
        val loc = item.location.ifBlank { "${item.campus} Campus" }
        b.tvLocationTime.text = "$loc · ${item.postedTime}"
        b.tvStat.text = if (item.status == ItemStatus.RETURNED) {
            "Handed over · ${item.views} views"
        } else {
            "${item.views} views · ${item.inquiries} inquiries"
        }
        UiHelpers.bindStatusBadge(b.tvStatus, item.status)
        UiHelpers.loadPhotoOrPlaceholder(b.ivPhoto, item.photoUris.firstOrNull())

        b.btnMore.visibility = if (showMoreMenu) View.VISIBLE else View.GONE
        b.root.setOnClickListener { onItemClick(item) }
        b.btnMore.setOnClickListener { onMoreClick(it, item) }
    }

    override fun getItemCount(): Int = items.size
}

class NotificationAdapter(
    private val onNotifClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.VH>() {

    private var items: List<NotificationItem> = emptyList()

    fun submitList(newItems: List<NotificationItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class VH(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val notif = items[position]
        val b = holder.binding
        val ctx = b.root.context

        b.tvTitle.text = notif.title
        b.tvBody.text = notif.body
        b.tvTime.text = notif.time
        b.viewUnread.visibility = if (notif.isUnread) View.VISIBLE else View.GONE

        val (iconRes, bgColorRes, tintColorRes) = when (notif.type) {
            NotifType.MATCH -> Triple(R.drawable.ic_auto_awesome, R.color.cf_found_soft, R.color.cf_found_text)
            NotifType.MESSAGE -> Triple(R.drawable.ic_chat, R.color.cf_blue_100, R.color.cf_navy_700)
            NotifType.STATUS -> Triple(R.drawable.ic_check_circle, R.color.cf_lavender_100, R.color.cf_navy_900)
        }
        b.ivIcon.setImageResource(iconRes)
        b.ivIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, bgColorRes))
        b.ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, tintColorRes))

        b.root.setOnClickListener { onNotifClick(notif) }
    }

    override fun getItemCount(): Int = items.size
}

class DetailPhotoAdapter(
    private val photos: List<String?>,
    private val onPhotoClick: (String?) -> Unit
) : RecyclerView.Adapter<DetailPhotoAdapter.VH>() {

    class VH(val binding: ItemDetailPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemDetailPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val uri = photos[position]
        UiHelpers.loadPhotoOrPlaceholder(holder.binding.root, uri)
        holder.binding.root.setOnClickListener { onPhotoClick(uri) }
    }

    override fun getItemCount(): Int = photos.size
}

class ChatMessageAdapter(
    private val onImageClick: (ChatMessage.Image) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_INCOMING = 1
        private const val TYPE_OUTGOING = 2
        private const val TYPE_SYSTEM = 3
        private const val TYPE_IMAGE_INCOMING = 4
        private const val TYPE_IMAGE_OUTGOING = 5
    }

    private var messages: List<ChatMessage> = emptyList()

    fun submitList(newMessages: List<ChatMessage>) {
        messages = newMessages.toList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (val msg = messages[position]) {
        is ChatMessage.Text -> if (msg.isOutgoing) TYPE_OUTGOING else TYPE_INCOMING
        is ChatMessage.Image -> if (msg.isOutgoing) TYPE_IMAGE_OUTGOING else TYPE_IMAGE_INCOMING
        is ChatMessage.System -> TYPE_SYSTEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INCOMING -> IncomingVH(ItemMessageIncomingBinding.inflate(inflater, parent, false))
            TYPE_OUTGOING -> OutgoingVH(ItemMessageOutgoingBinding.inflate(inflater, parent, false))
            TYPE_SYSTEM -> SystemVH(ItemMessageSystemBinding.inflate(inflater, parent, false))
            TYPE_IMAGE_INCOMING -> ImageIncomingVH(ItemMessageImageIncomingBinding.inflate(inflater, parent, false))
            else -> ImageOutgoingVH(ItemMessageImageOutcomingBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val msg = messages[position]) {
            is ChatMessage.Text -> {
                if (holder is IncomingVH) {
                    holder.binding.tvMessage.text = msg.text
                    holder.binding.tvTime.text = msg.time
                } else if (holder is OutgoingVH) {
                    holder.binding.tvMessage.text = msg.text
                    holder.binding.tvTime.text = msg.time
                }
            }
            is ChatMessage.System -> {
                if (holder is SystemVH) {
                    holder.binding.tvSystemMessage.text = msg.text
                }
            }
            is ChatMessage.Image -> {
                if (holder is ImageIncomingVH) {
                    holder.binding.tvFilename.text = msg.filename
                    holder.binding.tvCaption.text = msg.caption
                    holder.binding.tvTime.text = msg.time
                    UiHelpers.loadPhotoOrPlaceholder(holder.binding.ivImage, msg.imageUri)
                    holder.binding.ivImage.setOnClickListener { onImageClick(msg) }
                } else if (holder is ImageOutgoingVH) {
                    holder.binding.tvFilename.text = msg.filename
                    holder.binding.tvCaption.text = msg.caption
                    holder.binding.tvTime.text = msg.time
                    UiHelpers.loadPhotoOrPlaceholder(holder.binding.ivImage, msg.imageUri)
                    holder.binding.ivImage.setOnClickListener { onImageClick(msg) }
                }
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    class IncomingVH(val binding: ItemMessageIncomingBinding) : RecyclerView.ViewHolder(binding.root)
    class OutgoingVH(val binding: ItemMessageOutgoingBinding) : RecyclerView.ViewHolder(binding.root)
    class SystemVH(val binding: ItemMessageSystemBinding) : RecyclerView.ViewHolder(binding.root)
    class ImageIncomingVH(val binding: ItemMessageImageIncomingBinding) : RecyclerView.ViewHolder(binding.root)
    class ImageOutgoingVH(val binding: ItemMessageImageOutcomingBinding) : RecyclerView.ViewHolder(binding.root)
}
