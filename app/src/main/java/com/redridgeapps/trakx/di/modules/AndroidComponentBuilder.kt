package com.redridgeapps.trakx.di.modules

import com.redridgeapps.trakx.screen.detail.DetailActivity
import com.redridgeapps.trakx.screen.episode.EpisodeActivity
import com.redridgeapps.trakx.screen.episodelist.EpisodeListActivity
import com.redridgeapps.trakx.screen.main.MainActivity
import com.redridgeapps.trakx.screen.widget.UpcomingEpisodeListWidgetService
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey

@Suppress("unused")
@Module
abstract class AndroidComponentBuilder {

    // Activities

    @ClassKey(MainActivity::class)
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ClassKey(DetailActivity::class)
    @ContributesAndroidInjector
    abstract fun bindDetailActivity(): DetailActivity

    @ClassKey(EpisodeListActivity::class)
    @ContributesAndroidInjector
    abstract fun bindEpisodeListActivity(): EpisodeListActivity

    @ClassKey(EpisodeActivity::class)
    @ContributesAndroidInjector
    abstract fun bindEpisodeActivity(): EpisodeActivity

    // Services

    @ClassKey(UpcomingEpisodeListWidgetService::class)
    @ContributesAndroidInjector
    abstract fun bindUpcomingEpisodeListWidgetService(): UpcomingEpisodeListWidgetService
}
