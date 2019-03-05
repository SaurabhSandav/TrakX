package com.redridgeapps.trakx.ui.activity.main

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentFactory
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ActivityMainBinding
import com.redridgeapps.trakx.ui.activity.ToolbarOperations
import com.redridgeapps.trakx.utils.Constants.RequestType.AIRING_TODAY
import com.redridgeapps.trakx.utils.Constants.RequestType.ON_THE_AIR
import com.redridgeapps.trakx.utils.Constants.RequestType.POPULAR
import com.redridgeapps.trakx.utils.Constants.RequestType.TOP_RATED
import com.redridgeapps.trakx.utils.Constants.RequestType.TRACKED
import com.redridgeapps.trakx.work.UpcomingEpisodeSyncWorker
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    ToolbarOperations {

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        supportFragmentManager.fragmentFactory = fragmentFactory

        super.onCreate(savedInstanceState)

        // Schedule Sync
        UpcomingEpisodeSyncWorker.scheduleDaily()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        setTitle(R.string.drawer_sort_popular)
        setupNavigation()

        binding.navView.setNavigationItemSelectedListener(this@MainActivity)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }

    override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration)

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

    override fun hideToolbar() {
        binding.toolbar.visibility = View.GONE
    }

    override fun showToolbar() {
        binding.toolbar.visibility = View.VISIBLE
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Lock Drawer outside TVShowList screen
        navController.addOnDestinationChangedListener { _, destination, _ ->

            val lockMode =
                if (destination.id == R.id.TVShowListFragment) DrawerLayout.LOCK_MODE_UNLOCKED
                else DrawerLayout.LOCK_MODE_LOCKED_CLOSED

            binding.drawerLayout.setDrawerLockMode(lockMode)
        }
    }
}