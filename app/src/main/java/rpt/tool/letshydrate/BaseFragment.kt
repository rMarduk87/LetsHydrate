package rpt.tool.letshydrate

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import rpt.tool.letshydrate.utils.Inflate


abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getThemeColor(ctx: Context): Int {
        return ctx.resources.getColor(R.color.colorPrimaryDark)
    }

    fun getThemeColorArray(ctx: Context?): IntArray {
        val colors = intArrayOf(Color.parseColor("#001455da"),
            Color.parseColor("#FF1455da"))

        return colors
    }
}