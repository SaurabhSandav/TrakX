package com.redridgeapps.trakx.ui.tvshowlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.FragmentTvShowListBinding
import com.redridgeapps.trakx.ui.common.AutoFitGridLayoutManager
import com.redridgeapps.trakx.ui.common.dataBindingInflate
import com.redridgeapps.trakx.ui.common.navigation.setupToolbarWithNavigation
import com.redridgeapps.trakx.utils.Constants
import com.redridgeapps.trakx.utils.Constants.RequestType
import javax.inject.Inject

class TVShowListFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment(),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var tvShowListAdapter: TVShowListAdapter
    private lateinit var binding: FragmentTvShowListBinding
    private val viewModel by viewModels<TVShowListViewModel> { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflater.dataBindingInflate(R.layout.fragment_tv_show_list, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setRequestType(Constants.DEFAULT_CATEGORY_MAIN)

        setupLayout()

        requireActivity().onBackPressedDispatcher.addCallback {
            val isDrawerOpen = binding.drawerLayout.isDrawerOpen(GravityCompat.START)
            if (isDrawerOpen) binding.drawerLayout.closeDrawer(GravityCompat.START)

            isDrawerOpen
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        binding.navView.menu.forEach { it.isChecked = false }

        item.isChecked = true
        requireActivity().title = item.title

        val requestType = when (item.itemId) {
            R.id.category_my_shows -> Constants.RequestType.TRACKED
            R.id.category_popular -> Constants.RequestType.POPULAR
            R.id.category_top_rated -> Constants.RequestType.TOP_RATED
            R.id.category_on_the_air -> Constants.RequestType.ON_THE_AIR
            R.id.category_airing_today -> Constants.RequestType.AIRING_TODAY
            else -> return false
        }

        requestTypeChanged(requestType)

        return true
    }

    private fun setupLayout() {

        setupToolbarWithNavigation(binding.toolbar, binding.drawerLayout)

        requireActivity().setTitle(R.string.drawer_sort_popular)

        binding.navView.setNavigationItemSelectedListener(this)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        tvShowListAdapter = TVShowListAdapter {
            findNavController().navigate(TVShowListFragmentDirections.toDetailFragment(it, it.name))
        }

        binding.recyclerView.apply {
            val maxColumnWidth = resources.getDimension(R.dimen.default_tv_show_poster_width)

            layoutManager = AutoFitGridLayoutManager(requireContext(), maxColumnWidth)
            setHasFixedSize(true)
            adapter = tvShowListAdapter
        }

        viewModel.tvShowPagedListLiveData.apply {
            removeObservers(viewLifecycleOwner)
            observe(viewLifecycleOwner, tvShowListAdapter::submitList)
        }
    }

    private fun requestTypeChanged(requestType: RequestType) {
        tvShowListAdapter.submitList(null)

        viewModel.apply {
            tvShowPagedListLiveData.removeObservers(viewLifecycleOwner)
            setRequestType(requestType)
            tvShowPagedListLiveData.observe(viewLifecycleOwner, tvShowListAdapter::submitList)
        }
    }
}
