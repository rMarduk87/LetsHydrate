package rpt.tool.mementobibere.utils.permissions.dispatcher

import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.PermissionDispatcher


class DispatcherEntry(
    internal val dispatcher: RequestResultsDispatcher, val requestCode: Int
) : PermissionDispatcher() {

    private lateinit var permissions: Array<out String>


    var onGranted: () -> Unit = {}
        internal set


    var onDenied: () -> Unit = {}
        internal set


    var onShowRationale: ((List<String>, requestCode: Int) -> Unit)? = null
        internal set

    internal fun setPermissions(permissions: Array<out String>) {
        this.permissions = permissions
    }

    internal fun getPermissions(): Array<out String> = permissions

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DispatcherEntry

        if (!permissions.contentEquals(other.permissions)) return false
        if (onGranted != other.onGranted) return false
        if (onDenied != other.onDenied) return false

        return true
    }

    override fun hashCode(): Int {
        var result = permissions.contentHashCode()
        result = 31 * result + onGranted.hashCode()
        result = 31 * result + onDenied.hashCode()
        return result
    }
}