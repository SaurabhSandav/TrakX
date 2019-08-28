package com.redridgeapps.trakx.di.modules

import com.redridgeapps.trakx.di.PerActivity
import com.redridgeapps.trakx.ui.activity.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class AndroidComponentBuilder {

    // Activities

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}
