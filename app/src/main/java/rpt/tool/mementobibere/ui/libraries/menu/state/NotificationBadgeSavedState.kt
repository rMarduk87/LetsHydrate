package rpt.tool.mementobibere.ui.libraries.menu.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class NotificationBadgeSavedState(
    val badgeColor: Int,
    val badgeTextColor: Int,
    val badgeText: String?,
    val shouldShowBadge: Boolean,
    val superState: Parcelable?
): Parcelable
