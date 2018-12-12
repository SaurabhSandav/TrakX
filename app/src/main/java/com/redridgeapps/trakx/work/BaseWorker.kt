package com.redridgeapps.trakx.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class BaseWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override val coroutineContext: CoroutineDispatcher = Dispatchers.IO

    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): BaseWorker
    }
}