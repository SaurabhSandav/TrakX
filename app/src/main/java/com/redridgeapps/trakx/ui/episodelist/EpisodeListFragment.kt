package com.redridgeapps.trakx.ui.episodelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.redridgeapps.trakx.databinding.FragmentEpisodeListBinding
import com.redridgeapps.trakx.ui.common.AutoClearedValue
import com.redridgeapps.trakx.ui.common.dagger.ViewModelFactoryGenerator
import com.redridgeapps.trakx.ui.common.dagger.savedStateViewModels
import com.redridgeapps.trakx.ui.common.navigateWith
import com.redridgeapps.trakx.ui.common.navigation.setupToolbarWithNavigation
import javax.inject.Inject

class EpisodeListFragment @Inject constructor(
    vmfg: ViewModelFactoryGenerator
) : Fragment() {

    private var binding by AutoClearedValue<FragmentEpisodeListBinding>()
    private val args: EpisodeListFragmentArgs by navArgs()

    private val viewModel by savedStateViewModels<EpisodeListViewModel>(vmfg) {
        EpisodeListViewModelArgs(
            tvShowId = args.tvShow.id,
            seasonNumber = args.seasonNumber
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEpisodeListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout()
    }

    private fun setupLayout() {

        setupToolbarWithNavigation(binding.toolbar)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val navController = findNavController()

        val episodeListAdapter = EpisodeListAdapter { episode ->
            EpisodeListFragmentDirections
                .toEpisodeFragment(
                    args.tvShow,
                    args.seasonNumber,
                    episode.episodeNumber,
                    episode.name
                )
                .navigateWith(navController)
        }

        val linearLayoutManager = LinearLayoutManager(requireContext())
        val itemDecoration =
            DividerItemDecoration(requireContext(), linearLayoutManager.orientation)

        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = episodeListAdapter
            addItemDecoration(itemDecoration)
        }

        viewModel.seasonDetailLiveData.observe(viewLifecycleOwner) {
            episodeListAdapter.submitList(it.episodes)
        }
    }
}
