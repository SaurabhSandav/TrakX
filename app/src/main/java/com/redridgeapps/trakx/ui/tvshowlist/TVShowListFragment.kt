package com.redridgeapps.trakx.ui.tvshowlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.FragmentTvShowListBinding
import com.redridgeapps.trakx.ui.common.AutoClearedValue
import com.redridgeapps.trakx.ui.common.AutoFitGridLayoutManager
import com.redridgeapps.trakx.ui.common.dagger.ViewModelFactoryGenerator
import com.redridgeapps.trakx.ui.common.dagger.savedStateViewModels
import com.redridgeapps.trakx.ui.common.navigateWith
import com.redridgeapps.trakx.ui.common.navigation.setupToolbarWithNavigation
import com.redridgeapps.trakx.utils.Constants
import com.redridgeapps.trakx.utils.Constants.RequestType
import javax.inject.Inject

class TVShowListFragment @Inject constructor(
    vmfg: ViewModelFactoryGenerator
) : Fragment(),
    NavigationView.OnNavigationItemSelectedListener {

    private var tvShowListAdapter by AutoClearedValue<TVShowListAdapter>()
    private var binding by AutoClearedValue<FragmentTvShowListBinding>()
    private val viewModel by savedStateViewModels<TVShowListViewModel>(vmfg)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTvShowListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout()

        viewModel.tvShowPagedListLiveData.observe(viewLifecycleOwner, tvShowListAdapter::submitList)
        requestTypeChanged(Constants.DEFAULT_CATEGORY_MAIN)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        binding.navView.menu.forEach { it.isChecked = false }

        item.isChecked = true
        requireActivity().title = item.title

        val requestType = when (item.itemId) {
            R.id.category_my_shows -> RequestType.TRACKED
            R.id.category_popular -> RequestType.POPULAR
            R.id.category_top_rated -> RequestType.TOP_RATED
            R.id.category_on_the_air -> RequestType.ON_THE_AIR
            R.id.category_airing_today -> RequestType.AIRING_TODAY
            else -> return false
        }

        requestTypeChanged(requestType)

        return true
    }

    private fun setupLayout() = with(binding) {

        setupToolbarWithNavigation(toolbar, drawerLayout)

        requireActivity().setTitle(R.string.drawer_sort_popular)

        navView.setNavigationItemSelectedListener(this@TVShowListFragment)

        setupRecyclerView()
        setupDrawer()
    }

    private fun FragmentTvShowListBinding.setupRecyclerView() {

        val navController = findNavController()

        tvShowListAdapter = TVShowListAdapter { tvShow ->
            TVShowListFragmentDirections.toDetailFragment(tvShow, tvShow.name)
                .navigateWith(navController)
        }

        recyclerView.apply {
            val maxColumnWidth = resources.getDimension(R.dimen.default_tv_show_poster_width)

            layoutManager = AutoFitGridLayoutManager(requireContext(), maxColumnWidth)
            setHasFixedSize(true)
            adapter = tvShowListAdapter
        }
    }

    private fun FragmentTvShowListBinding.setupDrawer() {
        val callback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                drawerLayout.closeDrawer(GravityCompat.START)
                isEnabled = false
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                callback.isEnabled = true
            }
        })
    }

    private fun requestTypeChanged(requestType: RequestType) {
        tvShowListAdapter.submitList(null)
        viewModel.setRequestType(requestType)
    }
}
