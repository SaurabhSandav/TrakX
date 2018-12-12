package com.redridgeapps.trakx.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

private typealias WorkerFactoryMap = Map<Class<out BaseWorker>, @JvmSuppressWildcards Provider<BaseWorker.Factory>>

@Singleton
class BaseWorkerFactory @Inject constructor(
    private val creators: WorkerFactoryMap
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        val modelClass = Class.forName(workerClassName).asSubclass(BaseWorker::class.java)

        val creator = creators[modelClass] ?: creators.asIterable().firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value

        creator ?: throw IllegalArgumentException("Unknown Worker class $modelClass")

        return creator.get().create(appContext, workerParameters)
    }
}