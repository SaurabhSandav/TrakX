package com.redridgeapps.trakx.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

abstract class BaseWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    interface Factory {
        fun create(appContext: Context, params: WorkerParameters): BaseWorker
    }
}
