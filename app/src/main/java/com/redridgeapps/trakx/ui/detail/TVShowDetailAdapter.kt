package com.redridgeapps.trakx.ui.detail

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ItemSeasonBinding
import com.redridgeapps.trakx.databinding.ItemSeasonsHeaderBinding
import com.redridgeapps.trakx.databinding.ItemTvShowDetailBinding
import com.redridgeapps.trakx.model.tmdb.Season
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.redridgeapps.trakx.model.tmdb.TVShowDetail
import java.util.*

class TVShowDetailAdapter(
    private val tvShow: TVShow,
    private val resources: Resources,
    private val trackShowListener: View.OnClickListener,
    private val seasonClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var tvShowDetail: TVShowDetail? = null
    private var seasonList: List<Season> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DETAIL -> DetailViewHolder.create(parent, resources, trackShowListener)
            VIEW_TYPE_SEASONS_HEADER -> SeasonHeaderViewHolder.create(parent)
            VIEW_TYPE_SEASON -> SeasonViewHolder.create(parent, seasonClickListener)
            else -> throw IllegalArgumentException("Unexpected viewType!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DetailViewHolder) {
            holder.bind(tvShow)
        } else if (holder is SeasonViewHolder) {
            holder.bind(tvShowDetail!!.seasons[position - 2])
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<*>) {

        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isEmpty()) return

        if (holder is DetailViewHolder)
            holder.setTracked(payloads[0] as Boolean)
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> VIEW_TYPE_DETAIL
            position == 1 -> VIEW_TYPE_SEASONS_HEADER
            position >= 2 -> VIEW_TYPE_SEASON
            else -> super.getItemViewType(position)
        }
    }

    override fun getItemCount() = if (seasonList.isEmpty()) 1 else 2 + seasonList.size

    fun submitTVShowDetail(newTVShowDetail: TVShowDetail) {
        tvShowDetail = newTVShowDetail
        seasonList = newTVShowDetail.seasons

        notifyItemRangeInserted(1, seasonList.size + 1)
    }

    fun submitTracked(isTracked: Boolean) {
        notifyItemChanged(0, isTracked)
    }

    private class DetailViewHolder(private val binding: ItemTvShowDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tvShow: TVShow) {
            binding.tvShow = tvShow
        }

        fun setTracked(isTracked: Boolean) {
            val trackText = if (isTracked) R.string.text_stop_tracking else R.string.text_track

            binding.btTrackShow.setText(trackText)
            binding.btTrackShow.visibility = View.VISIBLE
        }

        companion object {

            fun create(
                parent: ViewGroup,
                resources: Resources,
                trackShowListener: View.OnClickListener
            ): DetailViewHolder {

                val binding = DataBindingUtil.inflate<ItemTvShowDetailBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_tv_show_detail,
                    parent,
                    false
                )

                // Calculate poster size to be relative to screen size with 3:2 aspect ratio.
                val fullWidth = resources.displayMetrics.widthPixels
                val presetItemWidth = resources.getDimension(R.dimen.default_tv_show_poster_width)
                val columns = Math.ceil((fullWidth / presetItemWidth).toDouble()).toInt()
                val itemWidth = fullWidth / columns

                binding.tvShowPoster.layoutParams.width = itemWidth
                binding.tvShowPoster.layoutParams.height = (itemWidth * 1.5).toInt()

                binding.btTrackShow.setOnClickListener(trackShowListener)

                return DetailViewHolder(binding)
            }
        }
    }

    private class SeasonHeaderViewHolder(binding: ItemSeasonsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {

            fun create(parent: ViewGroup): SeasonHeaderViewHolder {

                val binding = DataBindingUtil.inflate<ItemSeasonsHeaderBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_seasons_header,
                    parent,
                    false
                )

                binding.seasonHeader = "Seasons"

                return SeasonHeaderViewHolder(binding)
            }
        }
    }

    private class SeasonViewHolder(
        private val binding: ItemSeasonBinding,
        seasonClickListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var season: Season? = null

        init {

            binding.root.setOnClickListener { seasonClickListener(season!!.seasonNumber) }
        }

        fun bind(newSeason: Season) {
            season = newSeason

            binding.seasonTitle = newSeason.name
            binding.episodeCount = newSeason.episodeCount.toString() + " episodes"
        }

        companion object {

            fun create(parent: ViewGroup, seasonClickListener: (Int) -> Unit): SeasonViewHolder {

                val binding = DataBindingUtil.inflate<ItemSeasonBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_season,
                    parent,
                    false
                )

                return SeasonViewHolder(binding, seasonClickListener)
            }
        }
    }
}

private const val VIEW_TYPE_DETAIL = 0
private const val VIEW_TYPE_SEASONS_HEADER = 1
private const val VIEW_TYPE_SEASON = 2
