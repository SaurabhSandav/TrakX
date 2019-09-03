package com.redridgeapps.trakx.ui.common.dagger

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.redridgeapps.trakx.ui.common.ViewModelArgs
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider

private typealias ViewModelFactoryMap =
        Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<AssistedViewModelFactory>>

interface AssistedViewModelFactory {
    fun create(handle: SavedStateHandle): ViewModel
}

class SavedStateVMFactory(
    private val creators: ViewModelFactoryMap,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val creator = creators[modelClass] ?: creators.asIterable().firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value

        requireNotNull(creator) { "Unknown ViewModel class $modelClass" }

        @Suppress("UNCHECKED_CAST")
        return creator.get().create(handle) as T
    }
}

@Reusable
class ViewModelFactoryGenerator @Inject constructor(
    private val creators: ViewModelFactoryMap
) {

    fun generate(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
    ) = SavedStateVMFactory(creators, owner, defaultArgs)
}

inline fun <reified VM : ViewModel> Fragment.savedStateViewModels(
    factoryGenerator: ViewModelFactoryGenerator? = null,
    noinline defaultArgs: (() -> ViewModelArgs)? = null
): Lazy<VM> {

    val factoryProducer = when {
        factoryGenerator != null -> { ->
            factoryGenerator.generate(this, defaultArgs?.invoke()?.asBundle())
        }
        else -> null
    }

    return viewModels(factoryProducer = factoryProducer)
}
