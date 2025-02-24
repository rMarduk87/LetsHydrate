package rpt.tool.letshydrate.utils.view.custom.justify

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Layout
import android.text.Spannable
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.max
import kotlin.math.min

class MyJustifiedTextView(var mContext: Context?, attrs: AttributeSet?) :
    JustifiedTextView(mContext, attrs) {
    init {
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // setTextIsSelectable doesn't work unless the text view is attached to the window
        // because it uses the window layout params to check if it can display the handles.
        setTextIsSelectable(true)

        /*Typeface lbl_tf = Typeface.createFromAsset(mContext.getAssets(),"Assistant-Regular.ttf");
        setTypeface(lbl_tf);*/
    }

    // We want our text to be selectable, but we still want links to be clickable.
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val text = text as Spannable
        if (event.action == MotionEvent.ACTION_DOWN) {
            val layout = layout
            if (layout != null) {
                // final int pos = getOffsetForPosition(event.getX(), event.getY()); // API >= 14 only
                val line = getLineAtCoordinate(layout, event.y)
                val pos = getOffsetAtCoordinate(layout, line, event.x)
                val links = text.getSpans(
                    pos, pos,
                    ClickableSpan::class.java
                )
                if (links != null && links.isNotEmpty()) {
                    links[0].onClick(this)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getLineAtCoordinate(layout: Layout, y: Float): Int {
        val max = height - totalPaddingBottom - 1
        val v = (min(
            max.toDouble(),
            max(0.0, (y.toInt() - totalPaddingTop).toDouble())
        ) + scrollY).toInt()
        return layout.getLineForVertical(v)
    }

    private fun getOffsetAtCoordinate(layout: Layout, line: Int, x: Float): Int {
        val max = width - totalPaddingRight - 1
        val v = (min(
            max.toDouble(),
            max(0.0, (x.toInt() - totalPaddingLeft).toDouble())
        ) + scrollX).toInt()
        return layout.getOffsetForHorizontal(line, v.toFloat())
    }
}