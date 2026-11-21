package com.example.myapplication.ui

import android.content.Context
import android.view.LayoutInflater
import com.example.myapplication.R
import com.example.myapplication.data.FilterState
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip

object FilterBottomSheet {

    fun show(
        context: Context,
        current: FilterState,
        onApply: (FilterState) -> Unit
    ) {
        val dialog = BottomSheetDialog(context)
        val b = BottomSheetFilterBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(b.root)

        // Pre-populate state
        when (current.status) {
            ItemStatus.LOST -> b.chipFilterLost.isChecked = true
            ItemStatus.FOUND -> b.chipFilterFound.isChecked = true
            ItemStatus.RETURNED -> b.chipFilterReturned.isChecked = true
            null -> b.chipFilterAll.isChecked = true
        }

        b.cgFilterCampus.clearCheck()
        when (current.campus) {
            context.getString(R.string.campus_kemanggisan) -> b.chipCampusKemanggisan.isChecked = true
            context.getString(R.string.campus_alam_sutera) -> b.chipCampusAlamSutera.isChecked = true
            context.getString(R.string.campus_bekasi) -> b.chipCampusBekasi.isChecked = true
        }

        b.cgFilterCategory.clearCheck()
        for (i in 0 until b.cgFilterCategory.childCount) {
            val chip = b.cgFilterCategory.getChildAt(i) as? Chip ?: continue
            if (chip.text.toString().equals(current.category, ignoreCase = true)) {
                chip.isChecked = true
            }
        }

        for (i in 0 until b.cgFilterColor.childCount) {
            val chip = b.cgFilterColor.getChildAt(i) as? Chip ?: continue
            chip.isChecked = current.colors.any { it.equals(chip.text.toString(), ignoreCase = true) }
        }

        when (current.dateRange) {
            context.getString(R.string.filter_date_today) -> b.chipDateToday.isChecked = true
            context.getString(R.string.filter_date_7d) -> b.chipDate7d.isChecked = true
            context.getString(R.string.filter_date_30d) -> b.chipDate30d.isChecked = true
            else -> b.chipDateAny.isChecked = true
        }

        when (current.sortBy) {
            context.getString(R.string.filter_sort_nearest) -> b.chipSortNearest.isChecked = true
            else -> b.chipSortNewest.isChecked = true
        }

        b.btnFilterReset.setOnClickListener {
            b.chipFilterAll.isChecked = true
            b.cgFilterCampus.clearCheck()
            b.cgFilterCategory.clearCheck()
            b.cgFilterColor.clearCheck()
            b.chipDateAny.isChecked = true
            b.chipSortNewest.isChecked = true
        }

        b.btnFilterApply.setOnClickListener {
            val status = when (b.cgFilterStatus.checkedChipId) {
                R.id.chip_filter_lost -> ItemStatus.LOST
                R.id.chip_filter_found -> ItemStatus.FOUND
                R.id.chip_filter_returned -> ItemStatus.RETURNED
                else -> null
            }

            val campus = b.cgFilterCampus.checkedChipId.takeIf { it != -1 }?.let { id ->
                b.root.findViewById<Chip>(id)?.text?.toString()
            }

            val category = b.cgFilterCategory.checkedChipId.takeIf { it != -1 }?.let { id ->
                b.root.findViewById<Chip>(id)?.text?.toString()
            }

            val colors = mutableSetOf<String>()
            for (id in b.cgFilterColor.checkedChipIds) {
                b.root.findViewById<Chip>(id)?.text?.toString()?.let { colors.add(it) }
            }

            val dateRange = b.cgFilterDate.checkedChipId.takeIf { it != -1 }?.let { id ->
                b.root.findViewById<Chip>(id)?.text?.toString()
            } ?: context.getString(R.string.filter_date_any)

            val sortBy = b.cgFilterSort.checkedChipId.takeIf { it != -1 }?.let { id ->
                b.root.findViewById<Chip>(id)?.text?.toString()
            } ?: context.getString(R.string.filter_sort_newest)

            onApply(
                FilterState(
                    status = status,
                    campus = campus,
                    category = category,
                    colors = colors,
                    dateRange = dateRange,
                    sortBy = sortBy
                )
            )
            dialog.dismiss()
        }

        dialog.show()
    }
}
