package com.redridgeapps.trakx.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

fun <T : ViewDataBinding> LayoutInflater.dataBindingInflate(
    @LayoutRes resource: Int,
    root: ViewGroup?,
    attachToParent: Boolean = false
): T {
    return DataBindingUtil.inflate(this, resource, root, attachToParent)
}

fun NavDirections.navigateFrom(fragment: Fragment) {
    fragment.findNavController().navigate(this)
}
