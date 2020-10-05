package ir.co.common.base


import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import ir.co.common.dto.NetworkState
import ir.co.common.dto.Status
import ir.co.common.helper.SettingManager
import ir.co.common.utils.LogHelper
import javax.inject.Inject

abstract class FragmentParent<T : AbsBaseViewModel, K : ViewDataBinding> : FragmentBase<K>() {
    @Inject
    lateinit var settingManager: SettingManager

    companion object {
        var justFirstInitData = true
    }

    protected lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun init() {
        viewModel = ViewModelProviders.of(this, getFactory()).get(getViewModelClass())
        lifecycle.addObserver(viewModel)
        viewModel.network.observe(this, Observer {
            LogHelper(this::class.java).e("OkHttp", "viewModel.network:: $it")
            handleNetwork(it)
        })
        viewModel.getNetworkEventShow().observe(this, Observer {
            LogHelper(this::class.java).e("OkHttp", "getNetworkEventShow:: $it")
            handleNetwork(it)
        })
        viewModel.onEmptyList.observe(this, Observer {
            emptyListMessageHandler(it)
        })

        viewModel.getHideKeyboard().observe(this, Observer {
            hideKeyboard()
        })
        viewModel.showToastMessage().observe(this, Observer {
            showToast(it)
        })
    }

    override fun showProgress() {
        super.showProgress()
        viewModel.onShowProgressBar.postValue(true)
    }

    override fun hideProgress() {
        super.hideProgress()
        viewModel.onShowProgressBar.postValue(false)
    }

    private fun handleNetwork(networkState: NetworkState) {
        when (networkState.status) {
            Status.FAILED -> onErrorHandler(networkState)
            Status.SUCCESS -> hideProgress()
            else -> showProgress()
        }
    }

    override fun onRetry() {
        super.onRetry()
        viewModel.onRetry()
    }

    override fun onCloseDialog() {
        super.onCloseDialog()
        viewModel.onCloseDialog()
    }

    open fun getPreferenceManager(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)


    protected abstract fun getFactory(): ViewModelProvider.Factory

    protected abstract fun getViewModelClass(): Class<T>

    protected abstract fun inject()

    protected abstract fun initView()
}