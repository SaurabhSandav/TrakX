package com.redridgeapps.trakx.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class SwapSourceLiveData<T> : LiveData<T>() {

    private var currentSource: LiveData<T>? = null
    private var currentObserver: Observer<T>? = null

    fun swapSource(newSource: LiveData<T>, newObserver: Observer<T> = Observer { value = it }) {
        unPlug()

        currentSource = newSource
        currentObserver = newObserver

        if (hasActiveObservers()) plug()
    }

    override fun onActive() {
        super.onActive()
        plug()
    }

    override fun onInactive() {
        super.onInactive()
        unPlug()
    }

    private fun plug() {
        currentObserver?.let { currentSource?.observeForever(it) }
    }

    private fun unPlug() {
        currentObserver?.let { currentSource?.removeObserver(it) }
    }
}
