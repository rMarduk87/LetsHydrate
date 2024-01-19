package rpt.tool.mementobibere.utils.view.recyclerview.items.reachedGoal

import rpt.tool.mementobibere.databinding.ItemReachedGoalBinding
import rpt.tool.mementobibere.utils.data.appmodel.ReachedGoal
import rpt.tool.mementobibere.utils.extensions.toStringDate
import rpt.tool.mementobibere.utils.view.BaseRecyclerViewBindingItem

class ReachedGoalItem(val reached: ReachedGoal) :
    BaseRecyclerViewBindingItem<ItemReachedGoalBinding>(ItemReachedGoalBinding::inflate) {

    override fun bindView(binding: ItemReachedGoalBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)

        binding.day.text = reached.day.toStringDate()
        binding.qta.text = reached.quantity
        binding.unit.text = reached.unit
    }

    override fun unbindView(binding: ItemReachedGoalBinding) {
    }

    override val type: Int
        get() = 0
}


