package com.redridgeapps.trakx.ui.common

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MediatorLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner) {
            if (mPending.compareAndSet(true, false))
                observer.onChanged(it)
        }
    }

    override fun setValue(value: T?) {
        mPending.set(true)
        super.setValue(value)
    }
}

private const val TAG = "SingleLiveEvent"