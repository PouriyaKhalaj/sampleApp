package ir.co.common.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ir.co.common.R
import ir.co.common.dialog.MessageDialog
import ir.co.common.dto.EmptyMessage
import ir.co.common.dto.NetworkState
import ir.co.common.helper.OnActivityEventHandler
import ir.co.common.utils.LogHelper
import ir.co.common.widget.ToastCustom
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

abstract class FragmentBase<T : ViewDataBinding> : Fragment() {

    protected lateinit var binding: T

    private var navigationDelay: Boolean = false

    val logHelper: LogHelper = LogHelper(javaClass)

    private var onActivityEventListener: OnActivityEventHandler? = null

    abstract fun getResourceLayoutId(): Int

    abstract fun init()

    protected var onActivityResultNotifier: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit =
        { _, _, _ -> }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, getResourceLayoutId(), container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setHasOptionsMenu(true)
        return binding.root
    }

    fun showSnackBar(text: String, action: String? = null, callback: (() -> Unit?)? = null) {
        val snack = Snackbar.make(
            activity?.window?.decorView!!.findViewById(android.R.id.content),
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

    open fun updateContent() {

    }

    protected fun showSnackBar(text: String?) {
        if (view != null)
            Snackbar.make(requireView(), text ?: "", Snackbar.LENGTH_LONG).show()
    }

    fun showToast(text: String) {
        ToastCustom(WeakReference(requireContext())).show(text)
    }

    fun showMessageDialog(
        title: String? = getString(R.string.str_error),
        message: String,
        messageType: MessageDialog.Builder.MessageType? = MessageDialog.Builder.MessageType.CLEAR_MESSAGE,
        onClose: (() -> Unit)? = null
    ) {
        MessageDialog.Builder(requireContext())
            .setTitle(title ?: getString(R.string.str_error))
            .setPositiveButton(getString(R.string.str_close)) {
                onClose?.let { it() }
            }
            .setCancelable(false)
            .setMessageType(messageType!!)
            .setMessage(message)
            .build()
            .show()
    }

    fun hideKeyboard() {
        GlobalScope.launch {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity?.currentFocus
            if (view == null)
                view = View(context)
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideKeyboard(view: View) {
        GlobalScope.launch {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showFragment(@IdRes layoutId: Int, frg: Fragment, tag: String) {
        childFragmentManager
            .beginTransaction()
            .replace(layoutId, frg, tag)
            .addToBackStack(tag)
            .commit()
    }


    open fun startCustomActivity(intent: Intent, finished: Boolean = false) {
        onActivityEventListener?.onStartActivity(intent, finished)
    }

    open fun showProgress() {
    }

    open fun hideProgress() {
    }

    open fun emptyListMessageHandler(emptyMessage: EmptyMessage) {}

    open fun endListMessageHandler(emptyMessage: EmptyMessage) {}

    open fun onErrorHandler(error: NetworkState?) {
        hideProgress()
        if (onActivityEventListener != null) {
            onActivityEventListener?.onError(error, {
                onRetry()
            }, {
                onCloseDialog()
            })
        }
    }

    open fun onRetry() {}
    open fun onCloseDialog() {}


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnActivityEventHandler) {
            onActivityEventListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        onActivityEventListener = null
    }

    fun convertDpToPixelInt(dp: Int): Int {
        return (dp * (resources.displayMetrics.densityDpi.toFloat() / 160.0f)).toInt()
    }
}