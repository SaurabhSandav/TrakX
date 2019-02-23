package com.redridgeapps.trakx.di.modules

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.redridgeapps.trakx.ui.base.CustomNavHostFragment
import com.redridgeapps.trakx.ui.base.dagger.DaggerFragmentFactory
import com.redridgeapps.trakx.ui.detail.DetailFragment
import com.redridgeapps.trakx.ui.episodelist.EpisodeListFragment
import com.redridgeapps.trakx.ui.tvshowlist.TVShowListFragment
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
internal annotation class FragmentKey(val value: KClass<out Fragment>)

@Module
abstract class FragmentBuilder {

    @Binds
    abstract fun bindFragmentFactory(factory: DaggerFragmentFactory): FragmentFactory

    @Binds
    @IntoMap
    @FragmentKey(CustomNavHostFragment::class)
    abstract fun bindCustomNavHostFragment(factory: CustomNavHostFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(TVShowListFragment::class)
    abstract fun bindTVShowListFragment(fragment: TVShowListFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(DetailFragment::class)
    abstract fun bindDetailFragment(fragment: DetailFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(EpisodeListFragment::class)
    abstract fun bindEpisodeListFragment(fragment: EpisodeListFragment): Fragment
}
