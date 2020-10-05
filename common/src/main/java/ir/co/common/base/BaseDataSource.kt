package ir.co.common.base


import android.nfc.FormatException
import androidx.paging.PageKeyedDataSource
import com.google.gson.JsonSyntaxException
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import ir.co.common.dto.EmptyMessage
import ir.co.common.dto.NetworkState
import ir.co.common.utils.ErrorType
import ir.co.common.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.net.UnknownHostException


interface BasePageKeyed {
    fun getNetworkEventShow(): SingleLiveEvent<NetworkState>
    fun getEmptyList(): SingleLiveEvent<EmptyMessage>
    fun endListMessage(): SingleLiveEvent<EmptyMessage>
}

abstract class BaseDataSource<T> : PageKeyedDataSource<Int, T>(), BasePageKeyed {

    private val job = Job()

    val uiScope = CoroutineScope(Dispatchers.IO + job)
    var haveNextPage: Boolean = true
    var lastRequestedPage = 1
    var isRequestInProgress = false


    private val compositeDisposable = CompositeDisposable()
    private var retryCompletable: Completable? = null


    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
        if (action == null) compositeDisposable.clear()
    }

    fun onRetry() {
        if (retryCompletable != null)
            uiScope.launch {
                compositeDisposable.add(retryCompletable?.subscribe())
            }
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, T>
    ) {
        setRetry(Action { loadInitial(params, callback) })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        setRetry(Action { loadAfter(params, callback) })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        setRetry(Action { loadBefore(params, callback) })
    }


    fun showProgressAction() {
        getEmptyList().postValue(EmptyMessage(false))
        getNetworkEventShow().postValue(NetworkState.LOADING)
    }

    fun hideProgressAction() {
        getNetworkEventShow().postValue(NetworkState.LOADED)
    }

    fun handleResponseData(pageSize: Int, dataSize: Int, page: Int, emptyMessage: String? = null) {
        lastRequestedPage = page
        haveNextPage = dataSize >= pageSize
        getEmptyList().postValue(EmptyMessage(page == 1 && dataSize == 0, emptyMessage))
        endListMessage().postValue(
            EmptyMessage(
                page == 1 && dataSize > 0 || page > 1,
                emptyMessage
            )
        )
        hideProgressAction()
    }

    fun handleError(t: Throwable) {
        hideProgressAction()
        when (t) {
            is EOFException -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.EofException))
            is FormatException -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.FormatException))
            is IOException -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.Network))
            is HttpException -> when {
                t.code() == 401 -> {
                    getNetworkEventShow().postValue(NetworkState.error(ErrorType.Authorization))
                }
                t.code() == 500 -> {
                    getNetworkEventShow().postValue(NetworkState.error(ErrorType.Server))
                }
                t.code() == 403 -> {
                    getNetworkEventShow().postValue(NetworkState.error(ErrorType.Forbidden))
                }
                else -> getNetworkEventShow().postValue(
                    NetworkState.error(
                        ErrorType.MessageShow,
                        getErrorMessage(t.response()!!.errorBody())
                    )
                )
            }
            is JsonSyntaxException -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.JsonFormat))
            else -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.Undefine))
        }
    }

    private fun getErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val jsonObject = JSONObject(errorBody?.string() ?: "")
            jsonObject.getString("message")
        } catch (e: Exception) {
            ""
        }
    }

}