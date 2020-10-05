package ir.co.common.helper

import android.content.Intent
import ir.co.common.dto.NetworkState

interface OnActivityEventHandler {
    fun onError(error: NetworkState?, onRetry: () -> Unit, onClose: () -> Unit)
    fun onStartActivity(intent : Intent, finished: Boolean = false)
}