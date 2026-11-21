package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

object AppRepository {

    private const val PREFS_NAME = "campus_find_prefs"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_CAMPUS = "user_campus"

    private val nowMillis = System.currentTimeMillis()
    private fun hoursAgo(h: Long): Long = nowMillis - TimeUnit.HOURS.toMillis(h)
    private fun daysAgo(d: Long): Long = nowMillis - TimeUnit.DAYS.toMillis(d)

    val currentUser = UserProfile()

    val recentSearches: MutableList<String> = mutableListOf(
        "AirPods",
        "Wallet",
        "Flazz Card",
        "Tumbler"
    )

    val items: MutableList<ReportItem> = mutableListOf(
        ReportItem(
            id = "item_1",
            code = "#F28",
            title = "Black Leather Wallet",
            status = ItemStatus.FOUND,
            category = "Wallet & Cards",
            colors = listOf("Black"),
            shape = "Rectangle",
            itemType = "Bifold wallet",
            brand = "Fossil",
            description = "Found on study table near the east window, 3rd floor library. Contains a BINUS student ID card inside.",
            marks = "Small scratch on the bottom right corner",
            hiddenDetail = "Passport photo inside inner sleeve",
            campus = "Kemanggisan",
            location = "Library 3rd Floor, Building B",
            locationNote = "Found on study table near east window",
            postedTime = "2h ago",
            dateTimeLabel = "Today, 14:15 WIB · Shelf A-14",
            posterName = "Dimas Arya",
            posterInfo = "BINUS Kemanggisan · Computer Science",
            posterVerified = true,
            isMine = false,
            isSaved = true,
            views = 24,
            inquiries = 3,
            createdAtMillis = hoursAgo(2),
            placeholderIcon = R.drawable.ic_image
        ),
        ReportItem(
            id = "item_2",
            code = "#L19",
            title = "Silver AirPods Pro Case",
            status = ItemStatus.LOST,
            category = "Electronics",
            colors = listOf("Gray / Silver", "White"),
            shape = "Round",
            itemType = "Wireless earbuds",
            brand = "Apple",
            description = "Left my AirPods Pro in a clear silver protective case near the food court tables after lunch.",
            marks = "Small blue cat sticker on the back",
            campus = "Kemanggisan",
            location = "Canteen Building B",
            locationNote = "Near coffee stall table #4",
            postedTime = "5h ago",
            dateTimeLabel = "Today, 11:30 WIB",
            posterName = "Nadia Sari",
            posterInfo = "BINUS Kemanggisan · Information Systems",
            posterVerified = true,
            isMine = false,
            isSaved = false,
            views = 18,
            inquiries = 2,
            createdAtMillis = hoursAgo(5),
            placeholderIcon = R.drawable.ic_image
        ),
        ReportItem(
            id = "item_3",
            code = "#L14",
            title = "Blue Stainless Water Bottle",
            status = ItemStatus.LOST,
            category = "Accessories",
            colors = listOf("Blue"),
            shape = "Cylinder",
            itemType = "Tumbler 600ml",
            brand = "Corkcicle",
            description = "Matte navy blue insulated bottle left in Room 502 after Mobile Programming lecture.",
            marks = "BINUS Tech Club sticker near the base",
            campus = "Kemanggisan",
            location = "Anggrek Campus, Room 502",
            locationNote = "Left under front row desk",
            postedTime = "1d ago",
            dateTimeLabel = "Yesterday, 16:40 WIB",
            posterName = "Rina Putri",
            posterInfo = "BINUS Kemanggisan · Computer Science",
            posterVerified = true,
            isMine = true,
            isSaved = false,
            views = 12,
            inquiries = 3,
            createdAtMillis = daysAgo(1),
            placeholderIcon = R.drawable.ic_image
        ),
        ReportItem(
            id = "item_4",
            code = "#F22",
            title = "BINUS Flazz Student Card",
            status = ItemStatus.FOUND,
            category = "Documents & IDs",
            colors = listOf("Blue", "White"),
            shape = "Flat / Thin",
            itemType = "Student ID Card",
            brand = "BCA Flazz",
            description = "Picked up a BINUS Flazz card near the elevator lobby on the 2nd floor. Safe with me.",
            marks = "Clear plastic card holder with navy lanyard",
            campus = "Alam Sutera",
            location = "Main Lobby, 2nd Floor Elevator",
            locationNote = "Handed near elevator B",
            postedTime = "1d ago",
            dateTimeLabel = "Yesterday, 13:10 WIB",
            posterName = "Kevin Pratama",
            posterInfo = "BINUS Alam Sutera · Visual Comm Design",
            posterVerified = true,
            isMine = false,
            isSaved = true,
            views = 31,
            inquiries = 1,
            createdAtMillis = daysAgo(1) - TimeUnit.HOURS.toMillis(3),
            placeholderIcon = R.drawable.ic_image
        ),
        ReportItem(
            id = "item_5",
            code = "#R07",
            title = "Casio Scientific Calculator",
            status = ItemStatus.RETURNED,
            category = "Books & Stationery",
            colors = listOf("Black", "Gray / Silver"),
            shape = "Rectangle",
            itemType = "Calculator fx-991EX",
            brand = "Casio",
            description = "Found in Lab 704 after Calculus quiz. Successfully verified and returned to the owner!",
            marks = "Initials 'A.R.' written on slide cover",
            campus = "Kemanggisan",
            location = "Syahdan Campus, Lab 704",
            locationNote = "Returned at Student Center",
            postedTime = "3d ago",
            dateTimeLabel = "3 days ago, 10:00 WIB",
            posterName = "Rina Putri",
            posterInfo = "BINUS Kemanggisan · Computer Science",
            posterVerified = true,
            isMine = true,
            isSaved = false,
            views = 45,
            inquiries = 4,
            createdAtMillis = daysAgo(3),
            placeholderIcon = R.drawable.ic_image
        ),
        ReportItem(
            id = "item_6",
            code = "#F30",
            title = "USB-C Laptop Charger 65W",
            status = ItemStatus.FOUND,
            category = "Electronics",
            colors = listOf("Black"),
            shape = "Square",
            itemType = "Power Adapter",
            brand = "Lenovo",
            description = "Left plugged into the wall socket at the coworking corner on 4th floor.",
            marks = "Velcro cable tie wrapped around cord",
            campus = "Bekasi",
            location = "Coworking Corner, 4th Floor",
            locationNote = "Kept at security desk",
            postedTime = "6h ago",
            dateTimeLabel = "Today, 09:45 WIB",
            posterName = "Rina Putri",
            posterInfo = "BINUS Kemanggisan · Computer Science",
            posterVerified = true,
            isMine = true,
            isSaved = false,
            views = 15,
            inquiries = 2,
            createdAtMillis = hoursAgo(6),
            placeholderIcon = R.drawable.ic_image
        )
    )

    val chatRooms: MutableList<ChatRoom> = mutableListOf(
        ChatRoom(
            id = "chat_1",
            peerName = "Dimas Arya",
            peerVerified = true,
            isOnline = true,
            itemId = "item_1",
            itemTitle = "Black Leather Wallet",
            itemStatus = ItemStatus.FOUND,
            itemLocation = "Library 3rd Floor",
            lastMessage = "That's mine! Can we meet at the library lobby?",
            lastTime = "14:22",
            unreadCount = 1,
            isArchived = false,
            messages = mutableListOf(
                ChatMessage.System("m1", "Item status updated to Found · Today 14:15"),
                ChatMessage.Text("m2", "Hi! I think I found your black leather wallet on the 3rd floor library.", "14:16", false),
                ChatMessage.Image("m3", "IMG_20261121.jpg", "Found photo confirmation", "14:19", false),
                ChatMessage.Text("m4", "That's mine! Can we meet at the library lobby?", "14:22", true)
            )
        ),
        ChatRoom(
            id = "chat_2",
            peerName = "Nadia Sari",
            peerVerified = true,
            isOnline = false,
            itemId = "item_2",
            itemTitle = "Silver AirPods Pro Case",
            itemStatus = ItemStatus.LOST,
            itemLocation = "Canteen Building B",
            lastMessage = "Let me know if you see a blue cat sticker on it.",
            lastTime = "12:05",
            unreadCount = 1,
            isArchived = false,
            messages = mutableListOf(
                ChatMessage.Text("m5", "Hi Nadia, I think someone handed a silver AirPods case to the canteen cashier.", "11:50", true),
                ChatMessage.Text("m6", "Let me know if you see a blue cat sticker on it.", "12:05", false)
            )
        ),
        ChatRoom(
            id = "chat_3",
            peerName = "Kevin Pratama",
            peerVerified = true,
            isOnline = true,
            itemId = "item_4",
            itemTitle = "BINUS Flazz Student Card",
            itemStatus = ItemStatus.FOUND,
            itemLocation = "Main Lobby, 2nd Floor",
            lastMessage = "Sure, I'll be near the admissions desk at 3 PM.",
            lastTime = "Yesterday",
            unreadCount = 0,
            isArchived = false,
            messages = mutableListOf(
                ChatMessage.Text("m7", "Hi Kevin, I saw your post about the Flazz card.", "Yesterday", true),
                ChatMessage.Text("m8", "Sure, I'll be near the admissions desk at 3 PM.", "Yesterday", false)
            )
        ),
        ChatRoom(
            id = "chat_4",
            peerName = "Arief Rahman",
            peerVerified = true,
            isOnline = false,
            itemId = "item_5",
            itemTitle = "Casio Scientific Calculator",
            itemStatus = ItemStatus.RETURNED,
            itemLocation = "Syahdan Campus, Lab 704",
            lastMessage = "Thank you so much for returning it!",
            lastTime = "3d ago",
            unreadCount = 0,
            isArchived = true,
            messages = mutableListOf(
                ChatMessage.System("m9", "Item marked as Returned · 3 days ago"),
                ChatMessage.Text("m10", "Thank you so much for returning it!", "3d ago", false)
            )
        )
    )

    val notifications: MutableList<NotificationItem> = mutableListOf(
        NotificationItem(
            id = "n1",
            type = NotifType.MATCH,
            title = "Possible match found",
            body = "A Found item near Library 3rd Floor matches your saved Wallet & Cards alert.",
            time = "2m ago",
            isUnread = true,
            relatedItemId = "item_1"
        ),
        NotificationItem(
            id = "n2",
            type = NotifType.MESSAGE,
            title = "New message from Dimas Arya",
            body = "That's mine! Can we meet at the library lobby?",
            time = "15m ago",
            isUnread = true,
            relatedChatId = "chat_1"
        ),
        NotificationItem(
            id = "n3",
            type = NotifType.STATUS,
            title = "Item marked as Returned",
            body = "Your post 'Casio Scientific Calculator' has been updated to Returned.",
            time = "3d ago",
            isUnread = false,
            relatedItemId = "item_5"
        ),
        NotificationItem(
            id = "n4",
            type = NotifType.MATCH,
            title = "New Found item at Kemanggisan",
            body = "USB-C Laptop Charger 65W was posted near your campus.",
            time = "6h ago",
            isUnread = true,
            relatedItemId = "item_6"
        )
    )

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadUserPrefs(context: Context) {
        val p = prefs(context)
        currentUser.name = p.getString(KEY_USER_NAME, currentUser.name) ?: currentUser.name
        currentUser.campus = p.getString(KEY_USER_CAMPUS, currentUser.campus) ?: currentUser.campus
    }

    fun saveUserPrefs(context: Context) {
        prefs(context).edit()
            .putString(KEY_USER_NAME, currentUser.name)
            .putString(KEY_USER_CAMPUS, currentUser.campus)
            .apply()
    }

    fun isOnboardingDone(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingDone(context: Context, done: Boolean) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    }

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        prefs(context).edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply()
    }

    fun getBoolPref(context: Context, key: String, default: Boolean = true): Boolean =
        prefs(context).getBoolean(key, default)

    fun setBoolPref(context: Context, key: String, value: Boolean) {
        prefs(context).edit().putBoolean(key, value).apply()
    }

    // --- Basic Auth Logic ---

    fun validateLogin(identifier: String, password: String): Boolean {
        val id = identifier.trim()
        val isNim = id.all { it.isDigit() } && id.length in 8..12
        val isBinusEmail = id.endsWith("@binus.ac.id", ignoreCase = true) ||
            id.endsWith("@binus.edu", ignoreCase = true)
        return (isNim || isBinusEmail) && password.length >= 4
    }

    // --- Item Queries & Mutations ---

    fun getItemById(id: String?): ReportItem? = items.find { it.id == id }

    fun recordItemView(itemId: String) {
        getItemById(itemId)?.let { it.views += 1 }
    }

    fun searchAndFilterItems(
        query: String,
        filter: FilterState,
        userCampus: String
    ): List<ReportItem> {
        val now = System.currentTimeMillis()
        val maxAgeMillis: Long? = when (filter.dateRange) {
            "Today" -> TimeUnit.DAYS.toMillis(1)
            "7 days" -> TimeUnit.DAYS.toMillis(7)
            "30 days" -> TimeUnit.DAYS.toMillis(30)
            else -> null
        }

        val filtered = items.filter { item ->
            val matchesQuery = query.isEmpty() ||
                item.title.contains(query, ignoreCase = true) ||
                item.location.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true) ||
                item.campus.contains(query, ignoreCase = true) ||
                item.brand.contains(query, ignoreCase = true) ||
                item.colors.any { it.contains(query, ignoreCase = true) }

            val matchesStatus = filter.status == null || item.status == filter.status
            val matchesCampus = filter.campus.isNullOrBlank() ||
                item.campus.equals(filter.campus, ignoreCase = true)
            val matchesCategory = filter.category.isNullOrBlank() ||
                item.category.equals(filter.category, ignoreCase = true)
            val matchesColors = filter.colors.isEmpty() ||
                item.colors.any { c -> filter.colors.any { it.equals(c, ignoreCase = true) } }
            val matchesDate = maxAgeMillis == null || (now - item.createdAtMillis) <= maxAgeMillis

            matchesQuery && matchesStatus && matchesCampus && matchesCategory && matchesColors && matchesDate
        }

        return if (filter.sortBy == "Nearest to me") {
            filtered.sortedWith(
                compareByDescending<ReportItem> { it.campus.equals(userCampus, ignoreCase = true) }
                    .thenByDescending { it.createdAtMillis }
            )
        } else {
            filtered.sortedByDescending { it.createdAtMillis }
        }
    }

    fun addItem(item: ReportItem, matchAlerts: Boolean = true) {
        items.add(0, item)
        if (matchAlerts) {
            checkAndNotifyPossibleMatch(item)
        }
    }

    fun updateItem(updated: ReportItem) {
        val idx = items.indexOfFirst { it.id == updated.id }
        if (idx >= 0) {
            items[idx] = updated
            chatRooms.filter { it.itemId == updated.id }.forEach { room ->
                room.itemStatus = updated.status
            }
        }
    }

    fun updateItemStatus(itemId: String, newStatus: ItemStatus) {
        val item = getItemById(itemId) ?: return
        if (item.status == newStatus) return
        item.status = newStatus

        val statusLabel = when (newStatus) {
            ItemStatus.LOST -> "Lost"
            ItemStatus.FOUND -> "Found"
            ItemStatus.RETURNED -> "Returned"
        }

        chatRooms.filter { it.itemId == itemId }.forEach { room ->
            room.itemStatus = newStatus
            room.messages.add(
                ChatMessage.System(
                    id = "sys_${System.currentTimeMillis()}",
                    text = "Item status updated to $statusLabel · Today ${currentTimeString()}"
                )
            )
            room.lastTime = currentTimeString()
        }

        notifications.add(
            0,
            NotificationItem(
                id = "n_${UUID.randomUUID().toString().take(6)}",
                type = NotifType.STATUS,
                title = "Item marked as $statusLabel",
                body = "'${item.title}' has been updated to $statusLabel.",
                time = "Just now",
                isUnread = true,
                relatedItemId = item.id
            )
        )
    }

    fun deleteItem(itemId: String) {
        items.removeAll { it.id == itemId }
    }

    private fun checkAndNotifyPossibleMatch(newItem: ReportItem) {
        val targetStatus = when (newItem.status) {
            ItemStatus.LOST -> ItemStatus.FOUND
            ItemStatus.FOUND -> ItemStatus.LOST
            ItemStatus.RETURNED -> return
        }
        val match = items.firstOrNull { other ->
            other.id != newItem.id &&
                other.status == targetStatus &&
                other.category.equals(newItem.category, ignoreCase = true) &&
                (other.campus.equals(newItem.campus, ignoreCase = true) ||
                    other.colors.any { c -> newItem.colors.any { it.equals(c, ignoreCase = true) } })
        } ?: return

        notifications.add(
            0,
            NotificationItem(
                id = "n_${UUID.randomUUID().toString().take(6)}",
                type = NotifType.MATCH,
                title = "Possible match found",
                body = "'${match.title}' at ${match.campus} matches your '${newItem.title}' post.",
                time = "Just now",
                isUnread = true,
                relatedItemId = match.id
            )
        )
    }

    // --- Chat & Claim Logic ---

    fun getChatById(id: String?): ChatRoom? = chatRooms.find { it.id == id }

    fun getOrCreateChatForItem(item: ReportItem): ChatRoom {
        val existing = chatRooms.find { it.itemId == item.id }
        if (existing != null) {
            existing.isArchived = false
            return existing
        }
        val room = ChatRoom(
            id = "chat_${UUID.randomUUID().toString().take(6)}",
            peerName = item.posterName,
            peerVerified = item.posterVerified,
            isOnline = true,
            itemId = item.id,
            itemTitle = item.title,
            itemStatus = item.status,
            itemLocation = item.location.ifBlank { "${item.campus} Campus" },
            lastMessage = "Started a conversation about ${item.title}",
            lastTime = currentTimeString(),
            unreadCount = 0,
            isArchived = false,
            messages = mutableListOf(
                ChatMessage.System(
                    id = "sys_${System.currentTimeMillis()}",
                    text = "Conversation started for ${item.title}"
                )
            )
        )
        chatRooms.add(0, room)
        return room
    }

    fun submitClaim(item: ReportItem, proof: String, photoUri: String?): ChatRoom {
        item.inquiries += 1
        val room = getOrCreateChatForItem(item)
        val now = currentTimeString()
        room.messages.add(
            ChatMessage.Text(
                id = "claim_${System.currentTimeMillis()}",
                text = "Claim verification: $proof",
                time = now,
                isOutgoing = true
            )
        )
        if (photoUri != null) {
            room.messages.add(
                ChatMessage.Image(
                    id = "claim_img_${System.currentTimeMillis()}",
                    filename = "proof_photo.jpg",
                    caption = "Claim proof photo",
                    time = now,
                    isOutgoing = true,
                    imageUri = photoUri
                )
            )
        }
        room.lastMessage = "Claim verification: $proof"
        room.lastTime = now
        return room
    }

    fun sendChatMessage(
        room: ChatRoom,
        text: String?,
        photoUri: String? = null,
        photoName: String = "photo.jpg"
    ): Boolean {
        val cleanText = text?.trim().orEmpty()
        val now = currentTimeString()
        return when {
            photoUri != null -> {
                val caption = cleanText.ifBlank { "Photo attachment" }
                room.messages.add(
                    ChatMessage.Image(
                        id = "img_${System.currentTimeMillis()}",
                        filename = photoName,
                        caption = caption,
                        time = now,
                        isOutgoing = true,
                        imageUri = photoUri
                    )
                )
                room.lastMessage = "Photo: $caption"
                room.lastTime = now
                true
            }
            cleanText.isNotEmpty() -> {
                room.messages.add(
                    ChatMessage.Text(
                        id = "txt_${System.currentTimeMillis()}",
                        text = cleanText,
                        time = now,
                        isOutgoing = true
                    )
                )
                room.lastMessage = cleanText
                room.lastTime = now
                true
            }
            else -> false
        }
    }

    fun generateSimulatedReply(room: ChatRoom, userText: String): ChatMessage.Text {
        val lower = userText.lowercase(Locale.getDefault())
        val replyText = when {
            lower.contains("meet") || lower.contains("where") ->
                "We can meet near ${room.itemLocation} after class. Does that work?"
            lower.contains("thank") ->
                "You're welcome! Glad to help a fellow BINUSian."
            lower.contains("on my way") ->
                "Great, I'll wait by the main entrance!"
            else ->
                "Got it! Let me check and confirm with you shortly."
        }
        val now = currentTimeString()
        val msg = ChatMessage.Text(
            id = "reply_${System.currentTimeMillis()}",
            text = replyText,
            time = now,
            isOutgoing = false
        )
        room.messages.add(msg)
        room.lastMessage = replyText
        room.lastTime = now
        return msg
    }

    // --- Notifications & Helpers ---

    fun unreadNotificationCount(): Int = notifications.count { it.isUnread }

    fun markAllNotificationsRead() {
        notifications.forEach { it.isUnread = false }
    }

    fun totalReturnedThisSemester(): Int =
        127 + items.count { it.status == ItemStatus.RETURNED }

    fun addRecentSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        recentSearches.removeAll { it.equals(trimmed, ignoreCase = true) }
        recentSearches.add(0, trimmed)
        if (recentSearches.size > 8) {
            recentSearches.removeAt(recentSearches.lastIndex)
        }
    }

    fun currentTimeString(): String =
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
}
