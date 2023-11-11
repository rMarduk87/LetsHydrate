package rpt.tool.mementobibere.utils.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import rpt.tool.mementobibere.utils.Inflate

abstract class BaseRecyclerViewBindingItem<VB : ViewBinding>(private val inflate: Inflate<VB>) :
    AbstractBindingItem<VB>() {

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): VB {
        return inflate.invoke(inflater, parent, false)
    }
}