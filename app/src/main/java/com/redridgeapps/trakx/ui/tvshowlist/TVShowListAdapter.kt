package com.redridgeapps.trakx.ui.tvshowlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.databinding.ItemTvShowListBinding
import com.redridgeapps.trakx.model.tmdb.TVShow

class TVShowListAdapter(
    private val clickListener: (TVShow) -> Unit
) : PagedListAdapter<TVShow, TVShowListAdapter.TVShowListViewHolder>(TVShow.DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVShowListViewHolder {

        val binding = DataBindingUtil.inflate<ItemTvShowListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_tv_show_list,
            parent,
            false
        )

        val viewHolder = TVShowListViewHolder(binding)

        viewHolder.itemView.setOnClickListener {
            if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
                getItem(viewHolder.adapterPosition)?.let { localTVShow -> clickListener(localTVShow) }
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: TVShowListViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    class TVShowListViewHolder(
        private val binding: ItemTvShowListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(tvShow: TVShow?) {
            binding.root.isClickable = tvShow != null
            binding.root.isFocusable = tvShow != null

            if (tvShow == null) return

            binding.tvShow = tvShow
            binding.executePendingBindings()
        }
    }
}
