package com.redridgeapps.trakx.screen.common

import android.view.View
import androidx.lifecycle.LiveData
import com.google.android.material.snackbar.Snackbar
import com.redridgeapps.trakx.R
import com.redridgeapps.trakx.screen.base.BaseActivity
import com.redridgeapps.trakx.utils.RetryableError

object ActivityErrorActions {

    fun handleErrors(activity: BaseActivity<*, *>, errorsLiveData: LiveData<Throwable>) {

        errorsLiveData.observe(activity) {
            val parentView = activity.findViewById<View>(android.R.id.content)

            if (it is RetryableError) showRetryableError(parentView, it)
            else showError(parentView)
        }
    }

    private fun showError(view: View) {
        Snackbar.make(view, R.string.error_network_request_failure, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun showRetryableError(view: View, error: RetryableError) {
        Snackbar.make(view, R.string.error_network_request_failure, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.text_retry) { error.retry() }
            .show()
    }
}
