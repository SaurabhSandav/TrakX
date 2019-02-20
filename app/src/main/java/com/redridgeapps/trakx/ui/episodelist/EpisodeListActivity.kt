package com.redridgeapps.trakx.ui.episodelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ActivityEpisodeListBinding
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.ui.base.BaseActivity
import com.redridgeapps.trakx.ui.episode.EpisodeActivity

class EpisodeListActivity : BaseActivity<EpisodeListViewModel, ActivityEpisodeListBinding>() {

    private lateinit var tvShow: TVShow
    private var seasonNumber: Int = -1

    override val layoutResId = R.layout.activity_episode_list

    override val viewModelClass = EpisodeListViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvShow = intent.getParcelableExtra(EXTRA_TV_SHOW)
        seasonNumber = intent.getIntExtra(EXTRA_SEASON_NUMBER, -1)

        if (seasonNumber == -1) throw IllegalStateException()

        viewModel.setTVSeason(tvShow.id, seasonNumber)

        setupLayout()
    }

    private fun setupLayout() {
        title = "Season $seasonNumber"

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val episodeListAdapter = EpisodeListAdapter(this::launchEpisodeActivity)

        val linearLayoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this@EpisodeListActivity, linearLayoutManager.orientation)

        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = episodeListAdapter
            addItemDecoration(itemDecoration)
        }

        viewModel.seasonDetailLiveData.observe(this) { episodeListAdapter.submitList(it.episodes) }
    }

    private fun launchEpisodeActivity(episodeNumber: Int) {
        val intent = EpisodeActivity.createIntent(this@EpisodeListActivity, tvShow, seasonNumber, episodeNumber)
        startActivity(intent)
    }

    companion object {

        private const val EXTRA_TV_SHOW = "EXTRA_TV_SHOW"
        private const val EXTRA_SEASON_NUMBER = "EXTRA_SEASON_NUMBER"

        fun createIntent(context: Context, tvShow: TVShow, seasonNumber: Int): Intent {
            return Intent(context, EpisodeListActivity::class.java).apply {
                putExtra(EXTRA_TV_SHOW, tvShow)
                putExtra(EXTRA_SEASON_NUMBER, seasonNumber)
            }
        }
    }
}
