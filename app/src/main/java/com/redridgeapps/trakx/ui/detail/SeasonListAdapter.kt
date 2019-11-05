package com.redridgeapps.trakx.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.redridgeapps.trakx.databinding.ItemSeasonBinding
import com.redridgeapps.trakx.model.tmdb.Season

class SeasonListAdapter(
    private val seasonList: List<Season>,
    private val seasonClickListener: (Season) -> Unit
) : ListAdapter<Season, SeasonListAdapter.SeasonViewHolder>(Season.DiffCallback) {

    init {
        submitList(seasonList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {

        val binding = ItemSeasonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SeasonViewHolder(binding, seasonClickListener, this::getItem)
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        holder.bind(seasonList[position])
    }

    class SeasonViewHolder(
        private val binding: ItemSeasonBinding,
        seasonClickListener: (Season) -> Unit,
        getItem: (Int) -> Season
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    seasonClickListener(getItem(adapterPosition))
            }
        }

        fun bind(season: Season) = with(binding) {

            tvShowSeason.text = season.name
            tvShowSeasonEpisodeCount.text = "${season.episodeCount} episodes"
        }
    }
}
