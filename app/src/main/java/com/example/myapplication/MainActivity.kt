package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.data.AppRepository
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var openFilterOnSearchTab: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppRepository.loadUserPrefs(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_post_placeholder) {
                openPostItem()
                return@setOnItemSelectedListener false
            }
            if (navController.currentDestination?.id != item.itemId) {
                val options = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setPopUpTo(navController.graph.startDestinationId, inclusive = false, saveState = true)
                    .build()
                navController.navigate(item.itemId, null, options)
            }
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val menuItem = binding.bottomNav.menu.findItem(destination.id)
            if (menuItem != null) {
                menuItem.isChecked = true
            }
        }

        binding.fabPost.setOnClickListener { openPostItem() }
    }

    fun openPostItem() {
        startActivity(Intent(this, PostItemActivity::class.java))
    }

    fun openSearchTab(openFilter: Boolean = false) {
        openFilterOnSearchTab = openFilter
        binding.bottomNav.selectedItemId = R.id.nav_search
    }

    fun openHomeTab() {
        binding.bottomNav.selectedItemId = R.id.nav_home
    }

    fun openProfileTab() {
        binding.bottomNav.selectedItemId = R.id.nav_profile
    }
}
