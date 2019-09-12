package com.redridgeapps.trakx.ui.common

import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavDirections.navigateWith(navController: NavController) {
    navController.navigate(this)
}
