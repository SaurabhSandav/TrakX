package com.redridgeapps.trakx.ui.detail

import android.animation.ObjectAnimator
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
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
import com.redridgeapps.trakx.ui.activity.ToolbarOperations
import com.redridgeapps.trakx.ui.common.dataBindingInflate
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

    override fun onStart() {
        super.onStart()

        (requireActivity() as ToolbarOperations).hideToolbar()

        val drawerArrowDrawable = DrawerArrowDrawable(binding.toolbar.context).apply { progress = 0f }

        ObjectAnimator
            .ofFloat(drawerArrowDrawable, "progress", drawerArrowDrawable.progress, 1f)
            .start()

        binding.toolbar.apply {
            setNavigationContentDescription(androidx.navigation.ui.R.string.nav_app_bar_navigate_up_description)
            navigationIcon = drawerArrowDrawable
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    override fun onStop() {
        super.onStop()

        (requireActivity() as ToolbarOperations).showToolbar()
    }

    private fun setupLayout(tvShow: TVShow) {

        val expandedTitleColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)

        binding.collapsingToolbar.title = tvShow.name
        binding.collapsingToolbar.setExpandedTitleColor(expandedTitleColor)

        // Set backdrop aspect ratio to 16:9
        val fullWidth = resources.displayMetrics.widthPixels
        val layoutParams = binding.tvShowBackdrop.layoutParams
        layoutParams.width = fullWidth
        layoutParams.height = (fullWidth * 0.56).toInt()

        binding.tvShow = tvShow

        // Calculate poster size to be relative to screen size with 3:2 aspect ratio.
        val presetItemWidth = resources.getDimension(R.dimen.default_tv_show_poster_width)
        val columns = Math.ceil((fullWidth / presetItemWidth).toDouble()).toInt()
        val itemWidth = fullWidth / columns

        binding.tvShowPoster.layoutParams.width = itemWidth
        binding.tvShowPoster.layoutParams.height = (itemWidth * 1.5).toInt()

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
            binding.recyclerView.adapter = SeasonListAdapter(it.seasons) { seasonNumber ->
                findNavController().navigate(DetailFragmentDirections.toEpisodeListFragment(args.tvShow, seasonNumber))
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
