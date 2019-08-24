package com.redridgeapps.trakx

import androidx.work.Configuration
import androidx.work.WorkManager
import com.redridgeapps.trakx.di.DaggerAppComponent
import com.redridgeapps.trakx.work.BaseWorkerFactory
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class App : DaggerApplication() {

    @Inject
    lateinit var baseWorkerFactory: BaseWorkerFactory

    override fun onCreate() {
        super.onCreate()

        setupWorkManager()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this@App).build()
    }

    private fun setupWorkManager() {

        val config = Configuration.Builder()
            .setWorkerFactory(baseWorkerFactory)
            .build()

        WorkManager.initialize(this, config)
    }
}
