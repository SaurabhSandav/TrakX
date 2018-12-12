package com.redridgeapps.trakx.screen.base

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.redridgeapps.trakx.screen.common.ActivityErrorActions
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel, VDB : ViewDataBinding> : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var binding: VDB
        private set

    protected lateinit var viewModel: VM
        private set

    protected abstract val layoutResId: Int

    protected abstract val viewModelClass: Class<VM>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)

        viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(viewModelClass)

        ActivityErrorActions.handleErrors(this, viewModel.errorsLiveData)
    }
}
