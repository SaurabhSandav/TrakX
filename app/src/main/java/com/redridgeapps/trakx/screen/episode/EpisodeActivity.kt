package com.redridgeapps.trakx.screen.episode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ActivityEpisodeBinding
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.screen.base.BaseActivity

class EpisodeActivity : BaseActivity<EpisodeViewModel, ActivityEpisodeBinding>() {

    override val layoutResId = R.layout.activity_episode

    override val viewModelClass = EpisodeViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tvShow = intent.getParcelableExtra<TVShow>(EXTRA_TV_SHOW)
        val seasonNumber = intent.getIntExtra(EXTRA_SEASON_NUMBER, -1)
        val episodeNumber = intent.getIntExtra(EXTRA_EPISODE_NUMBER, -1)

        if (seasonNumber == -1 || episodeNumber == -1) throw IllegalStateException()

        viewModel.setTVEpisode(tvShow.id, seasonNumber, episodeNumber)
        setupLayout()
    }

    private fun setupLayout() {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))

        // Set backdrop aspect ratio to 16:9
        val fullWidth = resources.displayMetrics.widthPixels
        val layoutParams = binding.tvShowBackdrop.layoutParams
        layoutParams.width = fullWidth
        layoutParams.height = (fullWidth * 0.56).toInt()

        viewModel.episodeDetailLiveData.observe(this) {
            title = it.name
            binding.episodeDetail = it
        }
    }

    companion object {

        private const val EXTRA_TV_SHOW = "EXTRA_TV_SHOW"
        private const val EXTRA_SEASON_NUMBER = "EXTRA_SEASON_NUMBER"
        private const val EXTRA_EPISODE_NUMBER = "EXTRA_EPISODE_NUMBER"

        fun createIntent(context: Context, tvShow: TVShow, seasonNumber: Int, episodeNumber: Int): Intent {
            return Intent(context, EpisodeActivity::class.java).apply {
                putExtra(EXTRA_TV_SHOW, tvShow)
                putExtra(EXTRA_SEASON_NUMBER, seasonNumber)
                putExtra(EXTRA_EPISODE_NUMBER, episodeNumber)
            }
        }
    }
}
