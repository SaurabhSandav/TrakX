package com.redridgeapps.trakx.di.modules

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.redridgeapps.trakx.ui.base.CustomNavHostFragment
import com.redridgeapps.trakx.ui.base.dagger.DaggerFragmentFactory
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
}
