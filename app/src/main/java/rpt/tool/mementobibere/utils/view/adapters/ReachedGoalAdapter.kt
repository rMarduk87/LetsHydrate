package rpt.tool.mementobibere.utils.view.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal
import rpt.tool.mementobibere.utils.extensions.toStringDate


class ReachedGoalAdapter(
    var mContext: Context, reachedGoalArrayList: MutableList<ReachedGoal>,
    var callBack: CallBack
) :
    RecyclerView.Adapter<ReachedGoalAdapter.ViewHolder>() {
    private val reachedGoalArrayList: MutableList<ReachedGoal> = reachedGoalArrayList

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.row_item_reached, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.lbl_date.text = reachedGoalArrayList[position].day!!.toStringDate()
        holder.qta_name.text = reachedGoalArrayList[position].quantity
        holder.lbl_unit.text = reachedGoalArrayList[position].unit

        if (position == 0) holder.super_item_block.setBackgroundColor(mContext.resources.getColor(R.color.colorPrimary))
        else holder.super_item_block.setBackgroundColor(mContext.resources.getColor(R.color.white))


        holder.divider.visibility = View.VISIBLE

        holder.item_block.setOnClickListener {
            callBack.onClickSelect(
                reachedGoalArrayList[position],
                position
            )
        }
    }

    override fun getItemCount(): Int {
        return reachedGoalArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var lbl_date: TextView = itemView.findViewById<TextView>(R.id.lbl_date)
        var item_block: LinearLayout = itemView.findViewById<LinearLayout>(R.id.item_block)
        var qta_name: TextView = itemView.findViewById<TextView>(R.id.quantity_name)
        var lbl_unit: TextView = itemView.findViewById<TextView>(R.id.lbl_unit)
        var divider: View = itemView.findViewById<View>(R.id.divider)
        var super_item_block: RelativeLayout =
            itemView.findViewById<RelativeLayout>(R.id.super_item_block)
    }

    interface CallBack {
        fun onClickSelect(reachedGoal: ReachedGoal?, position: Int)
    }


}