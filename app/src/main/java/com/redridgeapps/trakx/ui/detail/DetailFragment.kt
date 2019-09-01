package com.redridgeapps.trakx.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.FragmentDetailBinding
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.ui.common.AutoClearedValue
import com.redridgeapps.trakx.ui.common.dagger.ViewModelFactoryGenerator
import com.redridgeapps.trakx.ui.common.dagger.savedStateViewModels
import com.redridgeapps.trakx.ui.common.dataBindingInflate
import com.redridgeapps.trakx.ui.common.navigateWith
import com.redridgeapps.trakx.ui.common.navigation.setupCollapsingToolbarWithNavigation
import javax.inject.Inject

class DetailFragment @Inject constructor(
    vmfg: ViewModelFactoryGenerator
) : Fragment() {

    private var isTracked = false
    private var binding by AutoClearedValue<FragmentDetailBinding>()
    private val args: DetailFragmentArgs by navArgs()

    private val viewModel by savedStateViewModels<DetailViewModel>(vmfg) {
        DetailViewModelArgs(
            tvShow = args.tvShow
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.dataBindingInflate(R.layout.fragment_detail, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout(args.tvShow)

        viewModel.isShowTrackedLiveData.observe(viewLifecycleOwner) {
            isTracked = it
            setTracked(it)
        }
    }

    private fun setupLayout(tvShow: TVShow) {

        setupCollapsingToolbarWithNavigation(binding.collapsingToolbar, binding.toolbar)

        binding.tvShow = tvShow

        binding.btTrackShow.setOnClickListener {
            isTracked = !isTracked
            viewModel.trackShow(isTracked)
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(false)
        }

        val navController = findNavController()

        viewModel.tvShowDetailLiveData.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter = SeasonListAdapter(it.seasons) { season ->
                DetailFragmentDirections
                    .toEpisodeListFragment(args.tvShow, season.seasonNumber, season.name)
                    .navigateWith(navController)
            }
        }
    }

    private fun setTracked(isTracked: Boolean) {
        val trackText = if (isTracked) R.string.text_stop_tracking else R.string.text_track

        binding.btTrackShow.setText(trackText)
        binding.btTrackShow.visibility = View.VISIBLE
    }
}
