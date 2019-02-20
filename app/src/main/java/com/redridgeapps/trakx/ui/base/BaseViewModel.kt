package com.redridgeapps.trakx.ui.base

import androidx.lifecycle.ViewModel
import com.redridgeapps.trakx.ui.common.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    val errorsLiveData = SingleLiveEvent<Throwable>()

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
