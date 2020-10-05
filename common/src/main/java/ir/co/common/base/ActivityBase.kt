package ir.co.common.base


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import ir.co.common.R
import ir.co.common.dialog.MessageDialog
import ir.co.common.dto.NetworkState
import ir.co.common.helper.OnActivityEventHandler
import ir.co.common.utils.ErrorType
import ir.co.common.utils.LogHelper
import ir.co.common.widget.ToastCustom
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

abstract class ActivityBase : AppCompatActivity(), OnActivityEventHandler {

    private var latestViewId: Int = 0

    val log = LogHelper(this::class.java)

    private var navigationDelay: Boolean = false


    abstract fun getResourceLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        windowsStatus()
        setContentView(getResourceLayoutId())

    }

    private fun windowsStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window?.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
    }

    fun showSnackBar(text: String, action: String? = null, callback: (() -> Unit?)? = null) {
        val snack = Snackbar.make(
            window.decorView.findViewById(android.R.id.content),
            text,
            Snackbar.LENGTH_LONG
        )
        if (action != null) {
            snack.setAction(action) {
                callback!!()
            }
        }

        snack.show()
    }


    fun showToast(text: String) {
        ToastCustom(WeakReference(this)).show(text)
    }

    fun hideKeyboard() {
        GlobalScope.launch {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = currentFocus
            if (view == null)
                view = View(this@ActivityBase)
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideKeyboard(view: View) {
        GlobalScope.launch {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showFragment(@IdRes layoutId: Int, frg: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(layoutId, frg, tag)
            .addToBackStack(tag)
            .commit()
    }

    fun isCurrentFragment(tag: String): Boolean {
        val items =
            supportFragmentManager.fragments.filter { it is FragmentBase<*> || it is PreferenceFragmentCompat }
        if (items.isEmpty())
            return false
        return items[items.size - 1].tag == tag
    }

    fun getCurrentFragment(): Fragment? {
        val items = supportFragmentManager.fragments
        if (items.size == 0)
            return null
        return items[items.size - 1]
    }

    fun checkClickDuration(view: View? = null, navigate: () -> Unit) {
        if (view != null) {
            val sameView = latestViewId == view.id
            latestViewId = view.id
            if (!navigationDelay || !sameView)
                navigate()
        } else {
            if (!navigationDelay)
                navigate()
        }
        navigationDelay = true
        GlobalScope.launch {
            delay(2000)
            navigationDelay = false
        }
    }


    private var isDialogOpen = false
    private lateinit var messageDialog: MessageDialog.Builder
    override fun onError(error: NetworkState?, onRetry: () -> Unit, onClose: () -> Unit) {
        if (error == null)
            return
        messageDialog = MessageDialog.Builder(this)
            .setTitle(getString(R.string.str_error))
            .setNegativeButton(getString(R.string.str_close)) {
                onClose()
            }
            .setCancelable(false)

        var showDialog = true
        var showRetry = false
        val message = when (error.event) {
            ErrorType.Authorization -> {
                showDialog = false
                onClearData(true)
                ""
            }
            ErrorType.Network -> {
                showRetry = true
                messageDialog.setMessageType(MessageDialog.Builder.MessageType.INTERNET)
                    .setTitle(getString(R.string.str_title_internet_error))
                getString(R.string.str_network_error)
            }
            ErrorType.Server -> {
                showRetry = false
                messageDialog.setMessageType(MessageDialog.Builder.MessageType.SERVER)
                    .setTitle(getString(R.string.str_title_server_error))
                getString(R.string.str_network_error)
            }
            ErrorType.Forbidden -> {
                onUserForbidden()
                getString(R.string.str_invalid_user)
            }
            ErrorType.MessageShow -> {
                messageDialog.setMessageType(MessageDialog.Builder.MessageType.MESSAGE)
                error.msg!!
            }
            else -> error.msg ?: getString(R.string.str_runtime_error)
        }
        if (!showDialog) return
        messageDialog.setMessage(message)

        if (showRetry) {
            messageDialog.setPositiveButton(getString(R.string.str_retry)) {
                onRetry()
            }
        }

        if (!messageDialog.isShowing())
            Handler(Looper.getMainLooper()).post {
                messageDialog.build().show()
                isDialogOpen = true
            }
    }

    open fun initToolbar(toolbar: Toolbar, title: String? = null) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title ?: ""
    }

    open fun getDisplayWidth(): Int {
        val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }

    open fun onRetry() {}
    open fun onCloseDialog() {}

    open fun onFinishActivity() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    open fun onClearData(showMessage: Boolean) {
        if (showMessage) ToastCustom(WeakReference(this)).show(getString(R.string.str_error_authorization))
    }

    open fun onUserForbidden() {}

    open fun userLoginUpdate() {}

    @SuppressLint("HardwareIds")
    open fun getUniqueId(): String =
        Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)

    open fun getPreferenceManager(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                212 /* user Authenticated */ -> userLoginUpdate()
                else -> userLoginUpdate()
            }
        }
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    fun darkMode(on: Boolean) {
        if (on) {
            val view: View = window.decorView
            view.systemUiVisibility =
                view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            val view: View = window.decorView
            view.systemUiVisibility =
                view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}