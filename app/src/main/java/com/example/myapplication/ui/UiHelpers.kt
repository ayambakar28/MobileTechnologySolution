package com.example.myapplication.ui

import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.example.myapplication.R
import com.example.myapplication.data.ItemStatus

object UiHelpers {

    fun bindStatusBadge(tv: TextView, status: ItemStatus, prefix: String? = null) {
        val context = tv.context
        val label = when (status) {
            ItemStatus.LOST -> context.getString(R.string.status_lost)
            ItemStatus.FOUND -> context.getString(R.string.status_found)
            ItemStatus.RETURNED -> context.getString(R.string.status_returned)
        }
        tv.text = if (prefix != null) "$label: $prefix" else label

        val bgRes = when (status) {
            ItemStatus.LOST -> R.drawable.bg_badge_lost
            ItemStatus.FOUND -> R.drawable.bg_badge_found
            ItemStatus.RETURNED -> R.drawable.bg_badge_returned
        }
        val textColorRes = when (status) {
            ItemStatus.LOST -> R.color.cf_lost_text
            ItemStatus.FOUND -> R.color.cf_found_text
            ItemStatus.RETURNED -> R.color.cf_returned_text
        }
        tv.setBackgroundResource(bgRes)
        val color = ContextCompat.getColor(context, textColorRes)
        tv.setTextColor(color)
        TextViewCompat.setCompoundDrawableTintList(tv, ColorStateList.valueOf(color))
    }

    fun loadPhotoOrPlaceholder(imageView: ImageView, uriString: String?) {
        if (!uriString.isNullOrBlank()) {
            try {
                imageView.setImageURI(Uri.parse(uriString))
                imageView.imageTintList = null
                imageView.setPadding(0, 0, 0, 0)
                return
            } catch (_: Exception) {
                // Fallback to placeholder below
            }
        }
        imageView.setImageResource(R.drawable.ic_image)
        imageView.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(imageView.context, R.color.cf_navy_700)
        )
        val pad = dpToPx(imageView.context, 16)
        imageView.setPadding(pad, pad, pad, pad)
    }

    fun dpToPx(context: Context, dp: Int): Int =
        (dp * context.resources.displayMetrics.density).toInt()
}
