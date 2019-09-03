package com.redridgeapps.trakx.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider

private typealias WorkerFactoryMap = Map<Class<out BaseWorker>, @JvmSuppressWildcards Provider<BaseWorker.Factory>>

@Reusable
class BaseWorkerFactory @Inject constructor(
    private val creators: WorkerFactoryMap
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        val modelClass: Class<out BaseWorker>

        try {
            modelClass = Class.forName(workerClassName)
                .asSubclass(BaseWorker::class.java) as Class<out BaseWorker>
        } catch (e: ClassNotFoundException) {
            return null
        }

        val creator = creators[modelClass] ?: creators.asIterable().firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value

        return creator?.get()?.create(appContext, workerParameters) ?: return null
    }
}
