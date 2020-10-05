package ir.co.common.dialog


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import ir.co.common.R
import kotlinx.android.synthetic.main.dlg_message.*

class MessageDialog(
    private val dialogBuilder: Builder
) : Dialog(dialogBuilder.context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dlg_message)
        window?.setBackgroundDrawableResource(R.color.transparent)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        init(dialogBuilder)
    }

    private fun init(builder: Builder) {
        messageTextView.text = builder.message ?: ""
        if (builder.onNegativeTitle == null) {
            rejectButton.visibility = View.GONE
        } else {
            rejectButton.visibility = View.VISIBLE
            rejectButton.text = builder.onNegativeTitle ?: ""
            rejectButton.setOnClickListener {
                dialogBuilder.onNegativeClick?.let {
                    it()
                }
                this.dismiss()
            }
        }
        if (builder.onPositiveTitle != null) {
            acceptButton.visibility = View.VISIBLE
            acceptButton.text = builder.onPositiveTitle ?: ""
            acceptButton.setOnClickListener {
                dialogBuilder.onPositiveClick?.let { it() }
                this.dismiss()
            }
        } else {
            acceptButton.visibility = View.GONE
        }
        setCanceledOnTouchOutside(builder.cancelable)

    }

    class Builder constructor(val context: Context) {
        enum class MessageType {
            SERVER, INTERNET, MESSAGE, ERROR, LOGOUT, CLEAR_MESSAGE, LIMIT_AMOUNT
        }

        private var messageDialog: MessageDialog? = null
        var messageType: MessageType? = null
            private set
        var icon: Int? = null
            private set
        var title: String? = null
            private set
        var message: String? = null
            private set

        var onPositiveTitle: String? = null
            private set

        var onPositiveClick: (() -> Unit)? = null
            private set

        var onNegativeTitle: String? = null
            private set

        var onNegativeClick: (() -> Unit)? = null
            private set

        var cancelable: Boolean = false
            private set

        fun setIcon(resourceId: Int): Builder {
            this.icon = resourceId
            return this
        }

        fun setMessageType(messageType: MessageType): Builder {
            this.messageType = messageType
            return this
        }

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun setPositiveButton(title: String, onClick: (() -> Unit)? = null): Builder {
            this.onPositiveTitle = title
            if (onClick != null) {
                this.onPositiveClick = onClick
            }
            return this
        }

        fun setNegativeButton(title: String, onClick: (() -> Unit)? = null): Builder {
            this.onNegativeTitle = title
            if (onClick != null) {
                this.onNegativeClick = onClick
            }
            return this
        }

        fun build(): MessageDialog {
            messageDialog = MessageDialog(this)
            return messageDialog as MessageDialog
        }

        fun isShowing(): Boolean = messageDialog?.isShowing ?: false
    }
}