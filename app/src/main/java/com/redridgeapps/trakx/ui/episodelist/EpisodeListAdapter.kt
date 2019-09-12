package com.redridgeapps.trakx.ui.episodelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.redridgeapps.trakx.databinding.ItemEpisodeBinding
import com.redridgeapps.trakx.model.tmdb.Episode

class EpisodeListAdapter(
    private val episodeClickListener: (Episode) -> Unit
) : ListAdapter<Episode, EpisodeListAdapter.EpisodeItemViewHolder>(Episode.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeItemViewHolder {

        val binding = ItemEpisodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val viewHolder = EpisodeItemViewHolder(binding)

        binding.root.setOnClickListener {
            if (viewHolder.adapterPosition != RecyclerView.NO_POSITION)
                episodeClickListener(getItem(viewHolder.adapterPosition))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: EpisodeItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EpisodeItemViewHolder(
        private val binding: ItemEpisodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(episode: Episode) {
            binding.tvShowEpisode.text = "${episode.episodeNumber}. ${episode.name}"
        }
    }
}
