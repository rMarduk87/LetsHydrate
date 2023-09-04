package rpt.tool.mementobibere.ui.libraries.menu.components.notifications.badges

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

internal class NoOpDrawer: BadgeDrawer {

    override fun draw(
        paint: Paint,
        viewBounds: RectF,
        canvas: Canvas
    ) {
        // empty on purpose
    }

}
