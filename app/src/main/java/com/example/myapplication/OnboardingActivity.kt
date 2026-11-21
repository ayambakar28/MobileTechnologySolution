package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.data.AppRepository
import com.example.myapplication.databinding.ActivityOnboardingBinding
import com.example.myapplication.databinding.ItemOnboardingPageBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    private data class OnboardingPage(
        val titleRes: Int,
        val descRes: Int,
        val illustrationRes: Int
    )

    private val pages = listOf(
        OnboardingPage(R.string.onboarding_1_title, R.string.onboarding_1_desc, R.drawable.ill_onboarding),
        OnboardingPage(R.string.onboarding_2_title, R.string.onboarding_2_desc, R.drawable.ill_onboarding),
        OnboardingPage(R.string.onboarding_3_title, R.string.onboarding_3_desc, R.drawable.ill_onboarding)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.vpOnboarding.adapter = OnboardingAdapter(pages)
        TabLayoutMediator(binding.tabDots, binding.vpOnboarding) { _, _ -> }.attach()

        updatePageUi(0)

        binding.vpOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updatePageUi(position)
            }
        })

        binding.btnSkip.setOnClickListener { completeOnboarding() }

        binding.btnNext.setOnClickListener {
            val current = binding.vpOnboarding.currentItem
            if (current < pages.lastIndex) {
                binding.vpOnboarding.currentItem = current + 1
            } else {
                completeOnboarding()
            }
        }
    }

    private fun updatePageUi(position: Int) {
        binding.tvStep.text = getString(R.string.onboarding_step_format, position + 1, pages.size)
        val isLast = position == pages.lastIndex
        binding.btnNext.text = getString(
            if (isLast) R.string.onboarding_get_started else R.string.onboarding_next
        )
    }

    private fun completeOnboarding() {
        AppRepository.setOnboardingDone(this, true)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private class OnboardingAdapter(
        private val items: List<OnboardingPage>
    ) : RecyclerView.Adapter<OnboardingAdapter.PageViewHolder>() {

        class PageViewHolder(val binding: ItemOnboardingPageBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            val binding = ItemOnboardingPageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return PageViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            val page = items[position]
            holder.binding.ivIllustration.setImageResource(page.illustrationRes)
            holder.binding.tvTitle.setText(page.titleRes)
            holder.binding.tvDescription.setText(page.descRes)
        }

        override fun getItemCount(): Int = items.size
    }
}
