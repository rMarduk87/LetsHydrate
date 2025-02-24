package rpt.tool.mementobibere.utils.view.adapters

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import rpt.tool.mementobibere.R

class CustomShareAdapter(ctx: Context, pm: PackageManager?, apps: List<ResolveInfo?>?) :
    ArrayAdapter<ResolveInfo?>(ctx, R.layout.custom_share_list, apps!!) {
    private var pm: PackageManager? = null


    var appName: ArrayList<String> = ArrayList()

    init {
        this.pm = pm
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = newView(parent)
        }

        bindView(position, convertView)

        return (convertView)
    }

    private fun newView(parent: ViewGroup): View {
        val li: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return (li.inflate(R.layout.custom_share_list, parent, false))
    }

    fun get_app_name(position: Int): String {
        return "" + pm?.let { getItem(position)!!.loadLabel(it) }
    }

    private fun bindView(position: Int, row: View) {
        val label: TextView = row.findViewById<View>(R.id.label) as TextView
        val icon = row.findViewById<View>(R.id.icon) as ImageView

        appName.add("" + pm?.let { getItem(position)!!.loadLabel(it) })

        label.text = pm?.let { getItem(position)!!.loadLabel(it) }
        icon.setImageDrawable(getItem(position)!!.loadIcon(pm))
    }
}
