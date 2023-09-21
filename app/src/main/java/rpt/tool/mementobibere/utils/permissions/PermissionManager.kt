package rpt.tool.mementobibere.utils.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.permissions.dispatcher.RequestResultsDispatcher
import rpt.tool.mementobibere.utils.permissions.dispatcher.dsl.PermissionDispatcherDsl

class PermissionManager(val activity: Activity) {

    lateinit var dispatcher: RequestResultsDispatcher
        private set


    @PermissionDispatcherDsl
    infix fun buildRequestResultsDispatcher(init: RequestResultsDispatcher.() -> Unit) {
        dispatcher = RequestResultsDispatcher(this)
        dispatcher.apply(init)
    }


    infix fun checkRequestAndDispatch(requestCode: Int) {
        checkRequestAndDispatch(requestCode, false)
    }


    internal fun checkRequestAndDispatch(requestCode: Int, comingFromRationale: Boolean = false) {
        val permissionsNotGranted = dispatcher.getPermissions(requestCode)?.filter { permission ->
            ActivityCompat.checkSelfPermission(
                activity, permission
            ) != PackageManager.PERMISSION_GRANTED
        }?.toTypedArray() ?: throw UnhandledRequestCodeException(requestCode, activity)

        if (permissionsNotGranted.isEmpty()) {
            // All permissions are granted
            dispatcher.dispatchOnGranted(requestCode)
        } else {
            // Some permissions are not granted
            dispatchSomePermissionsNotGranted(
                permissionsNotGranted,
                requestCode,
                comingFromRationale
            )
        }
    }

    private fun dispatchSomePermissionsNotGranted(
        permissionsNotGranted: Array<out String>,
        requestCode: Int,
        comingFromRationale: Boolean
    ) {
        // if not coming from rationale, gets the list of permissions that require rationale to be shown
        val permissionsRequiringRationale =
            if (!comingFromRationale) getPermissionsRequiringRationale(permissionsNotGranted) else emptyList()

        // if some permissions require rationale, show rationale, otherwise ask for permissions
        // Note: if coming from rationale, the list will be empty, so the else branch will be executed
        if (permissionsRequiringRationale.isNotEmpty()) {
            dispatcher.showRationale(requestCode, permissionsRequiringRationale)
        } else {
            ActivityCompat.requestPermissions(activity, permissionsNotGranted, requestCode)
        }
    }

    private fun getPermissionsRequiringRationale(permissionsNotGranted: Array<out String>) =
        permissionsNotGranted.filter { permission ->
            shouldShowRequestPermissionRationale(activity, permission)
        }.toList()


    fun dispatchOnRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
    ) {
        dispatcher.dispatchAction(requestCode, grantResults)?.invoke()
    }

}


class UnhandledRequestCodeException(requestCode: Int, context: Context) : Throwable() {
    override val message: String =
        context.getString(R.string.unhandled_request_code_exception_message, requestCode)
}