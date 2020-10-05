package ir.co.common.dto

import android.graphics.drawable.Drawable

data class EmptyMessage(
    var show: Boolean,
    var message: String? = null,
    var iconRes: Drawable? = null
)