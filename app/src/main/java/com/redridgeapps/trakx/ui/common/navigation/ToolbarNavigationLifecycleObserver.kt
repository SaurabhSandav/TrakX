package com.redridgeapps.trakx.ui.common.navigation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController

class ToolbarNavigationLifecycleObserver(
    private val navController: NavController,
    private val listener: NavController.OnDestinationChangedListener
) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        navController.addOnDestinationChangedListener(listener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        navController.removeOnDestinationChangedListener(listener)
    }
}
