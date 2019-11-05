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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {

        val binding = ItemSeasonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val viewHolder = SeasonViewHolder(binding)

        binding.root.setOnClickListener {
            if (viewHolder.adapterPosition != RecyclerView.NO_POSITION)
                seasonClickListener(seasonList[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
        holder.bind(seasonList[position])
    }

    override fun getItemCount() = seasonList.size

    class SeasonViewHolder(
        private val binding: ItemSeasonBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(season: Season) = with(binding) {

            tvShowSeason.text = season.name
            tvShowSeasonEpisodeCount.text = "${season.episodeCount} episodes"
        }
    }
}
