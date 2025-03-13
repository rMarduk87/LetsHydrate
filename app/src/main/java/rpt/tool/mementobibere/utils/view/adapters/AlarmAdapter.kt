package rpt.tool.mementobibere.utils.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.data.appmodel.AlarmModel
import java.util.Locale


@SuppressLint("NewApi")
class AlarmAdapter(
    var mContext: Context,
    private val alarmList: MutableList<AlarmModel>,
    var callBack: CallBack
) :
    RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.row_item_alarm, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.lbl_time.text = alarmList[position].drinkTime

        if (alarmList[position].alarmType.equals("R", ignoreCase = true)) {
            holder.lbl_time.text = """${holder.lbl_time.text}
${mContext.resources.getString(R.string.str_every)} ${alarmList[position].alarmInterval} ${
                mContext.resources.getString(
                    R.string.str_minutes
                ).lowercase(Locale.getDefault())
            }"""
        }

        holder.item_block.setOnClickListener {
            callBack.onClickSelect(
                alarmList[position],
                position
            )
        }

        holder.img_remove.setOnClickListener { view ->
            showMenu(
                view,
                alarmList[position],
                position
            )
        }

        holder.switch_reminder.isChecked = alarmList[position].isOff != 1

        holder.switch_reminder.setOnCheckedChangeListener { buttonView, isChecked ->
            callBack.onClickSwitch(
                alarmList[position],
                position,
                isChecked
            )
        }


        holder.chk_sunday.isChecked = alarmList[position].sunday == 1
        holder.chk_monday.isChecked = alarmList[position].monday == 1
        holder.chk_tuesday.isChecked = alarmList[position].tuesday == 1
        holder.chk_wednesday.isChecked = alarmList[position].wednesday == 1
        holder.chk_thursday.isChecked = alarmList[position].thursday == 1
        holder.chk_friday.isChecked = alarmList[position].friday == 1
        holder.chk_saturday.isChecked = alarmList[position].saturday == 1

        holder.chk_sunday.setOnClickListener {
            callBack.onClickWeek(
                alarmList[position],
                position,
                0,
                holder.chk_sunday.isChecked
            )
        }

        holder.chk_monday.setOnClickListener {
            callBack.onClickWeek(
                alarmList[position],
                position,
                1,
                holder.chk_monday.isChecked
            )
        }

        holder.chk_tuesday.setOnClickListener {
            callBack.onClickWeek(
                alarmList[position],
                position,
                2,
                holder.chk_tuesday.isChecked
            )
        }

        holder.chk_wednesday.setOnClickListener {
            callBack.onClickWeek(
                alarmList[position],
                position,
                3,
                holder.chk_wednesday.isChecked
            )
        }

        holder.chk_thursday.setOnClickListener {
            callBack.onClickWeek(
                alarmList[position],
                position,
                4,
                holder.chk_thursday.isChecked
            )
        }

        holder.chk_friday.setOnClickListener {
            callBack.onClickWeek(
                alarmList[position],
                position,
                5,
                holder.chk_friday.isChecked
            )
        }

        holder.chk_saturday.setOnClickListener {
            callBack.onClickWeek(
                alarmList[position],
                position,
                6,
                holder.chk_saturday.isChecked
            )
        }
    }


    private fun showMenu(v: View?, alarmModel: AlarmModel?, position: Int) {
        val popup: PopupMenu = PopupMenu(mContext, v)
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.edit_item -> {
                        callBack.onClickEdit(alarmModel, position)
                        return true
                    }

                    R.id.delete_item -> {
                        callBack.onClickRemove(alarmModel, position)
                        return true
                    }

                    else -> return false
                }
            }
        })
        popup.inflate(R.menu.manual_reminder_menu)
        popup.show()
    }


    override fun getItemCount(): Int {
        return alarmList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_remove: ImageView = itemView.findViewById<ImageView>(R.id.img_remove)
        var item_block: LinearLayout = itemView.findViewById<LinearLayout>(R.id.item_block)
        var lbl_time: TextView = itemView.findViewById<TextView>(R.id.lbl_time)
        var switch_reminder: SwitchCompat =
            itemView.findViewById<SwitchCompat>(R.id.switch_reminder)

        var chk_sunday: CheckBox = itemView.findViewById<CheckBox>(R.id.chk_sunday)
        var chk_monday: CheckBox = itemView.findViewById<CheckBox>(R.id.chk_monday)
        var chk_tuesday: CheckBox = itemView.findViewById<CheckBox>(R.id.chk_tuesday)
        var chk_wednesday: CheckBox = itemView.findViewById<CheckBox>(R.id.chk_wednesday)
        var chk_thursday: CheckBox = itemView.findViewById<CheckBox>(R.id.chk_thursday)
        var chk_friday: CheckBox = itemView.findViewById<CheckBox>(R.id.chk_friday)
        var chk_saturday: CheckBox = itemView.findViewById<CheckBox>(R.id.chk_saturday)
    }

    interface CallBack {
        fun onClickSelect(time: AlarmModel?, position: Int)
        fun onClickRemove(time: AlarmModel?, position: Int)
        fun onClickEdit(time: AlarmModel?, position: Int)
        fun onClickSwitch(time: AlarmModel?, position: Int, isOn: Boolean)
        fun onClickWeek(time: AlarmModel?, position: Int, week_pos: Int, isOn: Boolean)
    }
}