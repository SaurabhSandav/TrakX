package com.redridgeapps.trakx.di.modules

import androidx.lifecycle.ViewModel
import com.redridgeapps.trakx.ui.common.dagger.AssistedViewModelFactory
import com.redridgeapps.trakx.ui.detail.DetailViewModel
import com.redridgeapps.trakx.ui.episode.EpisodeViewModel
import com.redridgeapps.trakx.ui.episodelist.EpisodeListViewModel
import com.redridgeapps.trakx.ui.tvshowlist.TVShowListViewModel
import com.squareup.inject.assisted.dagger2.AssistedModule
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
@AssistedModule
@Module(includes = [AssistedInject_ViewModelFactoryModule::class])
abstract class ViewModelFactoryModule {

    @Binds
    @IntoMap
    @ViewModelKey(TVShowListViewModel::class)
    abstract fun bindTVShowListViewModelFactory(
        viewModel: TVShowListViewModel.Factory
    ): AssistedViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModelFactory(
        viewModel: DetailViewModel.Factory
    ): AssistedViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(EpisodeListViewModel::class)
    abstract fun bindEpisodeListViewModelFactory(
        viewModel: EpisodeListViewModel.Factory
    ): AssistedViewModelFactory

    @Binds
    @IntoMap
    @ViewModelKey(EpisodeViewModel::class)
    abstract fun bindEpisodeViewModelFactory(
        viewModel: EpisodeViewModel.Factory
    ): AssistedViewModelFactory
}
