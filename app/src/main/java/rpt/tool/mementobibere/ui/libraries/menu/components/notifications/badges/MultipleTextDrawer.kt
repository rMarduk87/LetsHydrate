package rpt.tool.mementobibere.ui.libraries.menu.components.notifications.badges

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.ColorInt
import androidx.annotation.Px
import rpt.tool.mementobibere.ui.libraries.menu.utils.max
import rpt.tool.mementobibere.ui.libraries.menu.utils.toPx

internal class MultipleTextDrawer(
    @ColorInt private val textColor: Int,
    text: String
) : CharacterDrawer(text) {

    private val buffer = RectF()

    override fun draw(paint: Paint, viewBounds: RectF, canvas: Canvas) {
        val width = getTextWidth(paint)
        val height = getTextHeight(paint)

        val topRightX = viewBounds.right
        val topRightY = viewBounds.top

        val radius = max(width, height) / 2F + max(getVerticalPadding(), getHorizontalPadding())

        val centerX = topRightX - radius / 2F
        val centerY = topRightY + radius / 2F

        buffer.set(
            centerX - width / 2F - getHorizontalPadding(), //left
            centerY - height / 2F - getVerticalPadding(), //top
            centerX + width / 2F + getHorizontalPadding(), //right
            centerY + height / 2F + getVerticalPadding() //bottom
        )

        canvas.drawRoundRect(buffer, getCornerRoundRadius(), getCornerRoundRadius(), paint)

        val oldColor = paint.color

        paint.color = textColor
        canvas.drawText(text,
            centerX,
            centerY + getTextOffset(paint),
            paint
        )

        paint.color = oldColor
    }

    override fun getHorizontalPadding(): Float {
        return 3F.toPx()
    }

    @Px
    private fun getCornerRoundRadius(): Float {
        return 8F.toPx()
    }

}
