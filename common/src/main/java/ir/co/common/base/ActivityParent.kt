package ir.co.common.base


import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ir.co.common.dto.EmptyMessage
import ir.co.common.dto.Status
import ir.co.common.helper.SettingManager
import ir.co.common.utils.LogHelper
import javax.inject.Inject

abstract class ActivityParent<T : AbsBaseViewModel> : ActivityBase() {

    @Inject
    lateinit var settingManager: SettingManager

    val logHelper = LogHelper(ActivityParent::class.java)

    protected lateinit var viewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        init()
    }

    private fun init() {
        viewModel = ViewModelProviders.of(this, getFactory()).get(getViewModelClass())
        lifecycle.addObserver(viewModel)
        viewModel.onEmptyList.observe(this, Observer {
            emptyListMessageHandler(it)
        })
        viewModel.network.observe(this, Observer {
            when (it?.status) {
                Status.RUNNING -> showProgress()
                Status.FAILED -> {
                    hideProgress()
                    onError(it, { onRetry() }, { onCloseDialog() })
                }
                else -> hideProgress()
            }
        })
        viewModel.getNetworkEventShow().observe(this, Observer {
            when (it.status) {
                Status.FAILED -> {
                    hideProgress()
                    onError(it, { onRetry() }, { onCloseDialog() })
                }
                Status.SUCCESS -> hideProgress()
                else -> showProgress()
            }
        })

        viewModel.getHideKeyboard().observe(this, Observer {
            hideKeyboard()
        })
    }

    override fun onStartActivity(intent: Intent, finished: Boolean) {
        startActivity(intent)
        if (finished) {
            finish()
        }
    }

    protected abstract fun getFactory(): ViewModelProvider.Factory
    protected abstract fun getViewModelClass(): Class<T>
    protected abstract fun inject()

    open fun showProgress() {
        viewModel.onShowProgressBar.postValue(true)
        for (item in supportFragmentManager.fragments) {
            if (item is FragmentBase<*>) {
                item.showProgress()
            }
        }
    }

    open fun hideProgress() {
        viewModel.onShowProgressBar.postValue(false)
        for (item in supportFragmentManager.fragments) {
            if (item is FragmentBase<*>) {
                item.hideProgress()
            }
        }
    }

    open fun emptyListMessageHandler(emptyMessage: EmptyMessage) {
        for (item in supportFragmentManager.fragments) {
            if (item is FragmentBase<*>) {
                item.emptyListMessageHandler(emptyMessage)
            }
        }
    }
}