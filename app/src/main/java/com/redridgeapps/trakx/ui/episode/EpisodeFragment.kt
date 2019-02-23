package com.redridgeapps.trakx.ui.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.FragmentEpisodeBinding
import com.redridgeapps.trakx.ui.common.dataBindingInflate
import javax.inject.Inject

class EpisodeFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private lateinit var binding: FragmentEpisodeBinding
    private val viewModel by viewModels<EpisodeViewModel> { viewModelFactory }
    private val args: EpisodeFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflater.dataBindingInflate(R.layout.fragment_episode, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTVEpisode(args.tvShow.id, args.seasonNumber, args.episodeNumber)
        setupLayout()
    }

    private fun setupLayout() {

        binding.collapsingToolbar.setExpandedTitleColor(
            ContextCompat.getColor(requireContext(), android.R.color.transparent)
        )

        // Set backdrop aspect ratio to 16:9
        val fullWidth = resources.displayMetrics.widthPixels
        val layoutParams = binding.tvShowBackdrop.layoutParams
        layoutParams.width = fullWidth
        layoutParams.height = (fullWidth * 0.56).toInt()

        viewModel.episodeDetailLiveData.observe(viewLifecycleOwner) {
            requireActivity().title = it.name
            binding.episodeDetail = it
        }
    }
}
