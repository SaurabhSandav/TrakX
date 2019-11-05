package com.redridgeapps.trakx.ui.tvshowlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.redridgeapps.trakx.api.TMDbService
import com.redridgeapps.trakx.databinding.ItemTvShowListBinding
import com.redridgeapps.trakx.model.tmdb.TVShow
import com.squareup.picasso.Picasso

class TVShowListAdapter(
    private val tvShowClickListener: (TVShow) -> Unit
) : PagedListAdapter<TVShow, TVShowListAdapter.TVShowListViewHolder>(TVShow.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVShowListViewHolder {

        val binding = ItemTvShowListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TVShowListViewHolder(binding, tvShowClickListener, this::getItem)
    }

    override fun onBindViewHolder(holder: TVShowListViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    class TVShowListViewHolder(
        private val binding: ItemTvShowListBinding,
        tvShowClickListener: (TVShow) -> Unit,
        getItem: (Int) -> TVShow?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                getItem(adapterPosition)?.let { localTVShow -> tvShowClickListener(localTVShow) }
            }
        }

        fun bindTo(tvShow: TVShow?): Unit = with(binding) {
            root.isClickable = tvShow != null
            root.isFocusable = tvShow != null

            if (tvShow == null) return@with

            ivTvShowListPoster.contentDescription = "${tvShow.name} Backdrop"
            tvShow.posterPath?.let {
                val url = TMDbService.buildPosterURL(it)
                Picasso.get().load(url).fit().into(ivTvShowListPoster)
            }
        }
    }
}
