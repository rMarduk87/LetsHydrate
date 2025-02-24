package rpt.tool.letshydrate.utils.view.custom.justify

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.TextView
import rpt.tool.letshydrate.utils.view.custom.justify.Justify.Justified
import rpt.tool.letshydrate.utils.view.custom.justify.Justify.ScaleSpan

@SuppressLint("AppCompatCustomView")
open class JustifiedTextView : TextView, Justified {
    @Suppress("unused")
    constructor(context: Context?) : super(context) {
        super.setMovementMethod(LinkMovementMethod())
    }

    @Suppress("unused")
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        if (movementMethod == null) super.setMovementMethod(LinkMovementMethod())
    }

    @Suppress("unused")
    constructor(
        context: Context?,
        attrs: AttributeSet?, defStyle: Int
    ) : super(context, attrs, defStyle) {
        if (movementMethod == null) super.setMovementMethod(LinkMovementMethod())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Make sure we don't call setupScaleSpans again if the measure was triggered
        // by setupScaleSpans itself.
        if (!mMeasuring) {
            val typeface = typeface
            val textSize = textSize
            val textScaleX = textScaleX
            val fakeBold = paint.isFakeBoldText
            if (mTypeface !== typeface || mTextSize != textSize || mTextScaleX != textScaleX || mFakeBold != fakeBold) {
                val width = MeasureSpec.getSize(widthMeasureSpec)
                if (width > 0 && width != mWidth) {
                    mTypeface = typeface
                    mTextSize = textSize
                    mTextScaleX = textScaleX
                    mFakeBold = fakeBold
                    mWidth = width
                    mMeasuring = true
                    try {
                        // Setup ScaleXSpans on whitespaces to justify the text.
                        Justify.setupScaleSpans(this, mSpanStarts, mSpanEnds, mSpans)
                    } finally {
                        mMeasuring = false
                    }
                }
            }
        }
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int, lengthBefore: Int, lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        val layout = layout
        if (layout != null) {
            Justify.setupScaleSpans(this, mSpanStarts, mSpanEnds, mSpans)
        }
    }

    override val textView: TextView
        get() = this

    override val maxProportion: Float
        get() = Justify.DEFAULT_MAX_PROPORTION

    private var mMeasuring = false

    private var mTypeface: Typeface? = null
    private var mTextSize = 0f
    private var mTextScaleX = 0f
    private var mFakeBold = false
    private var mWidth = 0

    private val mSpanStarts = IntArray(MAX_SPANS)
    private val mSpanEnds = IntArray(MAX_SPANS)
    private val mSpans = arrayOfNulls<ScaleSpan>(MAX_SPANS)

    companion object {
        private const val MAX_SPANS = 512
    }
}