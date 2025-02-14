package rpt.tool.mementobibere.utils.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.view.custom.ClickSpan
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.pow
import kotlin.math.sqrt


class UtilityFunction
    (var mContext: Context, var act: Activity) {
    var sh: StringHelper = StringHelper(mContext, act)

    fun permission_StrictMode() {
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    fun permission_VMStrictMode() {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }

    fun hideKeyboard() {
        // Check if no view has focus:

        val view = act.currentFocus
        if (view != null) {
            val inputManager =
                act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun clickify_read_more_click(txt: TextView, read_more: View, str: String) {
        read_more.setOnClickListener { //txt.setText(str+" Read Less");
            //txt.setText(Html.fromHtml("<html><body>" + str.toString().trim() + " Read Less " + "</body></html>"));

            val des = Html.fromHtml(
                str.toString().trim { it <= ' ' } + " " + mContext.getString(R.string.str_read_less))
                .toString()
            txt.text = Html.fromHtml(des)

            txt.maxLines = Int.MAX_VALUE //your TextView
            txt.isSingleLine = false
            txt.ellipsize = null
            read_more.visibility = View.GONE
            clickify_read_less_click(txt, read_more, str)
        }
    }

    fun clickify_read_less_click(txt: TextView, read_more: View, str: String) {
        clickify(
            txt,
            "" + mContext.getString(R.string.str_read_less),
            object : ClickSpan.OnClickListener {
                override fun onClick() {
                    try {
                        txt.maxLines = 1 //your TextView
                        txt.isSingleLine = true

                        val des = Html.fromHtml(str.toString().trim { it <= ' ' }).toString()
                        txt.text = Html.fromHtml(des)

                        read_more.visibility = View.VISIBLE
                        txt.ellipsize = TextUtils.TruncateAt.END
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    fun clickify(view: TextView, clickableText: String, listener: ClickSpan.OnClickListener?) {
        val text = view.text
        val string = text.toString()
        val span = ClickSpan(listener!!)

        val start = string.indexOf(clickableText)
        val end = start + clickableText.length
        if (start == -1) return

        if (text is Spannable) {
            text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        } else {
            val s = SpannableString.valueOf(text)
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            view.text = s
        }

        val m = view.movementMethod
        if ((m == null) || m !is LinkMovementMethod) {
            view.movementMethod = LinkMovementMethod.getInstance()
        }
    }


    fun share_wish(str_subject: String?, str_link: String?) {
        val ih: IntentHelper = IntentHelper(mContext, act)
        if (str_subject != null) {
            if (str_link != null) {
                ih.CustomShare(str_subject, str_link)
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        var isValidEmail = false
        val inputStr: CharSequence = email
        val matcher = emailPattern.matcher(inputStr)
        if (matcher.matches()) {
            isValidEmail = true
        }

        return isValidEmail
    }

    fun isValidGSTNO(no: String): Boolean {
        var isValidEmail = false
        val inputStr: CharSequence = no
        val matcher = gstPattern.matcher(inputStr)
        if (matcher.matches()) {
            isValidEmail = true
        }

        return isValidEmail
    }

    fun generateNoteOnSD(sFileName: String?, sBody: String?) {
        try {
            val root = File(Environment.getExternalStorageDirectory(), "Notes")
            if (!root.exists()) {
                root.mkdirs()
            }
            val gpxfile = sFileName?.let { File(root, it) }
            val writer = FileWriter(gpxfile)
            writer.append(sBody)
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadbanner() {
        /*AdView mAdView = (AdView) act.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);*/
    }

    fun setLabelFont(font_name: String?, lbl_set: TextView) {
        val lbl_tf = Typeface.createFromAsset(mContext.assets, font_name)
        lbl_set.typeface = lbl_tf
    }

    val isTablet: Boolean
        get() = (mContext.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE

    val iMEI: String
        @SuppressLint("HardwareIds")
        get() {
            val mngr = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            @SuppressLint("MissingPermission") val imei = mngr.deviceId
            return imei
        }

    fun check_Enable_GPS() {
        val manager = act.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            CheckEnableGPS()
        }
    }

    fun CheckEnableGPS() {
        val builder = AlertDialog.Builder(act)
        val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        builder.setCancelable(false)
        builder.setMessage(act.resources.getString(R.string.open_gps_dialog))
            .setPositiveButton(
                act.resources.getString(R.string.dialog_ok_button)
            ) { d, id ->
                d.cancel()
                act.startActivity(Intent(action))
            }
            .setNegativeButton(
                act.resources.getString(R.string.dialog_cancel_button)
            ) { d, id -> d.cancel() }
        builder.create().show()
    }

    fun turnGPSOn() {
        val intent = Intent("android.location.GPS_ENABLED_CHANGE")
        intent.putExtra("enabled", true)
        act.sendBroadcast(intent)
    }

    fun turnGPSOff() {
        //Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        //intent.putExtra("enabled", false);
        //act.sendBroadcast(intent);
    }

    fun chkinternet(): Boolean {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @SuppressLint("MissingPermission") val i =
            connectivityManager.activeNetworkInfo ?: return false
        if (!i.isConnected) return false
        if (!i.isAvailable) return false

        return true
    }

    @SuppressLint("DefaultLocale")
    fun get_screen_size(): Double {
        val dm = DisplayMetrics()
        act.windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        val dens = dm.densityDpi
        val wi = width.toDouble() / dens.toDouble()
        val hi = height.toDouble() / dens.toDouble()
        val x = wi.pow(2.0)
        val y = hi.pow(2.0)
        val screenInches = sqrt(x + y)
        val screenInformation = String.format("%.2f", screenInches)
        return ("" + screenInformation).toDouble()
    }

    fun GetCountryZipCode(): String {
        var CountryID = ""
        var CountryZipCode = ""

        val manager = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //getNetworkCountryIso
        CountryID = manager.simCountryIso.uppercase(Locale.getDefault())
        val rl = mContext.resources.getStringArray(R.array.CountryCodes)
        for (i in rl.indices) {
            val g = rl[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (g[1].trim { it <= ' ' } == CountryID.trim { it <= ' ' }) {
                CountryZipCode = g[0]
                break
            }
        }
        return "+$CountryZipCode"
    }

    companion object {
        //email validation pattern
        val emailPattern: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        //email GST NO pattern
        val gstPattern: Pattern = Pattern.compile(
            "\\d{2}[a-zA-Z]{5}\\d{4}[a-zA-Z]{1}[a-zA-Z\\d]{1}[Z]{1}[a-zA-Z\\d]{1}"
        )


        fun getYoutubeThumbnailUrlFromVideoUrl(videoUrl: String): String {
            val imgUrl = "http://img.youtube.com/vi/" + extractVideoIdFromUrl(videoUrl) + "/0.jpg"
            return imgUrl
        }

        fun extractVideoIdFromUrl(url: String): String? {
            val youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(url)

            for (regex in AppUtils.videoIdRegex) {
                val compiledPattern = Pattern.compile(regex)
                val matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain)

                if (matcher.find()) {
                    return matcher.group(1)
                }
            }

            return null
        }

        private fun youTubeLinkWithoutProtocolAndDomain(url: String): String {
            val compiledPattern = Pattern.compile(AppUtils.youTubeUrlRegEx)
            val matcher = compiledPattern.matcher(url)

            if (matcher.find()) {
                return url.replace(matcher.group(), "")
            }
            return url
        }
    }
}