package rpt.tool.letshydrate.utils.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rpt.tool.letshydrate.R
import rpt.tool.letshydrate.utils.data.appmodel.Menu


@SuppressLint("NewApi")
class MenuAdapter(
    var mContext: Context,
    private val menu_name: ArrayList<Menu>,
    var callBack: CallBack
) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.row_item_menu, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = menu_name[position].menuName

        Glide.with(mContext).load(getImage(position)).into(holder.imageView)

        holder.item_block.setOnClickListener {
            callBack.onClickSelect(
                menu_name[position],
                position
            )
        }

        holder.selected_view.visibility = View.INVISIBLE

        if (position == 4 || position == 7) holder.lbl_divider.visibility = View.VISIBLE
        else holder.lbl_divider.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return menu_name.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById<TextView>(R.id.menu_name)
        var imageView: ImageView = itemView.findViewById<ImageView>(R.id.menu_img)
        var item_block: LinearLayout = itemView.findViewById<LinearLayout>(R.id.item_block)
        var selected_view: View = itemView.findViewById<View>(R.id.selected_view)
        var lbl_divider: View = itemView.findViewById<View>(R.id.lbl_divider)
    }

    interface CallBack {
        fun onClickSelect(menu: Menu?, position: Int)
    }

    fun getImage(pos: Int): Int {
        var drawable: Int = R.drawable.ic_menu_drink_water

        drawable = when (pos) {
            0 -> R.drawable.ic_menu_drink_water
            1 -> R.drawable.ic_menu_history
            2 -> R.drawable.ic_menu_report
            3 -> R.drawable.ic_menu_settings
            4 -> R.drawable.ic_menu_faq
            5 -> R.drawable.ic_privacypolicy
            6 -> R.drawable.ic_menu_share
            7 -> R.drawable.ic_menu_go_premium
            else -> R.drawable.ic_menu_drink_water
        }

        return drawable
    }
}