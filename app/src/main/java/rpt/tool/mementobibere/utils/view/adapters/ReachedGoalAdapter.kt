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
import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal


class ReachedGoalAdapter(
    var mContext: Context, reachedGoals: ArrayList<ReachedGoal>,
    var callBack: CallBack
) :
    RecyclerView.Adapter<ReachedGoalAdapter.ViewHolder?>() {
    private val reachedGoalArrayList: ArrayList<ReachedGoal> = reachedGoals

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return reachedGoalArrayList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.row_item_reached_goal, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.lbl_date.text = reachedGoalArrayList[position].day.toString()

        holder.container_name.text = reachedGoalArrayList[position].quantity.toString() + " ml" + " - " +
                reachedGoalArrayList[position].quantityOZ + " fl oz"

        if (position == 0) holder.super_item_block.setBackgroundColor(
            mContext.resources.getColor(R.color.colorPrimary))
        else holder.super_item_block.setBackgroundColor(mContext.resources.getColor(R.color.white))

        if (position != 0) {
            if (showHeader(position)) holder.item_header_block.visibility = View.VISIBLE
            else holder.item_header_block.visibility = View.GONE
        } else holder.item_header_block.visibility = View.VISIBLE

        holder.divider.visibility = View.VISIBLE

        Glide.with(mContext).load(getImage(position)).into(holder.imageView)

        holder.item_block.setOnClickListener {
            callBack.onClickSelect(
                reachedGoalArrayList[position],
                position
            )
        }

    }

    private fun showHeader(position: Int): Boolean {
        return !reachedGoalArrayList[position].day
            ?.equals(reachedGoalArrayList[position - 1].day)!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lbl_date: TextView = itemView.findViewById<TextView>(R.id.lbl_date)
        var imageView: ImageView =
            itemView.findViewById<ImageView>(R.id.container_img)
        var item_block: LinearLayout = itemView.findViewById<LinearLayout>(R.id.item_block)
        var item_header_block: LinearLayout =
            itemView.findViewById<LinearLayout>(R.id.item_header_block)
        var container_name: TextView = itemView.findViewById<TextView>(R.id.container_name)
        var divider: View = itemView.findViewById<View>(R.id.divider)
        var super_item_block: RelativeLayout =
            itemView.findViewById<RelativeLayout>(R.id.super_item_block)

    }

    interface CallBack {
        fun onClickSelect(reachedGoal: ReachedGoal, position: Int)
    }

    private fun getImage(pos: Int): Int {
        val drawable: Int = R.drawable.ic_dashboard_reached

        return drawable
    }
}