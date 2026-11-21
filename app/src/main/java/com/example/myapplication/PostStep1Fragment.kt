package com.example.myapplication

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.databinding.FragmentPostStep1Binding
import com.google.android.material.chip.Chip

class PostStep1Fragment : Fragment() {

    private var _binding: FragmentPostStep1Binding? = null
    private val binding get() = _binding!!

    private val postActivity: PostItemActivity?
        get() = activity as? PostItemActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostStep1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardTypeLost.setOnClickListener { selectType(ItemStatus.LOST) }
        binding.cardTypeFound.setOnClickListener { selectType(ItemStatus.FOUND) }

        binding.etTitle.doAfterTextChanged {
            binding.tilTitle.error = null
        }
        binding.etDescription.doAfterTextChanged {
            binding.tilDescription.error = null
        }

        // Default or pre-filled selection (supports Edit mode)
        val draft = postActivity?.draft
        selectType(draft?.status ?: ItemStatus.FOUND)
        if (draft != null && draft.title.isNotEmpty()) {
            binding.etTitle.setText(draft.title)
            binding.etItemType.setText(draft.itemType)
            binding.etBrand.setText(draft.brand)
            binding.etDescription.setText(draft.description)
            binding.etMarks.setText(draft.marks)
            binding.etHiddenDetail.setText(draft.hiddenDetail)

            for (i in 0 until binding.cgCategory.childCount) {
                val chip = binding.cgCategory.getChildAt(i) as? Chip ?: continue
                if (chip.text.toString().equals(draft.category, ignoreCase = true)) {
                    chip.isChecked = true
                    break
                }
            }
            for (i in 0 until binding.cgColor.childCount) {
                val chip = binding.cgColor.getChildAt(i) as? Chip ?: continue
                chip.isChecked = draft.colors.any { it.equals(chip.text.toString(), ignoreCase = true) }
            }
            for (i in 0 until binding.cgShape.childCount) {
                val chip = binding.cgShape.getChildAt(i) as? Chip ?: continue
                if (chip.text.toString().equals(draft.shape, ignoreCase = true)) {
                    chip.isChecked = true
                    break
                }
            }
        }
    }

    private fun selectType(status: ItemStatus) {
        val isFound = status == ItemStatus.FOUND
        binding.cardTypeFound.isChecked = isFound
        binding.cardTypeLost.isChecked = !isFound
        postActivity?.draft?.status = status

        val ctx = requireContext()
        if (isFound) {
            binding.tvMotivation.setText(R.string.post_motivation_found)
            binding.tvMotivation.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.cf_banner_mint))
            val textColor = ContextCompat.getColor(ctx, R.color.cf_found_text)
            binding.tvMotivation.setTextColor(textColor)
            TextViewCompat.setCompoundDrawableTintList(binding.tvMotivation, ColorStateList.valueOf(textColor))
            binding.groupFoundOnly.visibility = View.VISIBLE
        } else {
            binding.tvMotivation.setText(R.string.post_motivation_lost)
            binding.tvMotivation.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, R.color.cf_lavender_100))
            val textColor = ContextCompat.getColor(ctx, R.color.cf_navy_900)
            binding.tvMotivation.setTextColor(textColor)
            TextViewCompat.setCompoundDrawableTintList(binding.tvMotivation, ColorStateList.valueOf(textColor))
            binding.groupFoundOnly.visibility = View.GONE
        }
    }

    fun validateAndSave(): Boolean {
        val draft = postActivity?.draft ?: return true
        val title = binding.etTitle.text?.toString()?.trim().orEmpty()
        if (title.isEmpty()) {
            binding.tilTitle.error = getString(R.string.post_error_title)
            binding.etTitle.requestFocus()
            return false
        }

        val catId = binding.cgCategory.checkedChipId
        val category = if (catId != View.NO_ID) {
            binding.root.findViewById<Chip>(catId)?.text?.toString().orEmpty()
        } else {
            ""
        }
        if (category.isEmpty()) {
            Toast.makeText(requireContext(), R.string.post_error_category, Toast.LENGTH_SHORT).show()
            return false
        }

        val description = binding.etDescription.text?.toString()?.trim().orEmpty()
        if (description.isEmpty()) {
            binding.tilDescription.error = "Please enter key details."
            binding.etDescription.requestFocus()
            return false
        }

        draft.title = title
        draft.category = category
        draft.colors.clear()
        for (id in binding.cgColor.checkedChipIds) {
            binding.root.findViewById<Chip>(id)?.text?.toString()?.let { draft.colors.add(it) }
        }
        draft.shape = binding.cgShape.checkedChipId.takeIf { it != View.NO_ID }?.let { id ->
            binding.root.findViewById<Chip>(id)?.text?.toString().orEmpty()
        }.orEmpty()
        draft.itemType = binding.etItemType.text?.toString()?.trim().orEmpty()
        draft.brand = binding.etBrand.text?.toString()?.trim().orEmpty()
        draft.description = description
        draft.marks = binding.etMarks.text?.toString()?.trim().orEmpty()
        draft.hiddenDetail = binding.etHiddenDetail.text?.toString()?.trim().orEmpty()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
