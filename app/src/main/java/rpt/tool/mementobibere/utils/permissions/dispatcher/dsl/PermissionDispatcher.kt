package rpt.tool.mementobibere.utils.permissions.dispatcher.dsl

import androidx.appcompat.app.AlertDialog
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.permissions.dispatcher.DispatcherEntry
import rpt.tool.mementobibere.utils.permissions.dispatcher.RequestResultsDispatcher


@DslMarker
annotation class PermissionDispatcherDsl

@PermissionDispatcherDsl
abstract class PermissionDispatcher


@PermissionDispatcherDsl
infix fun DispatcherEntry.checkPermissions(permissions: Array<out String>) {
    this.setPermissions(permissions)
}

@JvmName("checkPermissionsVararg")
@PermissionDispatcherDsl
fun DispatcherEntry.checkPermissions(vararg permissions: String) {
    this.setPermissions(arrayOf(*permissions))
}


@PermissionDispatcherDsl
fun DispatcherEntry.doOnGranted(onGranted: () -> Unit) {
    this.onGranted = onGranted
}


@PermissionDispatcherDsl
fun DispatcherEntry.doOnDenied(onDenied: () -> Unit) {
    this.onDenied = onDenied
}

@PermissionDispatcherDsl
fun DispatcherEntry.rationale(onShowRationale: (List<String>, Int) -> Unit) {
    this.onShowRationale = onShowRationale
}

@PermissionDispatcherDsl
fun DispatcherEntry.showRationaleDialog(
    message: String,
    positiveButtonText: String = "",
    negativeButtonText: String = "",
    onNegativeButtonPressed: (() -> Unit) = {}
) {
    val actualPositiveButtonText =
        positiveButtonText.ifEmpty {
            dispatcher.manager.activity.getString(
                R.string.rationale_dialog_ok
            )
        }
    val actualNegativeButtonText = negativeButtonText.ifEmpty {
        dispatcher.manager.activity.getString(
            R.string.rationale_dialog_cancel
        )
    }

    rationale { _, _ ->
        val manager = dispatcher.manager
        manager.activity.runOnUiThread {
            AlertDialog.Builder(manager.activity).setMessage(message)
                .setPositiveButton(actualPositiveButtonText) { _, _ ->
                    manager.checkRequestAndDispatch(requestCode, comingFromRationale = true)
                }.setNegativeButton(actualNegativeButtonText) { _, _ ->
                    onNegativeButtonPressed.invoke()
                }.show()
        }
    }
}

@PermissionDispatcherDsl
fun DispatcherEntry.showRationaleDialog(
    dialog: AlertDialog,
) {
    rationale { _, _ ->
        dispatcher.manager.activity.runOnUiThread {
            dialog.show()
        }
    }
}


@PermissionDispatcherDsl
fun RequestResultsDispatcher.withRequestCode(
    requestCode: Int, init: DispatcherEntry.() -> Unit
) {
    entries[requestCode] = DispatcherEntry(manager.dispatcher, requestCode).apply(init)
}


@PermissionDispatcherDsl
fun RequestResultsDispatcher.removeEntry(requestCode: Int) {
    entries.remove(requestCode)
}

@PermissionDispatcherDsl
fun RequestResultsDispatcher.addEntry(requestCode: Int, init: DispatcherEntry.() -> Unit) {
    if (entries[requestCode] == null) {
        entries[requestCode] = DispatcherEntry(manager.dispatcher, requestCode).apply(init)
    }
}


@PermissionDispatcherDsl
fun RequestResultsDispatcher.replaceEntryOnGranted(
    requestCode: Int, onGranted: () -> Unit
) {
    entries[requestCode]?.doOnGranted(onGranted)
}

@PermissionDispatcherDsl
fun RequestResultsDispatcher.replaceEntryOnDenied(requestCode: Int, onDenied: () -> Unit) {
    entries[requestCode]?.doOnDenied(onDenied)
}

@PermissionDispatcherDsl
fun RequestResultsDispatcher.replaceEntry(
    requestCode: Int, init: DispatcherEntry.() -> Unit
) {
    entries[requestCode] = DispatcherEntry(manager.dispatcher, requestCode).apply(init)
}