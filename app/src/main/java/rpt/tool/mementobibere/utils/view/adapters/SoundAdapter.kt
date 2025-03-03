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
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.data.appmodel.SoundModel

@SuppressLint("NewApi")
class SoundAdapter(
    var mContext: Context, sounds: List<SoundModel>,
    var callBack: CallBack
) :
    RecyclerView.Adapter<SoundAdapter.ViewHolder?>() {
    private val sounds: List<SoundModel> = sounds

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return sounds.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.row_item_sound, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.lbl_sound_name.text = sounds[position].name

        if (sounds[position].isSelected) {
            holder.img_selected.visibility = View.VISIBLE
            holder.item_block.background
                .setTint(mContext.resources.getColor(R.color.colorPrimary))
        } else {
            holder.img_selected.visibility = View.INVISIBLE
            holder.item_block.background.setTint(mContext.resources.getColor(R.color.white))
        }

        holder.item_block.setOnClickListener {
            callBack.onClickSelect(
                sounds[position],
                position
            )
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_selected: ImageView =
            itemView.findViewById<ImageView>(R.id.img_selected)
        var item_block: LinearLayout = itemView.findViewById<LinearLayout>(R.id.item_block)
        var lbl_sound_name: TextView = itemView.findViewById<TextView>(R.id.lbl_sound_name)
    }

    interface CallBack {
        fun onClickSelect(time: SoundModel?, position: Int)
    }
}

