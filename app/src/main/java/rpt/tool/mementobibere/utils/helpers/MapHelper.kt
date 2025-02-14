package rpt.tool.mementobibere.utils.helpers

import android.annotation.SuppressLint
import java.util.Locale
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class MapHelper {
    @SuppressLint("DefaultLocale")
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): String {
        var unit = unit
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1)) * sin(deg2rad(lat2))
                + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta)))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515

        //return (dist);
        unit = unit.uppercase(Locale.getDefault())

        var s = ""

        s = if (unit == "K") String.format("%.2f", (dist * 1.609344))
        else if (unit == "N") String.format("%.2f", (dist * 0.8684))
        else String.format("%.2f", dist)

        return s
    }

    @SuppressLint("DefaultLocale")
    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1)) * sin(deg2rad(lat2))
                + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta)))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515

        val s = String.format("%.2f", dist)

        return s
    }

    fun deg2rad(deg: Double): Double {
        return (deg * Math.PI / 180.0)
    }

    fun rad2deg(rad: Double): Double {
        return (rad * 180.0 / Math.PI)
    }
}