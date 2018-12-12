package com.redridgeapps.trakx.di

import android.app.Application
import com.redridgeapps.trakx.App
import com.redridgeapps.trakx.di.modules.AndroidComponentBuilder
import com.redridgeapps.trakx.di.modules.AppModule
import com.redridgeapps.trakx.di.modules.ViewModelFactoryModule
import com.redridgeapps.trakx.di.modules.WorkerFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AndroidComponentBuilder::class,
        AppModule::class,
        ViewModelFactoryModule::class,
        WorkerFactoryModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
