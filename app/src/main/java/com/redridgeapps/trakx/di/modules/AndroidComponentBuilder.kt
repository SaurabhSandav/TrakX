package com.redridgeapps.trakx.di.modules

import com.redridgeapps.trakx.di.PerActivity
import com.redridgeapps.trakx.ui.activity.main.MainActivity
import com.redridgeapps.trakx.ui.widget.UpcomingEpisodeListWidgetService
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey

@Suppress("unused")
@Module
abstract class AndroidComponentBuilder {

    // Activities

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    // Services

    @ClassKey(UpcomingEpisodeListWidgetService::class)
    @ContributesAndroidInjector
    abstract fun bindUpcomingEpisodeListWidgetService(): UpcomingEpisodeListWidgetService
}
