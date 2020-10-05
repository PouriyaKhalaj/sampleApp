package ir.co.avatar.utils

import androidx.databinding.BindingAdapter
import ir.co.common.widget.ImageViewCustom


class AdaptersBinding {
    companion object {

        @JvmStatic
        @BindingAdapter(value = ["app:srcUrl"])
        fun setIconUrlImageView(iv: ImageViewCustom, srcUrl: String?) {
            iv.load(srcUrl)
        }
    }
}
