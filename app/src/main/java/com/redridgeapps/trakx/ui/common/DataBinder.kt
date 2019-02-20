package com.redridgeapps.trakx.ui.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.redridgeapps.trakx.api.TMDbService
import com.squareup.picasso.Picasso

object DataBinder {

    @JvmStatic
    @BindingAdapter("posterPath")
    fun setPosterUrl(imageView: ImageView, urlPath: String?) {
        if (urlPath.isNullOrBlank()) return

        val url = TMDbService.buildPosterURL(urlPath)

        Picasso.get().load(url).fit().into(imageView)
    }

    @JvmStatic
    @BindingAdapter("backdropPath")
    fun setBackdropUrl(imageView: ImageView, urlPath: String?) {
        if (urlPath.isNullOrBlank()) return

        val url = TMDbService.buildBackdropURL(urlPath)

        Picasso.get().load(url).fit().into(imageView)
    }
}