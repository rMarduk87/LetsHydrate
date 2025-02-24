package rpt.tool.letshydrate.utils.helpers


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
import rpt.tool.letshydrate.R
import rpt.tool.letshydrate.utils.AppUtils
import rpt.tool.letshydrate.utils.view.custom.MaterialProgressBar


class AlertHelper
    (var mContext: Context) {
    var pDialog: ProgressDialog? = null
    var dialog1: Dialog? = null

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


    fun Show_Progress_Dialog(msg: String?) {
        pDialog = ProgressDialog(mContext, 0)
        pDialog!!.setMessage(msg)
        pDialog!!.isIndeterminate = false
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    //    public void Close_Progress_Dialog()
    //    {
    //        if(pDialog!=null)
    //        pDialog.dismiss();
    //    }
    fun Show_Custom_Progress_Dialog() {
        dialog1 = Dialog(mContext, R.style.AppDialogTheme)
        dialog1!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog1!!.setContentView(R.layout.custom_progress_dialog)
        dialog1!!.setCancelable(false)

        val width = mContext.resources.displayMetrics.widthPixels - 80
        val height = mContext.resources.displayMetrics.heightPixels - 100

        dialog1!!.window!!.setLayout(width, height)
        val progress_wheel = dialog1!!.findViewById<MaterialProgressBar>(R.id.progress_wheel)
        //progress_wheel.setBarColor(mContext.getResources().getColor(R.color.colorPrimary));
        progress_wheel.setBarColor(Color.parseColor("#3D6B70"))

        dialog1!!.show()
    }

    fun Close_Custom_Progress_Dialog() {
        dialog1!!.dismiss()
    }

    fun is_show_Custom_Progress_Dialog(): Boolean {
        if (dialog1 != null) return dialog1!!.isShowing

        return false
    }

    fun Show_Error_Dialog(msg: String?) {
        if (AppUtils.DEVELOPER_MODE) Show_Alert_Dialog(msg)
    }
}
