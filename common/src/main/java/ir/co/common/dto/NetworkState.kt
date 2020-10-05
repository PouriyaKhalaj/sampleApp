package ir.co.common.dto

import ir.co.common.utils.ErrorType


enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

data class NetworkState constructor(
    val status  : Status,
    val event   : ErrorType? = null,
    val msg     : String? = null
) {

    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String?) = NetworkState(status = Status.FAILED, msg = msg)
        fun error(event: ErrorType?) = NetworkState(status = Status.FAILED, event = event)
        fun error(event: ErrorType?,msg: String?) = NetworkState(status = Status.FAILED, msg = msg,event = event)
    }
}