package rpt.tool.mementobibere.utils.helpers


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.view.custom.MaterialProgressBar


class AlertHelper
    (var mContext: Context) {

    fun Show_Alert_Dialog(msg: String?) {
        val ad = AlertDialog.Builder(mContext)

        ad.setTitle("Info")
        ad.setIcon(android.R.drawable.ic_dialog_info)
        ad.setPositiveButton("Close", null)
        ad.setMessage(msg)
        ad.show()
    }

    fun alert(msg: String?) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("InflateParams")
    fun customAlert(msg: String?) {
        // Get the custom layout view.
        val layoutInflater = LayoutInflater.from(mContext)
        val toastView: View = layoutInflater.inflate(R.layout.activity_toast_custom_view, null)
        val customToastText = toastView.findViewById<TextView>(R.id.customToastText)
        customToastText.text = msg

        // Initiate the Toast instance.
        val toast = Toast(mContext)
        // Set custom view in toast.
        toast.setView(toastView)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM, 0, 40)
        toast.show()
    }


    fun Show_Error_Dialog(msg: String?) {
        if (AppUtils.DEVELOPER_MODE) Show_Alert_Dialog(msg)
    }
}
