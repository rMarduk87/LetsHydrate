package rpt.tool.mementobibere.utils.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import rpt.tool.mementobibere.R

class PrefixEditText(context: Context, attrs: AttributeSet?, defStyle: Int) :
    AppCompatEditText(context, attrs, defStyle) {
    private var mPrefixTextColor: ColorStateList

    var prefix: String? = "$"
    var prefix_color: Int = Color.BLACK
    var prefix_text_align: Int = 0

    constructor(context: Context) : this(context, null) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.editTextStyle
    ) {
        init(attrs)
    }

    init {
        mPrefixTextColor = textColors
        init(attrs)
    }


    @SuppressLint("Recycle", "CustomViewStyleable")
    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val a: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.PrefixTextView)

            prefix = a.getString(R.styleable.PrefixTextView_prefix_text)

            if (TextUtils.isEmpty(prefix)) prefix = "$"

            prefix_color = a.getInteger(R.styleable.PrefixTextView_prefix_text_color, prefix_color)
            mPrefixTextColor = ColorStateList.valueOf(prefix_color)
            prefix_text_align =
                a.getInteger(R.styleable.PrefixTextView_prefix_text_align, prefix_text_align)
        }

        load()
    }

    private fun load() {
        val config: Configuration = resources.configuration
        if (config.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            if (prefix_text_align == 0) setCompoundDrawables(
                null,
                null,
                TextDrawable(" $prefix"),
                null
            )
            else setCompoundDrawables(TextDrawable("$prefix "), null, null, null)
        } else {
            if (prefix_text_align == 0) setCompoundDrawables(
                TextDrawable("$prefix "),
                null,
                null,
                null
            )
            else setCompoundDrawables(null, null, TextDrawable("$prefix "), null)
        }
    }


    fun setPrefixAlign(prefix_text_align: Int) {
        this.prefix_text_align = prefix_text_align
        load()
    }

    fun setPrefixTextColor(color: Int) {
        mPrefixTextColor = ColorStateList.valueOf(color)
        load()
    }

    private inner class TextDrawable(text: String) : Drawable() {
        private var mText = ""

        init {
            mText = text
            setBounds(0, 0, paint.measureText(mText).toInt() + 3, textSize.toInt())
        }

        override fun draw(canvas: Canvas) {
            val paint: Paint = paint
            paint.color = mPrefixTextColor.getColorForState(drawableState, 0)
            val lineBaseline: Int = getLineBounds(0, null)
            canvas.drawText(mText, 0f, (canvas.clipBounds.top + lineBaseline).toFloat(), paint)
        }

        override fun setAlpha(alpha: Int) { /* Not supported */
        }

        override fun setColorFilter(colorFilter: ColorFilter?) { /* Not supported */
        }

        @Deprecated("Deprecated in Java", ReplaceWith("opacity"))
        override fun getOpacity(): Int {
           return PixelFormat.UNKNOWN
        }
    }
}
