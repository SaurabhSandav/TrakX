package com.redridgeapps.trakx.ui.common

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.redridgeapps.trakx.utils.Constants.KEY_VIEWMODEL_ARGS

interface ViewModelArgs : Parcelable {

    fun asBundle(): Bundle = bundleOf(KEY_VIEWMODEL_ARGS to this)
}

class ViewModelArgsLazy<T : ViewModelArgs>(
    val handle: SavedStateHandle
) : Lazy<T> {

    private var cached: T? = null

    override val value: T
        get() {
            val args = cached
            return if (args == null) {
                cached = handle.get<T>(KEY_VIEWMODEL_ARGS)
                cached ?: error("ViewModelArgs not found")
            } else args
        }

    override fun isInitialized() = cached != null
}

@Suppress("unused")
fun <T : ViewModelArgs> ViewModel.viewModelArgs(
    handle: SavedStateHandle
): Lazy<T> = ViewModelArgsLazy(handle)
