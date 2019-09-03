package com.redridgeapps.trakx.di.modules.android

import com.redridgeapps.trakx.work.BaseWorker
import com.redridgeapps.trakx.work.UpcomingEpisodeSyncWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class WorkerKey(val value: KClass<out BaseWorker>)

@Suppress("unused")
@Module
abstract class WorkerFactoryModule {

    @Binds
    @IntoMap
    @WorkerKey(UpcomingEpisodeSyncWorker::class)
    abstract fun bindUpcomingEpisodeSyncWorker(factory: UpcomingEpisodeSyncWorker.Factory): BaseWorker.Factory
}
