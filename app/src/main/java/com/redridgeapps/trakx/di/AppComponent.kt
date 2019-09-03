package com.redridgeapps.trakx.di

import android.app.Application
import com.redridgeapps.trakx.App
import com.redridgeapps.trakx.di.modules.AppModule
import com.redridgeapps.trakx.di.modules.DBModule
import com.redridgeapps.trakx.di.modules.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DBModule::class,
        NetworkModule::class
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
