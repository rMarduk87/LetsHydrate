package rpt.tool.mementobibere.utils.view.recyclerview.items.daily

import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.databinding.ItemDailyBinding
import rpt.tool.mementobibere.utils.data.appmodel.Daily
import rpt.tool.mementobibere.utils.extensions.toStringDate
import rpt.tool.mementobibere.utils.view.BaseRecyclerViewBindingItem

class DailyItem(val daily: Daily) :
    BaseRecyclerViewBindingItem<ItemDailyBinding>(ItemDailyBinding::inflate) {

    override fun bindView(binding: ItemDailyBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)

        binding.day.text = daily.day.toStringDate()
        binding.waterLevelView.centerTitle = "${daily.quantity}%"
        binding.waterLevelView.progressValue = daily.quantity.toInt()
        val animation = if(daily.quantity == 100f){
            R.raw.trophy
        }
        else{
            R.raw.cry
        }
        binding.reached.setAnimation(animation)
    }

    override fun unbindView(binding: ItemDailyBinding) {
    }

    override val type: Int
        get() = 0
}