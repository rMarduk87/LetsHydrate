package rpt.tool.mementobibere.ui.libraries.menu

import androidx.annotation.ColorInt
import androidx.annotation.IdRes

interface MenuItem {

    @get:IdRes
    val id: Int

    val text: CharSequence

    @get:ColorInt
    val activeColor: Int

    val isShown: Boolean

    val isAttached: Boolean

    /**
     * Shows item
     */
    fun show()

    /**
     * Hides item
     */
    fun hide()

    fun notification(): Notification

}
