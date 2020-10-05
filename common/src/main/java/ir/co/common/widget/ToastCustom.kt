package ir.co.common.widget

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import ir.co.common.R
import java.lang.ref.WeakReference


class ToastCustom(val context: WeakReference<Context>) {

    fun show(msg: String) {
        val view = LayoutInflater.from(context.get()).inflate(R.layout.layout_toast, null)
        val toast = Toast(context.get())
        view.findViewById<TextView>(R.id.tvMessage).text = msg
        toast.duration = Toast.LENGTH_LONG
        toast.view = view
        toast.show()
    }
}