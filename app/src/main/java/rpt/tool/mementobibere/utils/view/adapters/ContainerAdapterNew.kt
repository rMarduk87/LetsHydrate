package rpt.tool.mementobibere.utils.view.adapters

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
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.data.appmodel.Container
import rpt.tool.mementobibere.utils.log.e

@SuppressLint("NewApi")
class ContainerAdapterNew(
    var mContext: Context, containerArrayList: MutableList<Container>,
    private val callBack: CallBack
) :
    RecyclerView.Adapter<ContainerAdapterNew.ViewHolder?>() {
    private val containerArrayList: MutableList<Container> = containerArrayList

    override fun getItemId(position: Int): Long {
        return containerArrayList.size.toLong()
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.row_item_container, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
                if (containerArrayList[position].containerValue!!
                        .contains(".")
                ) holder.textView.text = AppUtils.decimalFormat.format(
                    containerArrayList[position].containerValue!!.toDouble()
                ) + " " + AppUtils.WATER_UNIT_VALUE
                else holder.textView.text = containerArrayList[position].containerValue + " " +
                        AppUtils.WATER_UNIT_VALUE
            } else {
                if (containerArrayList[position].containerValue!!
                        .contains(".")
                ) holder.textView.text = AppUtils.decimalFormat.format(
                    containerArrayList[position].containerValueOZ!!.toDouble()
                ) + " " + AppUtils.WATER_UNIT_VALUE
                else holder.textView.text = containerArrayList[position].containerValueOZ +
                        " " + AppUtils.WATER_UNIT_VALUE
            }
        } catch (e: Exception) {
            e.message?.let { e(Throwable(e), it) }
        }

        if (containerArrayList[position].isCustom) Glide.with(mContext)
            .load(R.drawable.ic_custom_ml).into(holder.imageView)
        else Glide.with(mContext).load(getImage(position)).into(holder.imageView)

        holder.item_block.setOnClickListener {
            callBack.onClickSelect(
                containerArrayList[position],
                position
            )
        }

        if (containerArrayList[position].isSelected) {
            holder.img_selected.visibility = View.VISIBLE
        } else {
            holder.img_selected.visibility = View.INVISIBLE
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById<TextView>(R.id.container_name)
        var imageView: ImageView =
            itemView.findViewById<ImageView>(R.id.container_img)
        var item_block: LinearLayout = itemView.findViewById<LinearLayout>(R.id.item_block)
        var img_selected: ImageView =
            itemView.findViewById<ImageView>(R.id.img_selected)
    }

    interface CallBack {
        fun onClickSelect(container: Container?, position: Int)
    }

    fun getImage(pos: Int): Int {
        var drawable: Int = R.drawable.ic_custom_ml

        if (AppUtils.WATER_UNIT_VALUE.equals("ml",true)) {
            if (containerArrayList[pos].containerValue!!.toFloat() == 50f) drawable =
                R.drawable.ic_50_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 100f) drawable =
                R.drawable.ic_100_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 150f) drawable =
                R.drawable.ic_150_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 200f) drawable =
                R.drawable.ic_200_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 250f) drawable =
                R.drawable.ic_250_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 300f) drawable =
                R.drawable.ic_300_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 500f) drawable =
                R.drawable.ic_500_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 600f) drawable =
                R.drawable.ic_600_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 700f) drawable =
                R.drawable.ic_700_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 800f) drawable =
                R.drawable.ic_800_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 900f) drawable =
                R.drawable.ic_900_ml
            else if (containerArrayList[pos].containerValue!!.toFloat() == 1000f) drawable =
                R.drawable.ic_1000_ml
        } else {
            if (containerArrayList[pos].containerValueOZ!!.toFloat() == 1.6907f) drawable =
                R.drawable.ic_50_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 3.3814f) drawable =
                R.drawable.ic_100_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 5.0721f) drawable =
                R.drawable.ic_150_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 6.7628f) drawable =
                R.drawable.ic_200_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 8.45351f) drawable =
                R.drawable.ic_250_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 10.1442f) drawable =
                R.drawable.ic_300_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 16.907f) drawable =
                R.drawable.ic_500_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 20.2884f) drawable =
                R.drawable.ic_600_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 23.6698f) drawable =
                R.drawable.ic_700_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 27.0512f) drawable =
                R.drawable.ic_800_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 30.4326f) drawable =
                R.drawable.ic_900_ml
            else if (containerArrayList[pos].containerValueOZ!!.toFloat() == 33.814f) drawable =
                R.drawable.ic_1000_ml
        }

        return drawable
    }
}

