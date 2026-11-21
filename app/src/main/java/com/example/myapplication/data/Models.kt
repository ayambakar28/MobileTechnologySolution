package com.example.myapplication.data

import androidx.annotation.DrawableRes

enum class ItemStatus {
    LOST, FOUND, RETURNED
}

data class ReportItem(
    val id: String,
    val code: String,
    val title: String,
    var status: ItemStatus,
    val category: String,
    val colors: List<String> = emptyList(),
    val shape: String = "",
    val itemType: String = "",
    val brand: String = "",
    val description: String = "",
    val marks: String = "",
    val hiddenDetail: String = "",
    val campus: String = "Kemanggisan",
    val location: String = "",
    val locationNote: String = "",
    val postedTime: String = "Just now",
    val dateTimeLabel: String = "Today",
    val posterName: String = "Rina Putri",
    val posterInfo: String = "BINUS Kemanggisan · Computer Science",
    val posterVerified: Boolean = true,
    val isMine: Boolean = false,
    var isSaved: Boolean = false,
    var views: Int = 1,
    var inquiries: Int = 0,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val photoUris: List<String> = emptyList(),
    @param:DrawableRes val placeholderIcon: Int = 0
)

sealed class ChatMessage {
    abstract val id: String
    abstract val time: String

    data class Text(
        override val id: String,
        val text: String,
        override val time: String,
        val isOutgoing: Boolean
    ) : ChatMessage()

    data class Image(
        override val id: String,
        val filename: String,
        val caption: String,
        override val time: String,
        val isOutgoing: Boolean,
        val imageUri: String? = null
    ) : ChatMessage()

    data class System(
        override val id: String,
        val text: String,
        override val time: String = ""
    ) : ChatMessage()
}

data class ChatRoom(
    val id: String,
    val peerName: String,
    val peerVerified: Boolean = true,
    val isOnline: Boolean = true,
    val itemId: String,
    val itemTitle: String,
    var itemStatus: ItemStatus,
    val itemLocation: String,
    var lastMessage: String,
    var lastTime: String,
    var unreadCount: Int = 0,
    var isArchived: Boolean = false,
    val messages: MutableList<ChatMessage> = mutableListOf()
)

enum class NotifType {
    MATCH, MESSAGE, STATUS
}

data class NotificationItem(
    val id: String,
    val type: NotifType,
    val title: String,
    val body: String,
    val time: String,
    var isUnread: Boolean = true,
    val relatedItemId: String? = null,
    val relatedChatId: String? = null
)

data class FilterState(
    val status: ItemStatus? = null,
    val campus: String? = null,
    val category: String? = null,
    val colors: Set<String> = emptySet(),
    val dateRange: String = "Any time",
    val sortBy: String = "Newest"
) {
    fun activeCount(): Int {
        var count = 0
        if (status != null) count++
        if (!campus.isNullOrBlank()) count++
        if (!category.isNullOrBlank()) count++
        if (colors.isNotEmpty()) count++
        if (dateRange != "Any time") count++
        if (sortBy != "Newest") count++
        return count
    }

    fun summary(): String {
        val parts = mutableListOf<String>()
        status?.let {
            parts.add(
                when (it) {
                    ItemStatus.LOST -> "Lost"
                    ItemStatus.FOUND -> "Found"
                    ItemStatus.RETURNED -> "Returned"
                }
            )
        }
        if (!campus.isNullOrBlank()) parts.add(campus)
        if (!category.isNullOrBlank()) parts.add(category)
        if (colors.isNotEmpty()) parts.add(colors.joinToString(", "))
        if (dateRange != "Any time") parts.add(dateRange)
        if (sortBy != "Newest") parts.add(sortBy)
        return parts.joinToString(" · ")
    }
}

data class UserProfile(
    var name: String = "Rina Putri",
    var nim: String = "2501234567",
    var major: String = "Computer Science",
    var campus: String = "Kemanggisan",
    var isVerified: Boolean = true,
    var avatarUri: String? = null
) {
    val firstName: String
        get() = name.trim().substringBefore(" ").ifBlank { "Student" }
}
