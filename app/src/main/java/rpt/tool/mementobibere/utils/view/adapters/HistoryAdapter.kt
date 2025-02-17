package rpt.tool.mementobibere.utils.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.data.appmodel.History

@SuppressLint("NewApi")
class HistoryAdapter(
    var mContext: Context, historyArrayList: ArrayList<History>,
    var callBack: CallBack
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder?>() {
    private val historyArrayList: ArrayList<History> = historyArrayList

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return historyArrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.row_item_history, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.lbl_date.text = historyArrayList[position].drinkDate
        holder.lbl_total_day_water.text = historyArrayList[position].totalML

        val str = " " + historyArrayList[position].containerMeasure

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) holder.container_name.text =
            historyArrayList[position].containerValue + str
        else holder.container_name.text = historyArrayList[position].containerValueOZ + str
        holder.lbl_time.text = historyArrayList[position].drinkTime

        if (position == 0) holder.super_item_block.setBackgroundColor(mContext.resources.getColor(R.color.colorPrimary))
        else holder.super_item_block.setBackgroundColor(mContext.resources.getColor(R.color.white))

        if (position != 0) {
            if (showHeader(position)) holder.item_header_block.visibility = View.VISIBLE
            else holder.item_header_block.visibility = View.GONE
        } else holder.item_header_block.visibility = View.VISIBLE

        holder.divider.visibility = View.VISIBLE

        Glide.with(mContext).load(getImage(position)).into(holder.imageView)

        holder.item_block.setOnClickListener {
            callBack.onClickSelect(
                historyArrayList[position],
                position
            )
        }

        holder.btnRemoveRow.setOnClickListener {
            callBack.onClickRemove(
                historyArrayList[position],
                position
            )
        }
    }

    fun showHeader(position: Int): Boolean {
        return !historyArrayList[position].drinkDate
            .equals(historyArrayList[position - 1].drinkDate)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lbl_date: TextView = itemView.findViewById<TextView>(R.id.lbl_date)
        var lbl_total_day_water: TextView =
            itemView.findViewById<TextView>(R.id.lbl_total_day_water)
        var imageView: ImageView =
            itemView.findViewById<ImageView>(R.id.container_img)
        var item_block: LinearLayout = itemView.findViewById<LinearLayout>(R.id.item_block)
        var item_header_block: LinearLayout =
            itemView.findViewById<LinearLayout>(R.id.item_header_block)
        var container_name: TextView = itemView.findViewById<TextView>(R.id.container_name)
        var lbl_time: TextView = itemView.findViewById<TextView>(R.id.lbl_time)
        var divider: View = itemView.findViewById<View>(R.id.divider)
        var super_item_block: RelativeLayout =
            itemView.findViewById<RelativeLayout>(R.id.super_item_block)

        var btnRemoveRow: ImageView = itemView.findViewById<ImageView>(R.id.btnRemoveRow)
    }

    interface CallBack {
        fun onClickSelect(history: History?, position: Int)
        fun onClickRemove(history: History?, position: Int)
    }

    fun getImage(pos: Int): Int {
        var drawable: Int = R.drawable.ic_custom_ml

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
            val `val`: String = historyArrayList[pos].containerValue.toString()

            if (`val`.toFloat() == 50f) drawable = R.drawable.ic_50_ml
            else if (`val`.toFloat() == 100f) drawable = R.drawable.ic_100_ml
            else if (`val`.toFloat() == 150f) drawable = R.drawable.ic_150_ml
            else if (`val`.toFloat() == 200f) drawable = R.drawable.ic_200_ml
            else if (`val`.toFloat() == 250f) drawable = R.drawable.ic_250_ml
            else if (`val`.toFloat() == 300f) drawable = R.drawable.ic_300_ml
            else if (`val`.toFloat() == 500f) drawable = R.drawable.ic_500_ml
            else if (`val`.toFloat() == 600f) drawable = R.drawable.ic_600_ml
            else if (`val`.toFloat() == 700f) drawable = R.drawable.ic_700_ml
            else if (`val`.toFloat() == 800f) drawable = R.drawable.ic_800_ml
            else if (`val`.toFloat() == 900f) drawable = R.drawable.ic_900_ml
            else if (`val`.toFloat() == 1000f) drawable = R.drawable.ic_1000_ml
        } else {
            val `val`: String = historyArrayList[pos].containerValueOZ.toString()

            if (`val`.toFloat() == 1.6907f) drawable = R.drawable.ic_50_ml
            else if (`val`.toFloat() == 3.3814f) drawable = R.drawable.ic_100_ml
            else if (`val`.toFloat() == 5.0721f) drawable = R.drawable.ic_150_ml
            else if (`val`.toFloat() == 6.7628f) drawable = R.drawable.ic_200_ml
            else if (`val`.toFloat() == 8.45351f) drawable = R.drawable.ic_250_ml
            else if (`val`.toFloat() == 10.1442f) drawable = R.drawable.ic_300_ml
            else if (`val`.toFloat() == 16.907f) drawable = R.drawable.ic_500_ml
            else if (`val`.toFloat() == 20.2884f) drawable = R.drawable.ic_600_ml
            else if (`val`.toFloat() == 23.6698f) drawable = R.drawable.ic_700_ml
            else if (`val`.toFloat() == 27.0512f) drawable = R.drawable.ic_800_ml
            else if (`val`.toFloat() == 30.4326f) drawable = R.drawable.ic_900_ml
            else if (`val`.toFloat() == 33.814f) drawable = R.drawable.ic_1000_ml
        }

        return drawable
    }
}