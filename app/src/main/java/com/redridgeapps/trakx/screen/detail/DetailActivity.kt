package com.redridgeapps.trakx.screen.detail

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ActivityDetailBinding
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.screen.base.BaseActivity
import com.redridgeapps.trakx.screen.episodelist.EpisodeListActivity
import com.redridgeapps.trakx.screen.widget.UpcomingEpisodeWidget

class DetailActivity : BaseActivity<DetailViewModel, ActivityDetailBinding>() {

    private var isTracked = false
    private var firstLaunch = true
    private lateinit var tvShow: TVShow
    private lateinit var tvShowDetailAdapter: TVShowDetailAdapter

    override val layoutResId = R.layout.activity_detail

    override val viewModelClass = DetailViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvShow = intent.getParcelableExtra(EXTRA_TV_SHOW)

        viewModel.setTVShow(tvShow)
        setupLayout(tvShow)

        viewModel.isShowTrackedLiveData.observe(this) {
            isTracked = it
            tvShowDetailAdapter.submitTracked(it)
        }
        viewModel.upcomingEpisodeUpdatedLiveData.observe(this) { updateWidgets() }
    }

    private fun setupLayout(tvShow: TVShow) {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.collapsingToolbar.title = tvShow.name
        binding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))

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

        tvShowDetailAdapter = TVShowDetailAdapter(tvShow, resources, trackShowListener, this::launchEpisodeListActivity)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DetailActivity)
            setHasFixedSize(true)
            adapter = tvShowDetailAdapter
        }

        viewModel.tvShowDetailLiveData.observe(this, tvShowDetailAdapter::submitTVShowDetail)
    }

    private fun launchEpisodeListActivity(seasonNumber: Int) {
        val intent = EpisodeListActivity.createIntent(this@DetailActivity, tvShow, seasonNumber)
        startActivity(intent)
    }

    private fun updateWidgets() {
        if (firstLaunch) {
            firstLaunch = false
            return
        }

        val name = ComponentName(applicationContext, UpcomingEpisodeWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)

        for (widgetId in appWidgetManager.getAppWidgetIds(name)) {
            UpcomingEpisodeWidget.updateAppWidget(applicationContext, appWidgetManager, widgetId)
        }
    }

    companion object {

        private const val EXTRA_TV_SHOW = "EXTRA_TV_SHOW"

        fun createIntent(context: Context, tvShow: TVShow): Intent {
            return Intent(context, DetailActivity::class.java).apply {
                putExtra(EXTRA_TV_SHOW, tvShow)
            }
        }
    }
}
