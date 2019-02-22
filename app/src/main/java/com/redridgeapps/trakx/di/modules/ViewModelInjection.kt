package com.redridgeapps.trakx.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.redridgeapps.trakx.ui.base.DaggerViewModelFactory
import com.redridgeapps.trakx.ui.detail.DetailViewModel
import com.redridgeapps.trakx.ui.episode.EpisodeViewModel
import com.redridgeapps.trakx.ui.episodelist.EpisodeListViewModel
import com.redridgeapps.trakx.ui.tvshowlist.TVShowListViewModel
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
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Suppress("unused")
@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TVShowListViewModel::class)
    abstract fun bindMainViewModel(viewModel: TVShowListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModel(viewModel: DetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EpisodeListViewModel::class)
    abstract fun bindEpisodeListViewModel(viewModel: EpisodeListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EpisodeViewModel::class)
    abstract fun bindEpisodeViewModel(viewModel: EpisodeViewModel): ViewModel
}
