package com.redridgeapps.trakx.ui.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.redridgeapps.trakx.utils.Constants.RequestType

class MainViewModel : ViewModel() {

    private val _requestTypeLiveData = MutableLiveData<RequestType>()

    val requestTypeLiveData: LiveData<RequestType> = _requestTypeLiveData

    fun setRequestType(requestType: RequestType) {
        _requestTypeLiveData.value = requestType
    }
}