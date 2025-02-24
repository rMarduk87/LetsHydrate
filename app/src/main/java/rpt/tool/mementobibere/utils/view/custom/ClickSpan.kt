package rpt.tool.mementobibere.utils.view.custom

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class ClickSpan(private val mListener: OnClickListener) :
    ClickableSpan() {
    private val isUnderline = false

    var text_color: Int = Color.BLUE

    override fun onClick(widget: View) {
        mListener?.onClick()
    }

    interface OnClickListener {
        fun onClick()
    }

    fun setTextColor(color: Int) {
        text_color = color
    }

    override fun updateDrawState(ds: TextPaint) {
        //super.updateDrawState(ds);
        ds.isUnderlineText = isUnderline
        ds.bgColor = (Color.parseColor("#FAFAFA"))
        //ds.setARGB(255,255,255,255);
        //ds.setColor(Color.parseColor("#014E76"));
        ds.color = text_color
    }
}