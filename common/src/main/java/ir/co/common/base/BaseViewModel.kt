package ir.co.common.base

import android.nfc.FormatException
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import ir.co.common.dto.NetworkState
import ir.co.common.utils.ErrorType
import ir.co.common.utils.SingleLiveEvent
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.lang.reflect.Type
import java.net.UnknownHostException


interface BaseViewModel {

    fun onRetry()

    fun onCloseDialog() {

    }

    fun getHideKeyboard(): SingleLiveEvent<Any>

    fun getNetworkEventShow(): SingleLiveEvent<NetworkState>


    fun showProgressAction() {
        getNetworkEventShow().postValue(NetworkState.LOADING)
    }

    fun hideProgressAction() {
        getNetworkEventShow().postValue(NetworkState.LOADED)
    }

    fun showToastMessage(): SingleLiveEvent<String>

    fun handleError(t: Throwable, returnError: ((errorCode: Long, message: String) -> Unit)? = null) {
        hideProgressAction()
        when (t) {
            is EOFException -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.EofException))
            is FormatException -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.FormatException))
            is IOException, is UnknownHostException -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.Network))
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
                else -> {
                    val errorBody = getErrorModel(t.response().errorBody())
                    if (returnError != null)
                        returnError(errorBody.errorCode, errorBody.message)
                    else getNetworkEventShow().postValue(
                        NetworkState.error(
                            ErrorType.MessageShow,
                            errorBody.message
                        )
                    )
                }
            }
            is JsonSyntaxException -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.JsonFormat))
            else -> getNetworkEventShow().postValue(NetworkState.error(ErrorType.Undefine))
        }
    }


    fun getErrorModel(errorBody: ResponseBody?): ErrorModel {
        return try {
            val intType: Type = object : TypeToken<ErrorModel?>() {}.type
            Gson().fromJson(errorBody?.string() ?: "", intType)
        } catch (e: Exception) {
            ErrorModel(-1, "")
        }
    }

    data class ErrorModel(@Keep val errorCode: Long, @Keep val message: String)
}