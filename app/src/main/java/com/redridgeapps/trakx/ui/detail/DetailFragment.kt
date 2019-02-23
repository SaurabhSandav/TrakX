package com.redridgeapps.trakx.ui.detail

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.FragmentDetailBinding
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.ui.common.dataBindingInflate
import com.redridgeapps.trakx.ui.widget.UpcomingEpisodeWidget
import javax.inject.Inject

class DetailFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private var isTracked = false
    private lateinit var tvShowDetailAdapter: TVShowDetailAdapter
    private lateinit var binding: FragmentDetailBinding
    private val viewModel by viewModels<DetailViewModel> { viewModelFactory }
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflater.dataBindingInflate(R.layout.fragment_detail, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTVShow(args.tvShow)
        setupLayout(args.tvShow)

        viewModel.isShowTrackedLiveData.observe(viewLifecycleOwner) {
            isTracked = it
            tvShowDetailAdapter.submitTracked(it)
        }
        viewModel.upcomingEpisodeUpdatedLiveData.observe(viewLifecycleOwner) { updateWidgets() }
    }

    private fun setupLayout(tvShow: TVShow) {

        binding.collapsingToolbar.title = tvShow.name
        binding.collapsingToolbar.setExpandedTitleColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        // Set backdrop aspect ratio to 16:9
        val fullWidth = resources.displayMetrics.widthPixels
        val layoutParams = binding.tvShowBackdrop.layoutParams
        layoutParams.width = fullWidth
        layoutParams.height = (fullWidth * 0.56).toInt()

        binding.tvShow = tvShow

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val trackShowListener = View.OnClickListener {
            isTracked = !isTracked
            viewModel.trackShow(isTracked)
        }

        tvShowDetailAdapter =
            TVShowDetailAdapter(args.tvShow, resources, trackShowListener, this::launchEpisodeListActivity)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = tvShowDetailAdapter
        }

        viewModel.tvShowDetailLiveData.observe(viewLifecycleOwner, tvShowDetailAdapter::submitTVShowDetail)
    }

    private fun launchEpisodeListActivity(seasonNumber: Int) {
        findNavController().navigate(DetailFragmentDirections.toEpisodeListFragment(args.tvShow, seasonNumber))
    }

    private fun updateWidgets() {

        val name = ComponentName(requireContext(), UpcomingEpisodeWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(requireContext())

        for (widgetId in appWidgetManager.getAppWidgetIds(name)) {
            UpcomingEpisodeWidget.updateAppWidget(requireContext(), appWidgetManager, widgetId)
        }
    }
}
