package rpt.tool.letshydrate.utils.helpers

import android.app.Activity;
import android.content.Context;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import rpt.tool.letshydrate.utils.AppUtils
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale
import java.util.regex.Pattern;

class StringHelper
    (var mContext: Context, act: Activity) {
    var act: Activity = act

    fun getMultiplePartParam(str: String?): StringBody? {
        var stringBody: StringBody? = null
        try {
            stringBody = StringBody(str, Charset.forName(HTTP.UTF_8))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return stringBody
    }


    fun getSqlValue(input: String): String {
        return input.replace("'", "''")
    }

    fun getSqlValue_reverse(input: String): String {
        return input.replace("\\", "")
    }

    fun getHtmlData(data: String): String {
        val head =
            "<head><style>@font-face {font-family: 'verdana';src: url('file:///android_asset/Roboto-Bold.ttf');}body {font-family: 'verdana';}</style></head>"
        val htmlData =
            "<html>$head<body style=\"text-align:justify\">$data</body></html>"
        return htmlData
    }

    fun getHtmlDataNormal(data: String): String {
        val head =
            "<head><style>@font-face {font-family: 'verdana';src: url('file:///android_asset/Roboto-Regular.ttf');}body {font-family: 'verdana';}</style></head>"
        val htmlData =
            "<html>$head<body style=\"text-align:justify\">$data</body></html>"
        return htmlData
    }

    fun getCommaSeparatedString(arr: ArrayList<String>): String {
        var lst_data = ""

        for (k in arr.indices) lst_data += arr[k] + ","

        if (!AppUtils.checkBlankData(lst_data)) lst_data = lst_data.substring(0, lst_data.length - 1)

        return lst_data
    }

    fun getCommaSeparatedString(arr: ArrayList<String>, sign: String): String {
        var lst_data = ""

        for (k in arr.indices) lst_data += arr[k] + sign

        if (!AppUtils.checkBlankData(lst_data)) lst_data = lst_data.substring(0, lst_data.length - 1)

        return lst_data
    }

    fun getArrayListFromCommaSeparatedString(comma_string: String): ArrayList<String> {
        val arr = ArrayList<String>()

        val str_arr =
            comma_string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (k in str_arr.indices) arr.add(str_arr[k])

        return arr
    }

    fun getArrayListFromCommaSeparatedString(
        comma_string: String,
        sign: String
    ): ArrayList<String> {
        val arr = ArrayList<String>()

        val str_arr =
            comma_string.split(sign.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (k in str_arr.indices) arr.add(str_arr[k])

        return arr
    }

    fun getArrayFromCommaSeparatedString(comma_string: String): Array<String> {
        val str_arr =
            comma_string.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        return str_arr
    }

    fun getArrayFromCommaSeparatedString(comma_string: String, sign: String): Array<String> {
        val str_arr =
            comma_string.split(sign.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        return str_arr
    }

    fun get_2_point(no: String): String {
        var no = no
        if (no.length == 1) no = "0$no"
        return no
    }

    fun get_2_digit_year(no: String): String {
        return no.substring(2)
    }

    fun firstLetterCaps(data: String): String {
        val firstLetter = data.substring(0, 1).uppercase(Locale.getDefault())
        val restLetters = data.substring(1).lowercase(Locale.getDefault())
        return firstLetter + restLetters
    }

    fun capitalize(capString: String): String {
        val capBuffer = StringBuffer()
        val capMatcher =
            Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString)
        while (capMatcher.find()) {
            capMatcher.appendReplacement(
                capBuffer,
                (capMatcher.group(1)?.uppercase(Locale.getDefault()) ?: "") + capMatcher.group(2)
                    ?.lowercase(
                        Locale.getDefault()
                    )
            )
        }

        return capMatcher.appendTail(capBuffer).toString()
    }

    fun get_string(id: Int): String {
        return mContext.resources.getString(id)
    }

    fun get_array(id: Int): Array<String> {
        return mContext.resources.getStringArray(id)
    }

    fun get_arraylist(id: Int): ArrayList<String> {
        val arr = ArrayList(Arrays.asList(*mContext.resources.getStringArray(id)))
        return arr
    }

    fun get_user_address(
        street: String?,
        city: String,
        state: String,
        zipcode: String,
        country: String
    ): String? {
        var final_address = street

        if (!(city == "" || city.isEmpty())) final_address += " , $city"

        if (!(state == "" || state.isEmpty())) final_address += " , $state"

        if (!(zipcode == "" || zipcode.isEmpty())) final_address += " - $zipcode"

        if (!(country == "" || country.isEmpty())) final_address += " , $country"

        return final_address
    }
}
