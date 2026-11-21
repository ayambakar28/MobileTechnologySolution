package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.data.AppRepository
import com.example.myapplication.data.ItemStatus
import com.example.myapplication.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val pickAvatarLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                AppRepository.currentUser.avatarUri = uri.toString()
                renderProfile()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsSettingsActivity::class.java))
        }
        binding.rowNotifSettings.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsSettingsActivity::class.java))
        }
        binding.rowMyPosts.setOnClickListener {
            startActivity(Intent(requireContext(), MyPostsActivity::class.java))
        }
        binding.rowSaved.setOnClickListener {
            val intent = Intent(requireContext(), MyPostsActivity::class.java).apply {
                putExtra(MyPostsActivity.EXTRA_SAVED_MODE, true)
            }
            startActivity(intent)
        }
        binding.btnChangePhoto.setOnClickListener {
            pickAvatarLauncher.launch("image/*")
        }
        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
        binding.rowHelp.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.profile_help)
                .setMessage("Need assistance with a lost or found item? Visit the BINUS Student Services Center (SSC) or email campusfind@binus.ac.id.")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.profile_logout)
                .setMessage(R.string.profile_logout_confirm)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.profile_logout) { _, _ ->
                    AppRepository.setLoggedIn(requireContext(), false)
                    val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                .show()
        }

        renderProfile()
    }

    override fun onResume() {
        super.onResume()
        renderProfile()
    }

    private fun renderProfile() {
        val user = AppRepository.currentUser
        binding.tvName.text = user.name
        binding.tvNim.text = "NIM ${user.nim} · ${user.major}"
        binding.tvCampus.text = "${user.campus} Campus"
        binding.tvVerified.visibility = if (user.isVerified) View.VISIBLE else View.GONE

        if (!user.avatarUri.isNullOrBlank()) {
            try {
                binding.ivAvatar.setImageURI(Uri.parse(user.avatarUri))
                binding.ivAvatar.imageTintList = null
                binding.ivAvatar.setContentPadding(0, 0, 0, 0)
            } catch (_: Exception) {
            }
        }

        val myPosts = AppRepository.items.filter { it.isMine }
        val savedItems = AppRepository.items.filter { it.isSaved }

        binding.tvStatPosted.text = myPosts.size.toString()
        binding.tvStatFound.text = myPosts.count { it.status == ItemStatus.FOUND }.toString()
        binding.tvStatReturned.text = myPosts.count { it.status == ItemStatus.RETURNED }.toString()

        binding.tvMyPostsCount.text = getString(R.string.profile_posts_count_format, myPosts.size)
        binding.tvSavedCount.text = getString(R.string.profile_saved_count_format, savedItems.size)
        binding.tvAppVersion.text = "${getString(R.string.app_name)} v1.0.0"
    }

    private fun showEditProfileDialog() {
        val ctx = requireContext()
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
        val etName = EditText(ctx).apply {
            hint = "Full Name"
            setText(AppRepository.currentUser.name)
        }
        val etCampus = EditText(ctx).apply {
            hint = "Campus (Kemanggisan / Alam Sutera / Bekasi)"
            setText(AppRepository.currentUser.campus)
        }
        container.addView(etName)
        container.addView(etCampus)

        MaterialAlertDialogBuilder(ctx)
            .setTitle(R.string.profile_edit)
            .setView(container)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val newName = etName.text.toString().trim()
                val newCampus = etCampus.text.toString().trim()
                if (newName.isNotEmpty()) AppRepository.currentUser.name = newName
                if (newCampus.isNotEmpty()) AppRepository.currentUser.campus = newCampus
                AppRepository.saveUserPrefs(ctx)
                renderProfile()
                Toast.makeText(ctx, "Profile updated", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
