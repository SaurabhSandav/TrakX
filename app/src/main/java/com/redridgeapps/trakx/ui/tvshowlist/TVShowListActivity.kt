package com.redridgeapps.trakx.ui.tvshowlist

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ActivityTvShowListBinding
import com.redridgeapps.trakx.ui.base.BaseActivity
import com.redridgeapps.trakx.ui.detail.DetailActivity
import com.redridgeapps.trakx.utils.Constants
import com.redridgeapps.trakx.utils.Constants.RequestType
import com.redridgeapps.trakx.work.UpcomingEpisodeSyncWorker

class TVShowListActivity : BaseActivity<TVShowListViewModel, ActivityTvShowListBinding>(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var tvShowListAdapter: TVShowListAdapter

    override val layoutResId = R.layout.activity_tv_show_list

    override val viewModelClass = TVShowListViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Schedule Sync
        UpcomingEpisodeSyncWorker.scheduleDaily()

        viewModel.setRequestType(Constants.DEFAULT_CATEGORY_MAIN)

        setupLayout()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else super.onBackPressed()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)

        val requestType = when (item.itemId) {
            R.id.category_my_shows -> RequestType.TRACKED
            R.id.category_popular -> RequestType.POPULAR
            R.id.category_top_rated -> RequestType.TOP_RATED
            R.id.category_on_the_air -> RequestType.ON_THE_AIR
            R.id.category_airing_today -> RequestType.AIRING_TODAY
            else -> return false
        }

        val size = binding.navView.menu.size()
        (0 until size).forEach { binding.navView.menu.getItem(it).isChecked = false }

        item.isChecked = true
        binding.toolbar.title = item.title

        tvShowListAdapter.submitList(null)
        viewModel.apply {
            tvShowPagedListLiveData.removeObservers(this@TVShowListActivity)
            setRequestType(requestType)
            tvShowPagedListLiveData.observe(this@TVShowListActivity, tvShowListAdapter::submitList)
        }


        return true
    }

    private fun setupLayout() {

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitle(R.string.drawer_sort_popular)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        binding.navView.setNavigationItemSelectedListener(this)

        toggle.syncState()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        // Calculate poster size to be relative to screen size
        val presetItemWidth = resources.getDimension(R.dimen.default_tv_show_poster_width)
        val fullWidth = resources.displayMetrics.widthPixels
        val columns = Math.ceil((fullWidth / presetItemWidth).toDouble()).toInt()
        val itemWidth = fullWidth / columns

        tvShowListAdapter = TVShowListAdapter(itemWidth) {
            startActivity(DetailActivity.createIntent(this@TVShowListActivity, it))
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@TVShowListActivity, columns)
            setHasFixedSize(true)
            adapter = tvShowListAdapter
        }

        viewModel.tvShowPagedListLiveData.apply {
            removeObservers(this@TVShowListActivity)
            observe(this@TVShowListActivity, tvShowListAdapter::submitList)
        }
    }
}
