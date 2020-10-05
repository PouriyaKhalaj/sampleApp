package ir.co.common.base


import androidx.lifecycle.*
import ir.co.common.dto.EmptyMessage
import ir.co.common.dto.NetworkState
import ir.co.common.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

abstract class AbsBaseViewModel : ViewModel(), BaseViewModel, LifecycleObserver {
    private val hideKeyboard: SingleLiveEvent<Any> = SingleLiveEvent()

    private val showErrorEvent: SingleLiveEvent<NetworkState> = SingleLiveEvent()

    private val onShowToast: SingleLiveEvent<String> = SingleLiveEvent()

    open val onShowProgressBar: SingleLiveEvent<Boolean> = SingleLiveEvent()
    open var network: LiveData<NetworkState> = MutableLiveData()

    open var onEmptyList: LiveData<EmptyMessage> = MutableLiveData()
    open var onEndListMessage: LiveData<EmptyMessage> = MutableLiveData()

    override fun getNetworkEventShow(): SingleLiveEvent<NetworkState> = showErrorEvent

    override fun getHideKeyboard(): SingleLiveEvent<Any> = hideKeyboard

    override fun showToastMessage(): SingleLiveEvent<String> = onShowToast




    private val job = Job()

    protected val uiScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCleared() {
        super.onCleared()
        uiScope.coroutineContext.cancel()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
        uiScope.coroutineContext.cancel()
    }

    abstract fun onCreateDone()

}