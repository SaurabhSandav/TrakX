package com.redridgeapps.trakx.ui.base

import androidx.lifecycle.ViewModel
import com.redridgeapps.trakx.ui.common.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {

    val errorsLiveData = SingleLiveEvent<Throwable>()
}
