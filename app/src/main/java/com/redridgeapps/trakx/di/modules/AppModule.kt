package com.redridgeapps.trakx.di.modules

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.redridgeapps.trakx.di.modules.android.AndroidComponentBuilder
import com.redridgeapps.trakx.di.modules.android.FragmentBuilder
import com.redridgeapps.trakx.di.modules.android.ViewModelFactoryModule
import com.redridgeapps.trakx.di.modules.android.WorkerFactoryModule
import dagger.Module
import dagger.Provides
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Module(
    includes = [
        AndroidSupportInjectionModule::class,
        AndroidComponentBuilder::class,
        FragmentBuilder::class,
        ViewModelFactoryModule::class,
        WorkerFactoryModule::class
    ]
)
object AppModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideResources(app: Application): Resources {
        return app.resources
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }
}
