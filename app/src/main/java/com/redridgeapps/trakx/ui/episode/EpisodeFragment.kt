package com.redridgeapps.trakx.ui.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.FragmentEpisodeBinding
import com.redridgeapps.trakx.ui.common.AutoClearedValue
import com.redridgeapps.trakx.ui.common.dagger.ViewModelFactoryGenerator
import com.redridgeapps.trakx.ui.common.dagger.savedStateViewModels
import com.redridgeapps.trakx.ui.common.dataBindingInflate
import com.redridgeapps.trakx.ui.common.navigation.setupCollapsingToolbarWithNavigation
import javax.inject.Inject

class EpisodeFragment @Inject constructor(
    vmfg: ViewModelFactoryGenerator
) : Fragment() {

    private var binding by AutoClearedValue<FragmentEpisodeBinding>()
    private val args: EpisodeFragmentArgs by navArgs()
    private val viewModel by savedStateViewModels<EpisodeViewModel>(vmfg)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.dataBindingInflate(R.layout.fragment_episode, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTVEpisode(args.tvShow.id, args.seasonNumber, args.episodeNumber)
        setupLayout()
    }

    private fun setupLayout() {

        setupCollapsingToolbarWithNavigation(binding.collapsingToolbar, binding.toolbar)

        viewModel.episodeDetailLiveData.observe(viewLifecycleOwner) {
            binding.episodeDetail = it
        }
    }
}
