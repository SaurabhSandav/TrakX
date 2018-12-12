package com.redridgeapps.trakx.utils

class RetryableError(
    throwable: Throwable,
    val retry: () -> Unit
) : Throwable(throwable)
