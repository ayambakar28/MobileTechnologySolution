package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object AppRepository {

    private const val PREFS_NAME = "campus_find_prefs"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_CAMPUS = "user_campus"

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

    fun getItemById(id: String?): ReportItem? = items.find { it.id == id }

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

    fun addItem(item: ReportItem) {
        items.add(0, item)
    }

    fun deleteItem(itemId: String) {
        items.removeAll { it.id == itemId }
    }

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
