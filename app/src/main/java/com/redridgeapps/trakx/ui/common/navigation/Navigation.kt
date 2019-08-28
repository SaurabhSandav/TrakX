package com.redridgeapps.trakx.ui.common.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.appbar.CollapsingToolbarLayout

fun Fragment.setupToolbarWithNavigation(
    toolbar: Toolbar,
    drawerLayout: DrawerLayout? = null
) {
    setupWithNavigation(toolbar, drawerLayout) {
        ToolbarOnDestinationChangedListener(toolbar, it)
    }
}

fun Fragment.setupCollapsingToolbarWithNavigation(
    collapsingToolbarLayout: CollapsingToolbarLayout,
    toolbar: Toolbar
) {
    setupWithNavigation(toolbar) {
        CollapsingToolbarOnDestinationChangedListener(collapsingToolbarLayout, toolbar, it)
    }
}

private fun Fragment.setupWithNavigation(
    toolbar: Toolbar,
    drawerLayout: DrawerLayout? = null,
    builder: (AppBarConfiguration) -> NavController.OnDestinationChangedListener
) {

    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

    val navController = findNavController()

    val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
    val listener = builder(appBarConfiguration)

    toolbar.setNavigationOnClickListener { navController.navigateUp(appBarConfiguration) }

    viewLifecycleOwner.lifecycle.addObserver(
        ToolbarNavigationLifecycleObserver(navController, listener)
    )
}
