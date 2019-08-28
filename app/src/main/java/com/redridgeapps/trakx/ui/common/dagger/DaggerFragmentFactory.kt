package com.redridgeapps.trakx.ui.common.dagger

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.navigation.fragment.NavHostFragment
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DaggerFragmentFactory @Inject constructor(
    private val creators: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        val modelClass = Class.forName(className).asSubclass(Fragment::class.java)

        // Let the library handle instantiating NavHostFragment
        if (modelClass.isAssignableFrom(NavHostFragment::class.java))
            return super.instantiate(classLoader, className)

        val creator = creators[modelClass] ?: creators.asIterable().firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value

        requireNotNull(creator) { "Unknown Fragment class $modelClass" }

        return creator.get()
    }
}
