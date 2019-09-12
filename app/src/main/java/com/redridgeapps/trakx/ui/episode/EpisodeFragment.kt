package com.redridgeapps.trakx.ui.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.databinding.FragmentEpisodeBinding
import com.redridgeapps.trakx.model.tmdb.EpisodeDetail
import com.redridgeapps.trakx.ui.common.AutoClearedValue
import com.redridgeapps.trakx.ui.common.dagger.ViewModelFactoryGenerator
import com.redridgeapps.trakx.ui.common.dagger.savedStateViewModels
import com.redridgeapps.trakx.ui.common.navigation.setupCollapsingToolbarWithNavigation
import com.squareup.picasso.Picasso
import javax.inject.Inject

class EpisodeFragment @Inject constructor(
    vmfg: ViewModelFactoryGenerator
) : Fragment() {

    private var binding by AutoClearedValue<FragmentEpisodeBinding>()
    private val args: EpisodeFragmentArgs by navArgs()

    private val viewModel by savedStateViewModels<EpisodeViewModel>(vmfg) {
        EpisodeViewModelArgs(
            tvShowId = args.tvShow.id,
            seasonNumber = args.seasonNumber,
            episodeNumber = args.episodeNumber
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEpisodeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout()
    }

    private fun setupLayout() {

        setupCollapsingToolbarWithNavigation(binding.collapsingToolbar, binding.toolbar)

        viewModel.episodeDetailLiveData.observe(viewLifecycleOwner) { bindData(it) }
    }

    private fun bindData(episodeDetail: EpisodeDetail) = with(binding) {

        episodeBackdrop.contentDescription = "${episodeDetail.name} Still"
        episodeDetail.stillPath?.let { stillPath ->
            val url = TMDbService.buildBackdropURL(stillPath)
            Picasso.get().load(url).fit().into(episodeBackdrop)
        }

        tvShowTitle.text = episodeDetail.name
        tvShowRating.rating = episodeDetail.voteAverage
        tvShowDescription.text = episodeDetail.overview
    }
}
