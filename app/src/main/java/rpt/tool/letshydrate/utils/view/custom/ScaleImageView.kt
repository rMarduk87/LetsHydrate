package rpt.tool.letshydrate.utils.view.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class ScaleImageView : ImageView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        try {
            val drawable = drawable
            if (drawable == null) {
                setMeasuredDimension(0, 0)
            } else {
                val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
                val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)
                if (measuredHeight == 0 && measuredWidth == 0) { //Height and width set to wrap_content
                    setMeasuredDimension(measuredWidth, measuredHeight)
                } else if (measuredHeight == 0) { //Height set to wrap_content
                    val width = measuredWidth
                    val height = width * drawable.intrinsicHeight / drawable.intrinsicWidth
                    setMeasuredDimension(width, height)
                } else if (measuredWidth == 0) { //Width set to wrap_content
                    val height = measuredHeight
                    val width = height * drawable.intrinsicWidth / drawable.intrinsicHeight
                    setMeasuredDimension(width, height)
                } else { //Width and height are explicitly set (either to match_parent or to exact value)
                    setMeasuredDimension(measuredWidth, measuredHeight)
                }
            }
        } catch (e: Exception) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}