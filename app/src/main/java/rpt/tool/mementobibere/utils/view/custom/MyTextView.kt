package rpt.tool.mementobibere.utils.view.custom

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.widget.TextView
import rpt.tool.mementobibere.R


@SuppressLint("AppCompatCustomView")
class MyTextView : TextView {
    var fontName: String? = null

    constructor(
        context: android.content.Context?,
        attrs: android.util.AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    constructor(
        context: android.content.Context?,
        attrs: android.util.AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init(attrs)
    }

    constructor(context: android.content.Context?, attrs: android.util.AttributeSet?) : super(
        context,
        attrs
    ) {
        init(attrs)
    }

    constructor(context: android.content.Context?) : super(context) {
        init(null)
    }

    private fun init(attrs: android.util.AttributeSet?) {
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTextView)
            fontName = a.getString(R.styleable.MyTextView_fontname)

            setTypeFace()

            a.recycle()
        }
    }


    private fun setTypeFace() {
        try {
            if (fontName != null) {
                val myTypeface =
                    android.graphics.Typeface.createFromAsset(context.assets, fontName)
                setTypeface(myTypeface)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
