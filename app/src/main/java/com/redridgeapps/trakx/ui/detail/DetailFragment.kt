package com.redridgeapps.trakx.ui.detail

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.FragmentDetailBinding
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.ui.common.dataBindingInflate
import com.redridgeapps.trakx.ui.common.navigateFrom
import com.redridgeapps.trakx.ui.common.navigation.setupCollapsingToolbarWithNavigation
import com.redridgeapps.trakx.ui.widget.UpcomingEpisodeWidget
import javax.inject.Inject

class DetailFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment() {

    private var isTracked = false
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
            setTracked(it)
        }
        viewModel.upcomingEpisodeUpdatedLiveData.observe(viewLifecycleOwner) { updateWidgets() }
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

        viewModel.tvShowDetailLiveData.observe(viewLifecycleOwner) {
            binding.recyclerView.adapter = SeasonListAdapter(it.seasons) { season ->
                DetailFragmentDirections
                    .toEpisodeListFragment(args.tvShow, season.seasonNumber, season.name)
                    .navigateFrom(this)
            }
        }
    }

    private fun updateWidgets() {

        val name = ComponentName(requireContext(), UpcomingEpisodeWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(requireContext())

        for (widgetId in appWidgetManager.getAppWidgetIds(name)) {
            UpcomingEpisodeWidget.updateAppWidget(requireContext(), appWidgetManager, widgetId)
        }
    }

    private fun setTracked(isTracked: Boolean) {
        val trackText = if (isTracked) R.string.text_stop_tracking else R.string.text_track

        binding.btTrackShow.setText(trackText)
        binding.btTrackShow.visibility = View.VISIBLE
    }
}
