package com.redridgeapps.trakx.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ItemSeasonBinding
import com.redridgeapps.trakx.model.tmdb.Season

class SeasonListAdapter(
    private val seasonList: List<Season>,
    private val seasonClickListener: (Season) -> Unit
) : RecyclerView.Adapter<SeasonListAdapter.SeasonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {

        val binding = DataBindingUtil.inflate<ItemSeasonBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_season,
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

        fun bind(season: Season) {

            binding.seasonTitle = season.name
            binding.episodeCount = season.episodeCount.toString() + " episodes"
        }
    }
}
