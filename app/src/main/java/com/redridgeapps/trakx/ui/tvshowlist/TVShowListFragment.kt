package com.redridgeapps.trakx.ui.tvshowlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.FragmentTvShowListBinding
import com.redridgeapps.trakx.ui.activity.main.MainViewModel
import com.redridgeapps.trakx.ui.common.AutoFitGridLayoutManager
import com.redridgeapps.trakx.ui.common.dataBindingInflate
import com.redridgeapps.trakx.utils.Constants
import com.redridgeapps.trakx.utils.Constants.RequestType
import javax.inject.Inject

class TVShowListFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private lateinit var tvShowListAdapter: TVShowListAdapter
    private lateinit var binding: FragmentTvShowListBinding
    private val viewModel by viewModels<TVShowListViewModel> { viewModelFactory }
    private val activityViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflater.dataBindingInflate(R.layout.fragment_tv_show_list, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setRequestType(Constants.DEFAULT_CATEGORY_MAIN)

        setupLayout()

        activityViewModel.requestTypeLiveData.observe(viewLifecycleOwner, this::requestTypeChanged)
    }

    private fun setupLayout() {
        requireActivity().setTitle(R.string.drawer_sort_popular)

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
