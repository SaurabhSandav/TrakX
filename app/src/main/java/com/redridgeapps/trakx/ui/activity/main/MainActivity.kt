package com.redridgeapps.trakx.ui.activity.main

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.navigation.NavigationView
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ActivityMainBinding
import com.redridgeapps.trakx.utils.Constants.RequestType.AIRING_TODAY
import com.redridgeapps.trakx.utils.Constants.RequestType.ON_THE_AIR
import com.redridgeapps.trakx.utils.Constants.RequestType.POPULAR
import com.redridgeapps.trakx.utils.Constants.RequestType.TOP_RATED
import com.redridgeapps.trakx.utils.Constants.RequestType.TRACKED
import com.redridgeapps.trakx.work.UpcomingEpisodeSyncWorker
import dagger.android.AndroidInjection

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        // Schedule Sync
        UpcomingEpisodeSyncWorker.scheduleDaily()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        binding.navView.setNavigationItemSelectedListener(this@MainActivity)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        val size = binding.navView.menu.size()
        (0 until size).forEach { binding.navView.menu.getItem(it).isChecked = false }

        item.isChecked = true
        binding.toolbar.title = item.title

        val requestType = when (item.itemId) {
            R.id.category_my_shows -> TRACKED
            R.id.category_popular -> POPULAR
            R.id.category_top_rated -> TOP_RATED
            R.id.category_on_the_air -> ON_THE_AIR
            R.id.category_airing_today -> AIRING_TODAY
            else -> return false
        }

        viewModel.setRequestType(requestType)

        return true
    }
}