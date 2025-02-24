package rpt.tool.mementobibere.utils.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import rpt.tool.mementobibere.R

class MyPageAdapter(var mContext: Context) : PagerAdapter() {
    var resId: Int = 0

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        when (position) {
            0 -> resId = R.layout.screen_battery_optimize_one
            1 -> resId = R.layout.screen_battery_optimize_two
        }
        val layout: ViewGroup = inflater.inflate(resId, collection, false) as ViewGroup
        collection.addView(layout)
        return layout
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }
}