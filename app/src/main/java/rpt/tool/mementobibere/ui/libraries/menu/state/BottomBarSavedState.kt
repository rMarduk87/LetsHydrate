package rpt.tool.mementobibere.ui.libraries.menu.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class BottomBarSavedState(
    val selectedItem: Int?,
    val superState: Parcelable?
): Parcelable
