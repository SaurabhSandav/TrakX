package com.redridgeapps.trakx.ui.episodelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.ui.common.dataBindingInflate
import javax.inject.Inject

class EpisodeListFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private lateinit var binding: com.redridgeapps.trakx.databinding.FragmentEpisodeListBinding
    private val viewModel by viewModels<EpisodeListViewModel> { viewModelFactory }
    private val args: EpisodeListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflater.dataBindingInflate(R.layout.fragment_episode_list, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTVSeason(args.tvShow.id, args.seasonNumber)

        setupLayout()
    }

    private fun setupLayout() {
        requireActivity().title = "Season ${args.seasonNumber}"

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val episodeListAdapter = EpisodeListAdapter(this::launchEpisodeActivity)

        val linearLayoutManager = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(), linearLayoutManager.orientation)

        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = episodeListAdapter
            addItemDecoration(itemDecoration)
        }

        viewModel.seasonDetailLiveData.observe(viewLifecycleOwner) { episodeListAdapter.submitList(it.episodes) }
    }

    private fun launchEpisodeActivity(episodeNumber: Int) {
        findNavController().navigate(
            EpisodeListFragmentDirections.toEpisodeFragment(args.tvShow, args.seasonNumber, episodeNumber)
        )
    }
}
