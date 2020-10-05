package ir.co.common.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import ir.co.common.R
import ir.co.common.dto.Shape

class ImageViewCustom : AppCompatImageView {

    private var placeHolder: Int = 0

    var isSquare: Boolean = false

    var shape: Shape = Shape.UNDEFINE

    constructor(context: Context?, placeHolder: Int) : super(context) {
        this.placeHolder = placeHolder
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        context?.let {
            val typed = context.obtainStyledAttributes(attrs, R.styleable.ImageViewCustom, defStyleAttr, 0)
            placeHolder = typed.getInt(R.styleable.ImageViewCustom_place_holder, 0)
            shape = Shape.convert(
                typed.getInt(
                    R.styleable.ImageViewCustom_imageShape,
                    0
                )
            )
            isSquare = when (shape) {
                Shape.RECTANGLE,
                Shape.RADIUS -> true
                else -> false
            }
            if (placeHolder != 0) {
                setImageResource(getPlaceHolder())
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, if (isSquare) widthMeasureSpec else heightMeasureSpec)
    }

    fun loadBankLogo(url: String, cardPrefix: String) {
        this.load("$url${cardPrefix}.png")
    }

    fun load(url: String?) {
        val request = Glide.with(this.context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(getPlaceHolder())
            .error(getPlaceHolder())
            .transition(withCrossFade())

        when (shape) {
            Shape.CIRCLE -> request.transform(CircleCrop()).into(this)
            Shape.RADIUS -> {
                request.transform(RoundedCorners(25)).into(
                    this
                )
            }
            else -> request.into(this)
        }
    }


    fun load(resource: Int) {
        val request = Glide.with(this.context)
            .load(resource)
            .placeholder(getPlaceHolder())

        when (shape) {
            Shape.CIRCLE -> request.transform(CircleCrop()).into(this)
            Shape.RADIUS -> request.transform(RoundedCorners(10)).into(
                this
            )
            else -> request.into(this)
        }
    }


     fun getPlaceHolder(): Int = when (placeHolder) {
        else -> R.drawable.ic_blank
    }
}

