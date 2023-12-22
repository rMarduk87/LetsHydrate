package rpt.tool.mementobibere.utils.view.recyclerview.items.daily

import android.view.View
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
        binding.waterLevelViewDark.centerTitle = "${daily.quantity}%"
        binding.waterLevelViewDark.progressValue = daily.quantity.toInt()
        binding.waterLevelViewWater.centerTitle = "${daily.quantity}%"
        binding.waterLevelViewWater.progressValue = daily.quantity.toInt()
        setVisibility(daily.theme,binding)

        val animation = if(daily.quantity >= 100f){
            R.raw.trophy
        }
        else{
            R.raw.cry
        }
        binding.reached.setAnimation(animation)
    }

    private fun setVisibility(theme: Int, binding: ItemDailyBinding) {
        when(theme){
            0->toLightTheme(binding)
            1->toDarkTheme(binding)
            2->toWaterTheme(binding)
            else->toLightTheme(binding)
        }
    }

    private fun toWaterTheme(binding: ItemDailyBinding) {
        binding.waterLevelView.visibility = View.GONE
        binding.waterLevelViewDark.visibility = View.GONE
        binding.waterLevelViewWater.visibility = View.VISIBLE
    }

    private fun toDarkTheme(binding: ItemDailyBinding) {
        binding.waterLevelView.visibility = View.GONE
        binding.waterLevelViewDark.visibility = View.VISIBLE
        binding.waterLevelViewWater.visibility = View.GONE
    }

    private fun toLightTheme(binding: ItemDailyBinding) {
        binding.waterLevelView.visibility = View.VISIBLE
        binding.waterLevelViewDark.visibility = View.GONE
        binding.waterLevelViewWater.visibility = View.GONE
    }

    override fun unbindView(binding: ItemDailyBinding) {
    }

    override val type: Int
        get() = 0
}